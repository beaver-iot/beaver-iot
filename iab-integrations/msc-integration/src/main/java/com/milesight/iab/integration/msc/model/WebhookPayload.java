package com.milesight.iab.integration.msc.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.milesight.iab.context.integration.entity.annotation.IntegrationEntities;
import com.milesight.iab.context.integration.model.ExchangePayload;
import lombok.*;

import javax.annotation.Nullable;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookPayload extends ExchangePayload {

    private String eventId;

    private Long eventCreatedTime;

    private String eventVersion;

    private String eventType;

    @Nullable
    private JsonNode data;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceData {

        private Profile deviceProfile;

        private String type;

        private String tslId;

        @Nullable
        private JsonNode payload;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Profile {

            private Long deviceId;

            private String sn;

            private String devEUI;

            private String name;

            private String communicationMethod;

            private String model;
        }
    }

}
