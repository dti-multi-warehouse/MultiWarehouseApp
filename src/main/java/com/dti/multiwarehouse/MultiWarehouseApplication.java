package com.dti.multiwarehouse;

import com.dti.multiwarehouse.config.CloudinaryConfigProperties;
import com.dti.multiwarehouse.config.MidtransConfigProperties;
import com.dti.multiwarehouse.config.RsaKeyProperties;
import com.dti.multiwarehouse.config.TypeSenseConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableConfigurationProperties({
        CloudinaryConfigProperties.class,
        RsaKeyProperties.class,
        TypeSenseConfigProperties.class,
        MidtransConfigProperties.class
})
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class MultiWarehouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiWarehouseApplication.class, args);
    }

}
