package com.dti.multiwarehouse.cron;

import com.dti.multiwarehouse.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CronJob {

    private final OrderService orderService;

    @Scheduled(cron = "* */30 * * * *")
    public void execute() {
        orderService.autoCancelOrder();
        orderService.autoFinalizeOrder();
    }
}
