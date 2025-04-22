package com.saz.se.goat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResendOTPRequest {
    private String email;
    private String phoneNo;
}
