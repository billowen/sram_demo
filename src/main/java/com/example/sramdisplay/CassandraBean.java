package com.example.sramdisplay;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CassandraBean {
    @Bean
    public Session session() {
        Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
        return cluster.connect("sram_test");
    }
}
