package com.milesight.iab.integration.msc.controller;

import com.milesight.iab.integration.msc.model.WebhookPayload;
import com.milesight.iab.integration.msc.service.MscWebhookService;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/public/integration/msc")
public class MscIntegrationPublicController {

    @Autowired
    private MscWebhookService mscWebhookService;

    @PostMapping("/webhook")
    public String webhook(@RequestHeader(name = "x-msc-request-signature") String signature,
                          @RequestHeader(name = "x-msc-webhook-uuid") String webhookUuid,
                          @RequestHeader(name = "x-msc-request-timestamp") String requestTimestamp,
                          @RequestHeader(name = "x-msc-request-nonce") String requestNonce,
                          @RequestBody List<WebhookPayload> webhookPayloads) {
        mscWebhookService.handleWebhookData(signature, webhookUuid, requestTimestamp, requestNonce, webhookPayloads);
        return "success";
    }

}
