package com.milesight.iab.entity.model.response;

import lombok.Data;

import java.util.List;

/**
 * @author loong
 * @date 2024/10/21 10:51
 */
@Data
public class EntityHistoryResponse {

    private List<HistoryData> historyData;

    public static class HistoryData {

        private String timestamp;
        private String value;

    }
}
