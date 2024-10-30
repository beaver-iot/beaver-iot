package com.milesight.iab.integration.msc.constant;

public interface MscIntegrationConstants {

    String INTEGRATION_IDENTIFIER = "msc-integration";

    interface InternalPropertyKey {

        String LAST_SYNC_TIME = "_#last_sync_time#_";

        static String getLastSyncTimeKey(String deviceKey) {
            return String.format("%s.%s", deviceKey, LAST_SYNC_TIME);
        }

    }

}
