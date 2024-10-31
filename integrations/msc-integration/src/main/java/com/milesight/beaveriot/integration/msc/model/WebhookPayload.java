package com.milesight.beaveriot.integration.msc.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import javax.annotation.Nullable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookPayload {

    @JsonAlias("eventId")
    private String eventId;

    @JsonAlias("eventCreatedTime")
    private Long eventCreatedTime;

    @JsonAlias("eventVersion")
    private String eventVersion;

    @JsonAlias("eventType")
    private String eventType;

    @Nullable
    private JsonNode data;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceData {

        @JsonAlias("deviceProfile")
        private Profile deviceProfile;

        private String type;

        @JsonAlias("tslId")
        private String tslId;

        @Nullable
        private JsonNode payload;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Profile {

            @JsonAlias("deviceId")
            private Long deviceId;

            private String sn;

            @JsonAlias("devEUI")
            private String devEUI;

            private String name;

            @JsonAlias("communicationMethod")
            private String communicationMethod;

            private String model;
        }
    }

}
