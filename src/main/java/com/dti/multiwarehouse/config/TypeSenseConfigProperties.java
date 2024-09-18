package com.dti.multiwarehouse.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "typesense")
public class TypeSenseConfigProperties {
    private String url;
    private String adminKey;
}
