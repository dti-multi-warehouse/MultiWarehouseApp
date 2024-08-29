package com.dti.multiwarehouse;

import com.dti.multiwarehouse.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
@EnableCaching
public class MultiWarehouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiWarehouseApplication.class, args);
    }

}
