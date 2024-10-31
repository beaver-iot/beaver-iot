package com.milesight.beaveriot.context.integration.model;

/**
 * DeviceBuilder is a builder class for Device, eg:
 *         Entity entityConfig = new EntityBuilder()
 *                 .property("humidity", AccessMod.RW)
 *                 .identifier("humidity")
 *                     .children()
 *                         .property("value", AccessMod.RW)
 *                         .end()
 *                     .children()
 *                         .property("unit", AccessMod.RW)
 *                         .end()
 *                     .children()
 *                         .property("timestamp", AccessMod.RW)
 *                         .end()
 *                 .build();
 *
 *         Device device = new DeviceBuilder("myIntegrationId"，"myIntegration")
 *                             .name("myDevice")
 *                             .identifier("mySN")
 *                             .entity(entityConfig)
 *                             .build();
 * @author leon
 */
public class DeviceBuilder extends BaseDeviceBuilder<DeviceBuilder>{

    private IntegrationBuilder integrationBuilder;

    public DeviceBuilder(Integration integration){
        super(integration);
    }

    public DeviceBuilder(String integrationId, String integrationName){
        super(integrationId, integrationName);
    }

    public DeviceBuilder(){
    }

    public static class IntegrationDeviceBuilder extends BaseDeviceBuilder<IntegrationDeviceBuilder>{
        protected IntegrationBuilder integrationBuilder;
        public IntegrationDeviceBuilder(IntegrationBuilder integrationBuilder) {
            super(integrationBuilder.integration);
            this.integrationBuilder = integrationBuilder;
        }

        public IntegrationBuilder end() {
            integrationBuilder.integration.addInitialDevice(build());
            return integrationBuilder;
        }
    }

}
