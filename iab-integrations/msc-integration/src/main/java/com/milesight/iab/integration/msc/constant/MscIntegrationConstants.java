package com.milesight.iab.integration.msc.constant;

public interface MscIntegrationConstants {

    String INTEGRATION_IDENTIFIER = "msc-integration";

    interface OpenapiStatus {

        String CONNECTED = "CONNECTED";

        String NOT_CONNECTED = "NOT_CONNECTED";

        String ERROR = "ERROR";

    }

    interface WebhookStatus {

        String READY = "READY";

        String NOT_READY = "NOT_READY";

        String ERROR = "ERROR";

    }

    interface InternalPropertyKey {

        String LAST_SYNC_TIME = "last_sync_time";

        static String getLastSyncTimeKey(String deviceKey) {
            return String.format("%s.%s", deviceKey, LAST_SYNC_TIME);
        }

    }

}
