package com.dti.multiwarehouse.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "cloudinary")
public class CloudinaryConfigProperties {
    private String cloudName;
    private String apiKey;
    private String apiSecret;
    private String url;
}

