package com.saz.se.goat.fcm;

import com.saz.se.goat.model.FcmTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fcmtokenresgistration")
public class FcmTokenController {

    @Autowired
    private FcmTokenService fcmTokenService;

    @PostMapping
    public ResponseEntity<String> registerToken(@RequestBody FcmTokenRequest request) {
        fcmTokenService.saveToken(request);
        return ResponseEntity.ok("Token registered");
    }
}
