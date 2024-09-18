package com.dti.multiwarehouse.config;
import com.midtrans.Config;
import com.midtrans.ConfigFactory;
import com.midtrans.service.MidtransCoreApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MidtransConfig {

    @Value("${midtrans.client-key}")
    private String clientKey;

    @Value("${midtrans.server-key}")
    private String serverKey;

    @Bean
    public MidtransCoreApi midtransCoreApi() {
        Config coreApiConfigOptions = Config.builder()
                .setClientKey(clientKey)
                .setServerKey(serverKey)
                .setIsProduction(false)
                .build();

        return new ConfigFactory(coreApiConfigOptions).getCoreApi();
    }
}
