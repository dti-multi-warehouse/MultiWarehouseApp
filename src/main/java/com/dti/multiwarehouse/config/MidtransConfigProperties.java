package com.dti.multiwarehouse.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "midtrans")
public class MidtransConfigProperties {
    private String clientKey;
    private String serverKey;
}
