package br.senai.sp.informatica.trevobk.propostas.components.services;

import io.quarkus.arc.Arc;
import io.quarkus.flyway.runtime.FlywayContainer;
import io.quarkus.flyway.runtime.FlywayContainerProducer;
import io.quarkus.flyway.runtime.QuarkusPathLocationScanner;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.Flyway;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import java.util.List;

@ApplicationScoped
public class RunFlyway {
    @Inject
    private Logger logger;
    @ConfigProperty(name = "quarkus.flyway.migrate-at-start")
    boolean runMigration;
    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    String datasourceUrl;
    @ConfigProperty(name = "quarkus.datasource.username")
    String datasourceUsername;
    @ConfigProperty(name = "quarkus.datasource.password")
    String datasourcePassword;
    @ConfigProperty(name = "quarkus.native.resources.includes")
    List<String> files;

    public void runFlywayMigration(@Observes StartupEvent event) {
        if (runMigration) {
            logger.info("int flyway ...");
            QuarkusPathLocationScanner.setApplicationMigrationFiles(files);
            FlywayContainerProducer flywayProducer = Arc.container().instance(FlywayContainerProducer.class).get();
            FlywayContainer flywayContainer = flywayProducer.createFlyway(Flyway.configure()
                .validateOnMigrate(true)
                .validateMigrationNaming(true)
                .dataSource(datasourceUrl, datasourceUsername, datasourcePassword).getDataSource(), "<default>", true, true);
            Flyway flyway = flywayContainer.getFlyway();
            flyway.migrate();
        }
    }
}
