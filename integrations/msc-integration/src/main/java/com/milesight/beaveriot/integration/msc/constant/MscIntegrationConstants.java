package com.milesight.beaveriot.integration.msc.constant;

public interface MscIntegrationConstants {

    String INTEGRATION_IDENTIFIER = "msc-integration";

    interface DeviceAdditionalDataName {

        String DEVICE_ID = "device_id";

    }

    interface InternalPropertyIdentifier {

        interface Pattern {
            String PREFIX = "_#";
            String SUFFIX = "#_";
            String TEMPLATE = "_#%s#_";

            static boolean match(String key) {
                return key.startsWith(PREFIX) && key.endsWith(SUFFIX);
            }
        }

        String LAST_SYNC_TIME = "_#last_sync_time#_";

        static String getLastSyncTimeKey(String deviceKey) {
            return String.format("%s.%s", deviceKey, LAST_SYNC_TIME);
        }

    }

}
