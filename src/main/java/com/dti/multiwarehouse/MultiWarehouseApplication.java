package com.dti.multiwarehouse;

import com.dti.multiwarehouse.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class MultiWarehouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiWarehouseApplication.class, args);
    }

}
