package com.milesight.iab.integration.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.milesight.iab.base.enums.ErrorCode;
import com.milesight.iab.base.exception.ServiceException;
import com.milesight.iab.base.utils.JsonUtils;
import com.milesight.iab.context.api.DeviceServiceProvider;
import com.milesight.iab.context.api.EntityServiceProvider;
import com.milesight.iab.context.api.IntegrationServiceProvider;
import com.milesight.iab.context.integration.enums.AttachTargetType;
import com.milesight.iab.context.integration.model.Entity;
import com.milesight.iab.context.integration.model.Integration;
import com.milesight.iab.integration.model.request.SearchIntegrationRequest;
import com.milesight.iab.integration.model.response.IntegrationDetailData;
import com.milesight.iab.integration.model.response.IntegrationEntityData;
import com.milesight.iab.integration.model.response.SearchIntegrationResponseData;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IntegrationService {
    @Autowired
    IntegrationServiceProvider integrationServiceProvider;

    @Autowired
    DeviceServiceProvider deviceServiceProvider;

    @Autowired
    EntityServiceProvider entityServiceProvider;

    private SearchIntegrationResponseData integrationToSearchResponseData(Integration integration) {
        SearchIntegrationResponseData data = new SearchIntegrationResponseData();
        data.setId(integration.getId());
        data.setIcon(integration.getIconUrl());
        data.setName(integration.getName());
        data.setDescription(integration.getDescription());
        data.setAddDeviceServiceKey(integration.getEntityKeyAddDevice());
        return data;
    }

    public List<SearchIntegrationResponseData> searchIntegration(SearchIntegrationRequest searchDeviceRequest) {
        List<Integration> integrations = integrationServiceProvider.findAllIntegrations().stream().toList();
        if (integrations.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> integrationIds = integrations.stream().map(Integration::getId).toList();
        Map<String, Long> integrationDeviceCount = deviceServiceProvider.countByIntegrationIds(integrationIds);
        Map<String, Long> integrationEntityCount = entityServiceProvider.countAllEntitiesByIntegrationIds(integrationIds);
        return integrations
                .stream()
                .filter(integration -> {
            if (searchDeviceRequest.getDeviceAddable() != null) {
                Boolean canAddDevice = integration.getEntityIdentifierAddDevice() != null;
                if (!searchDeviceRequest.getDeviceAddable().equals(canAddDevice)) {
                    return false;
                }
            }

            if (searchDeviceRequest.getDeviceDeletable() != null) {
                Boolean canDeleteDevice = integration.getEntityIdentifierDeleteDevice() != null;
                if (!searchDeviceRequest.getDeviceDeletable().equals(canDeleteDevice)) {
                    return false;
                }
            }

            return true;
        }).map(integration -> {
            SearchIntegrationResponseData data = this.integrationToSearchResponseData(integration);
            data.setDeviceCount(integrationDeviceCount.get(integration.getId()));
            data.setEntityCount(integrationEntityCount.get(integration.getId()));
            return data;
        }).collect(Collectors.toList());
    }

    public IntegrationDetailData getDetailData(String integrationId) {
        Integration integration = integrationServiceProvider.getIntegration(integrationId);
        if (integration == null) {
            throw ServiceException
                    .with(ErrorCode.DATA_NO_FOUND)
                    .detailMessage("Integration " + integrationId + " not found!")
                    .build();
        }

        IntegrationDetailData data = new IntegrationDetailData();
        BeanUtils.copyProperties(integrationToSearchResponseData(integration), data);
        data.setDeviceCount(deviceServiceProvider.countByIntegrationId(integrationId));
        data.setEntityCount(entityServiceProvider.countAllEntitiesByIntegrationId(integrationId));
        List<Entity> entities = entityServiceProvider.findByTargetId(AttachTargetType.INTEGRATION, integrationId);
        JsonNode entityValues = entityServiceProvider.findExchangeValuesByKeys(entities.stream().map(Entity::getKey).toList());
        if (entityValues == null) {
            entityValues = JsonUtils.getObjectMapper().createObjectNode();
        }

        final JsonNode finalEntityValues = entityValues;
        data.setIntegrationEntities(entities
                .stream().flatMap((Entity pEntity) -> {
                    ArrayList<Entity> flatEntities = new ArrayList<>();
                    flatEntities.add(pEntity);

                    List<Entity> childrenEntities = pEntity.getChildren();
                    if (childrenEntities != null) {
                        flatEntities.addAll(childrenEntities);
                    }

                    return flatEntities.stream().map(entity -> IntegrationEntityData
                            .builder()
                            .id(entity.getId().toString())
                            .key(entity.getKey())
                            .type(entity.getType())
                            .valueType(entity.getValueType())
                            .valueAttribute(entity.getAttributes())
                            .value(finalEntityValues.get(entity.getKey()))
                            .build());
                }).toList());
        return data;
    }
}
