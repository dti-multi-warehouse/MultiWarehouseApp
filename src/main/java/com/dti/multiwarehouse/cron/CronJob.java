package com.dti.multiwarehouse.cron;

import com.dti.multiwarehouse.order.service.OrderService;
import com.dti.multiwarehouse.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CronJob {

    private final OrderService orderService;
    private final ProductService productService;

    @Scheduled(cron = "* */30 * * * *")
    public void execute() {
        orderService.autoCancelOrder();
        orderService.autoFinalizeOrder();

        productService.syncStockWithTypeSense();
    }
}
