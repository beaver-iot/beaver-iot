package com.milesight.beaveriot.integration.msc.entity;

import com.milesight.beaveriot.context.integration.entity.annotation.Attribute;
import com.milesight.beaveriot.context.integration.entity.annotation.Entities;
import com.milesight.beaveriot.context.integration.entity.annotation.Entity;
import com.milesight.beaveriot.context.integration.entity.annotation.IntegrationEntities;
import com.milesight.beaveriot.context.integration.enums.EntityType;
import com.milesight.beaveriot.context.integration.model.ExchangePayload;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IntegrationEntities
public class MscServiceEntities extends ExchangePayload {

    @Entity(type = EntityType.SERVICE)
    private AddDevice addDevice;

    @Entity(type = EntityType.SERVICE)
    private SyncDevice syncDevice;

    @Entity(type = EntityType.SERVICE)
    private DeleteDevice deleteDevice;

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Entities
    public static class AddDevice extends ExchangePayload {

        @Entity(attributes = {@Attribute(minLength = 12, maxLength = 16)})
        private String sn;

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @NoArgsConstructor
    @Entities
    public static class DeleteDevice extends ExchangePayload {

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @NoArgsConstructor
    @Entities
    public static class SyncDevice extends ExchangePayload {

    }

}
