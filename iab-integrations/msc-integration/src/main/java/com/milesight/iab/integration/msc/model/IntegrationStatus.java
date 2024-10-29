package com.milesight.iab.integration.msc.model;

import lombok.*;


@Getter
@RequiredArgsConstructor
public enum IntegrationStatus {
    READY,
    NOT_READY,
    ERROR,
    ;

    @Override
    public String toString() {
        return name();
    }
}
