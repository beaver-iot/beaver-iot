package com.milesight.iab.integration.msc.entity;

import com.milesight.iab.base.utils.StringUtils;
import com.milesight.iab.context.integration.entity.annotation.Entities;
import com.milesight.iab.context.integration.entity.annotation.Entity;
import com.milesight.iab.context.integration.entity.annotation.IntegrationEntities;
import com.milesight.iab.context.integration.enums.AccessMod;
import com.milesight.iab.context.integration.model.ExchangePayload;
import com.milesight.iab.integration.msc.constant.MscIntegrationConstants;
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
     * CONNECTED<br/>
     * NOT_CONNECTED<br/>
     * ERROR<br/>
     */
    @Entity(accessMod = AccessMod.R)
    private String openapiStatus;

    @Entity(accessMod = AccessMod.R)
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

        @Entity
        private String serverUrl;

        @Entity
        private String clientId;

        @Entity
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

        @Entity
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

        @Entity
        private Integer period;

    }

}
