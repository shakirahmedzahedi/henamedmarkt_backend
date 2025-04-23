package com.saz.se.goat.otp;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class FCMService {

    public void sendOtpNotification(String fcmToken, String otp, String phone) {
        // Create the FCM message
        Message message = Message.builder()
                .setToken(fcmToken)
                /*.setNotification(Notification.builder()
                        .setTitle("Your OTP Code")
                        .setBody("Your OTP is: " + otp)
                        .build())*/
                .putData("otp", otp)
                .putData("phoneNumber", phone)
                .build();

        try {
            System.out.println(" message: " + message.toString());
            // Send the message using Firebase Admin SDK
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            System.err.println("Error sending FCM message: " + e.getMessage());
        }
    }
}
