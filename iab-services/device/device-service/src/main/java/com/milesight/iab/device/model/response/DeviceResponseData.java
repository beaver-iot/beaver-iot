package com.milesight.iab.device.model.response;

import com.milesight.iab.device.po.DevicePO;
import lombok.*;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
public class DeviceResponseData extends DevicePO {
    private String integrationName;
    private Boolean deletable;
}
