package com.milesight.iab.integration.msc.entity;

import com.milesight.iab.context.integration.entity.annotation.Entities;
import com.milesight.iab.context.integration.entity.annotation.Entity;
import com.milesight.iab.context.integration.entity.annotation.IntegrationEntities;
import com.milesight.iab.context.integration.enums.EntityType;
import com.milesight.iab.context.integration.model.ExchangePayload;
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
    private DeleteDevice deleteDevice;

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Entities
    public static class AddDevice extends ExchangePayload {

        @Entity
        private String sn;

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @NoArgsConstructor
    @Entities
    public static class DeleteDevice extends ExchangePayload {

    }

}
