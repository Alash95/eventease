package com.alash.eventease.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
    @lombok.Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Data {

        @JsonProperty("name")
        private String name;

        @JsonProperty("amount")
        private String amount;

        @JsonProperty("interval")
        private String interval;

        @JsonProperty("integration")
        private String integration;

        @JsonProperty("plan_code")
        private String planCode;

        @JsonProperty("send_invoices")
        private String sendInvoices;

        @JsonProperty("send_sms")
        private String sendSms;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("id")
        private String id;

        @JsonProperty("createdAt")
        private String createdAt;

        @JsonProperty("updatedAt")
        private String updatedAt;

    }

