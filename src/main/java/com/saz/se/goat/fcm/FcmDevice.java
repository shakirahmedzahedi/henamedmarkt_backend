package com.saz.se.goat.fcm;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class FcmDevice {
    @Id
    @GeneratedValue
    private Long id;

    private String device;
    private String fcmToken;
}