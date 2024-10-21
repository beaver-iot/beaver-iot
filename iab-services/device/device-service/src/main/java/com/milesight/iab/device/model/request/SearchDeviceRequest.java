package com.milesight.iab.device.model.request;

import com.milesight.iab.base.page.GenericPageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SearchDeviceRequest extends GenericPageRequest {
    private String name;
}
