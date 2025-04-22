package com.saz.se.goat.model;

import lombok.Data;

@Data
public class FcmTokenRequest {
    private String device;
    private String fcmToken;
}