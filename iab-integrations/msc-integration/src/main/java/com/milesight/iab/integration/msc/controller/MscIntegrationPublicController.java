package com.milesight.iab.integration.msc.controller;

import com.milesight.iab.integration.msc.model.WebhookPayload;
import com.milesight.iab.integration.msc.service.MscWebhookService;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/public/integration/msc")
public class MscIntegrationPublicController {

    @Autowired
    private MscWebhookService mscWebhookService;

    @PostMapping("/webhook")
    public String webhook(@RequestHeader(name = "x-msc-request-signature") String signature,
                          @RequestHeader(name = "x-msc-webhook-uuid") String webhookUuid,
                          @RequestHeader(name = "x-msc-request-timestamp") String requestTimestamp,
                          @RequestHeader(name = "x-msc-request-nonce") String requestNonce,
                          @RequestBody WebhookPayload webhookPayload) {
        mscWebhookService.handleWebhookData(signature, webhookUuid, requestTimestamp, requestNonce, webhookPayload);
        return "success";
    }

}
