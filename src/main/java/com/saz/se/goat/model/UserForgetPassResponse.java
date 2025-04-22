package com.saz.se.goat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserForgetPassResponse {
    private String email;
    private String phoneNo;
}
