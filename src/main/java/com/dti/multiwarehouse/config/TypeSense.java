package com.dti.multiwarehouse.config;

import org.springframework.context.annotation.Bean;
import org.typesense.api.*;
import org.typesense.resources.*;

import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class TypeSense {


    @Bean
    public Client client() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(
            new Node(
                    "http",
                    "localhost",
                    "8108"
            )
        );

       org.typesense.api.Configuration configuration = new org.typesense.api.Configuration(nodes, Duration.ofSeconds(2),"xyz");

        return new Client(configuration);
    }
}
