package br.senai.sp.informatica.produtos.tests;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.jboss.logging.Logger;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.HashMap;
import java.util.Map;

public class ProdutoTestLifecycleManager implements QuarkusTestResourceLifecycleManager {
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:12.12");

    private Logger logger = Logger.getLogger(ProdutoTestLifecycleManager.class);

    @Override
    public Map<String, String> start() {
        POSTGRES.start();
        HashMap<String, String> properties = new HashMap<>();
        properties.put("quarkus.datasource.jdbc.url",  "jdbc:postgresql://localhost:5432/postgres"); // POSTGRES.getJdbcUrl());
        logger.error("URL: " + POSTGRES.getJdbcUrl());
        properties.put("quarkus.datasource.username", "produtos"); // POSTGRES.getUsername());
        logger.error("UID: " + POSTGRES.getUsername());
        properties.put("quarkus.datasource.password", "produtos"); // POSTGRES.getPassword());
        logger.error("PWD: " + POSTGRES.getPassword());

        return properties;
    }

    @Override
    public void stop() {
        if(POSTGRES.isRunning())
            POSTGRES.stop();
    }
}
