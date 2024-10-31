package com.milesight.beaveriot.integration.msc.entity;

import com.milesight.beaveriot.base.utils.StringUtils;
import com.milesight.beaveriot.context.integration.entity.annotation.Attribute;
import com.milesight.beaveriot.context.integration.entity.annotation.Entities;
import com.milesight.beaveriot.context.integration.entity.annotation.Entity;
import com.milesight.beaveriot.context.integration.entity.annotation.IntegrationEntities;
import com.milesight.beaveriot.context.integration.enums.AccessMod;
import com.milesight.beaveriot.context.integration.model.ExchangePayload;
import com.milesight.beaveriot.integration.msc.constant.MscIntegrationConstants;
import com.milesight.beaveriot.integration.msc.model.IntegrationStatus;
import lombok.*;
import lombok.experimental.*;

@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IntegrationEntities
public class MscConnectionPropertiesEntities extends ExchangePayload {

    public static String getKey(String propertyKey) {
        return MscIntegrationConstants.INTEGRATION_IDENTIFIER + ".integration." + StringUtils.toSnakeCase(propertyKey);
    }

    /**
     * The status of the connection.<br/>
     * Possible values:<br/>
     * READY<br/>
     * NOT_READY<br/>
     * ERROR<br/>
     */
    @Entity(accessMod = AccessMod.R, attributes = {@Attribute(enumClass = IntegrationStatus.class)})
    private String openapiStatus;

    @Entity(accessMod = AccessMod.R, attributes = {@Attribute(enumClass = IntegrationStatus.class)})
    private String webhookStatus;

    @Entity
    private Openapi openapi;

    @Entity
    private Webhook webhook;

    @Entity
    private ScheduledDataFetch scheduledDataFetch;

    @FieldNameConstants
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Entities
    public static class Openapi extends ExchangePayload {

        @Entity(attributes = {@Attribute(minLength = 1)})
        private String serverUrl;

        @Entity(attributes = {@Attribute(minLength = 1)})
        private String clientId;

        @Entity(attributes = {@Attribute(minLength = 1)})
        private String clientSecret;

    }

    @FieldNameConstants
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Entities
    public static class Webhook extends ExchangePayload {

        @Entity
        private Boolean enabled;

        @Entity(attributes = {@Attribute(minLength = 1)})
        private String secretKey;

    }

    @FieldNameConstants
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Entities
    public static class ScheduledDataFetch extends ExchangePayload {

        @Entity
        private Boolean enabled;

        @Entity(attributes = {@Attribute(min = 30, max = 86400)})
        private Integer period;

    }

}
