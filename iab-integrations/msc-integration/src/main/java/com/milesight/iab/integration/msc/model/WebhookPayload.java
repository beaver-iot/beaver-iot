package com.milesight.iab.integration.msc.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import javax.annotation.Nullable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookPayload {

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
