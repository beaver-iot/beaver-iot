package com.milesight.beaveriot.demo.provider;

import com.milesight.beaveriot.context.api.IntegrationServiceProvider;
import com.milesight.beaveriot.context.integration.model.Integration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author leon
 */
@Slf4j
@Component
public class JpaIntegrationStorageProvider extends IntegrationServiceProvider {

    @Override
    public void save(Integration integrationConfig) {
        log.info("save integration config: {}", integrationConfig);
    }

    @Override
    public void batchSave(Collection<Integration> integrationConfig) {
        log.info("batch save integration config: {}", integrationConfig);
        integrationConfig.forEach(integration -> {
            log.info("==========integration: {}===========", integration.getName());
            integration.getInitialDevices().forEach(device -> {
                log.info("device: {}", device.getKey());
                device.getEntities().forEach(entity -> {
                    log.info("entity: {}", entity.getKey());
                    if(entity.getChildren() != null){
                        entity.getChildren().forEach(child -> {
                            log.info("entity child: {}", child.getKey());
                        });
                    }
                });
            });
        });
    }
}