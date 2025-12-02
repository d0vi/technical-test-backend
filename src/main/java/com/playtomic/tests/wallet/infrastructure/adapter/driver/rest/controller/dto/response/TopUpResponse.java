package com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TopUpResponse(@JsonProperty("payment_id") String paymentId) {}
