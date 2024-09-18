package com.dti.multiwarehouse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.typesense.api.*;
import org.typesense.resources.*;

import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class TypeSense {
    @Value("${TYPESENSE_URL}")
    private String url;

    @Value("${TYPESENSE_ADMIN_KEY}")
    private String adminKey;

    @Bean
    public Client client() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(
            new Node(
                    "https",
                    url,
                    "443"
            )
        );

       var configuration = new org.typesense.api.Configuration(nodes, Duration.ofSeconds(2),adminKey);

        return new Client(configuration);
    }
}
