package com.dti.multiwarehouse.config;
import com.midtrans.Config;
import com.midtrans.ConfigFactory;
import com.midtrans.service.MidtransCoreApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MidtransConfig {


    @Bean
    public MidtransCoreApi midtransCoreApi() {
        Config coreApiConfigOptions = Config.builder()
                .setClientKey("SB-Mid-client-bsCFuKRKeh79Gi7T")
                .setServerKey("SB-Mid-server-W96jBLEV6Dfhu4m22IHt-C-T")
                .setIsProduction(false)
                .build();

        return new ConfigFactory(coreApiConfigOptions).getCoreApi();
    }
}
