package com.dti.multiwarehouse.stock.service.impl;

import com.dti.multiwarehouse.exceptions.InsufficientStockException;
import com.dti.multiwarehouse.order.dto.request.CreateOrderRequestDto;
import com.dti.multiwarehouse.product.service.ProductService;
import com.dti.multiwarehouse.stock.dao.Stock;
import com.dti.multiwarehouse.stock.dao.StockMutation;
import com.dti.multiwarehouse.stock.dao.enums.StockMutStatus;
import com.dti.multiwarehouse.stock.dao.key.StockCompositeKey;
import com.dti.multiwarehouse.stock.dto.request.RequestMutationRequestDto;
import com.dti.multiwarehouse.stock.dto.request.RestockRequestDto;
import com.dti.multiwarehouse.stock.repository.StockMutationRepository;
import com.dti.multiwarehouse.stock.repository.StockRepository;
import com.dti.multiwarehouse.stock.service.StockService;
import com.dti.multiwarehouse.warehouse.dao.Warehouse;
import com.dti.multiwarehouse.warehouse.service.WarehouseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    private final StockMutationRepository stockMutationRepository;

    private final WarehouseService warehouseService;
    private final ProductService productService;

    @Override
    public void restock(RestockRequestDto requestDto) {
        var product = productService.findProductById(requestDto.getProductId());
        var warehouseTo = warehouseService.findWarehouseById(requestDto.getWarehouseToId());
        var stockMutation = StockMutation.builder()
                .product(product)
                .warehouseFrom(null)
                .warehouseTo(warehouseTo)
                .quantity(requestDto.getQuantity())
                .status(StockMutStatus.COMPLETED)
                .build();
        stockMutationRepository.save(stockMutation);
        calculateStock(requestDto.getProductId(), requestDto.getWarehouseToId());
    }

    @Override
    public void requestStockMutation(RequestMutationRequestDto requestDto) {
        var product = productService.findProductById(requestDto.getProductId());
        var warehouseTo = warehouseService.findWarehouseById(requestDto.getWarehouseToId());
        var warehouseFrom = warehouseService.findWarehouseById(requestDto.getWarehouseFromId());
        var stockMutation = StockMutation.builder()
                .product(product)
                .warehouseFrom(warehouseFrom)
                .warehouseTo(warehouseTo)
                .quantity(requestDto.getQuantity())
                .status(StockMutStatus.AWAITING_CONFIRMATION)
                .build();
        stockMutationRepository.save(stockMutation);
        calculateStock(requestDto.getProductId(), requestDto.getWarehouseToId(), requestDto.getWarehouseFromId());
    }

    @Override
    public void acceptStockMutation(Long stockMutationId) {
        mutateStock(stockMutationId, StockMutStatus.COMPLETED);
    }

    @Override
    public void cancelStockMutation(Long stockMutationId) {
        mutateStock(stockMutationId, StockMutStatus.CANCELLED);
    }

    @Override
    public void rejectStockMutation(Long stockMutationId) {
        mutateStock(stockMutationId, StockMutStatus.REJECTED);
    }

    @Override
    public void processOrder(CreateOrderRequestDto createOrderRequestDto) {
        var closestWarehouse = warehouseService.findWarehouseById(1L);
        var items = createOrderRequestDto.getItems();
        for (var item : items) {
            checkStockAvailability(item.getProductId(), closestWarehouse, item.getQuantity());
        }
    }

    private void checkStockAvailability(Long productId, Warehouse warehouse, int quantity) {
        var product = productService.findProductById(productId);
        if (product.getStock() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + productId);
        }
        var stockOptional = stockRepository.findById(new StockCompositeKey(product, warehouse));
        if (stockOptional.isEmpty()) {
            autoMutateStock(productId, warehouse.getId(), quantity);
        }
        if (stockOptional.isPresent()) {
            var stock = stockOptional.get();
            if (stock.getStock() < quantity) {
                autoMutateStock(productId, warehouse.getId(), quantity - stock.getStock());
            }
        }
    }

    private void autoMutateStock(Long productId, Long warehouseId, int quantity) {
        int amount = 0;
        int count = 0;
        while (amount <= quantity) {
            amount++;
        }
    }

    private void mutateStock(Long stockMutationId, StockMutStatus status) {
        var stockMutation = stockMutationRepository
                .findById(stockMutationId)
                .orElseThrow(() -> new EntityNotFoundException("Stock mutation with id " + stockMutationId + " not found"));
        stockMutation.setStatus(status);
        stockMutationRepository.save(stockMutation);
        calculateStock(stockMutation.getProduct().getId(), stockMutation.getWarehouseTo().getId(), stockMutation.getWarehouseFrom().getId());
    }

    private void calculateStock(Long productId, Long warehouseId) {
        createStockIfNotExist(productId, warehouseId);
        stockMutationRepository.calculateProductStock(productId);
        stockMutationRepository.calculateWarehouseStock(productId, warehouseId);
    }

    private void calculateStock(Long productId, Long warehouseToId, Long warehouseFromId) {
        createStockIfNotExist(productId, warehouseToId);
        stockMutationRepository.calculateWarehouseStock(productId, warehouseToId);
        stockMutationRepository.calculateWarehouseStock(productId, warehouseFromId);
    }

    private void createStockIfNotExist(Long productId, Long warehouseId) {
        var product = productService.findProductById(productId);
        var warehouse = warehouseService.findWarehouseById(warehouseId);
        var key = new StockCompositeKey();
        key.setProduct(product);
        key.setWarehouse(warehouse);
        var stock = stockRepository.findById(key);
        if (stock.isEmpty()) {
            var newStock = new Stock(key, 0);
            stockRepository.save(newStock);
        }
    }
}
