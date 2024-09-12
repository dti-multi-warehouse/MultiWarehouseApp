package com.dti.multiwarehouse.stock.service.impl;

import com.dti.multiwarehouse.cart.dto.CartItem;
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
import com.dti.multiwarehouse.warehouse.service.WarehouseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void processOrder(Long warehouseId, List<CartItem> cartItems) {
        var closestWarehouse = warehouseService.findWarehouseById(warehouseId);
        Map<Long, Integer> productQuantities = new HashMap<>();

        for (var item : cartItems) {
            productQuantities.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }

        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            var productId = entry.getKey();
            var requiredQuantity = entry.getValue();

            var product = productService.findProductById(productId);
            var stockOptional = stockRepository.findById(new StockCompositeKey(product, closestWarehouse));

            int quantityToMutate = stockOptional
                    .map(stock -> Math.max(0, requiredQuantity - stock.getStock()))
                    .orElse(requiredQuantity);

            if (quantityToMutate > 0) {
                var isFulfilled = autoMutateStock(productId, warehouseId, quantityToMutate);
                if (!isFulfilled) {
                    greedyAutoMutateStock(productId, warehouseId, quantityToMutate);
                }
            }
        }
    }

    private boolean autoMutateStock(Long productId, Long warehouseId, int quantity) {
//        find the nearest warehouses from current warehouse -> List of warehouses?
//        check their stock
//        if they have enough, make the mutation
        return false;
    }

    private void greedyAutoMutateStock(Long productId, Long warehouseId, int quantity) {
//        find the nearest warehouses from current warehouse -> List of warehouses?
//        Create an accumulator for the quantities
//        check their stock
//        if they have any, make the mutation
//        continue until accumulator == quantity
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
