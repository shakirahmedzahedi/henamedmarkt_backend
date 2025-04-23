package com.saz.se.goat.otp;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private final Map<String, OtpEntry> otpStorage = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public String generateOtp(String phoneNumber, String email) {
        String otp = String.format("%06d", random.nextInt(999999));
        OtpEntry entry = otpStorage.get(phoneNumber);

        if (entry != null) {
            entry.update(otp, LocalDateTime.now().plusMinutes(5));
        }
        else {
            otpStorage.put(phoneNumber, new OtpEntry(otp, email, LocalDateTime.now().plusMinutes(5))); // Valid for 5 minutes
        }
        System.out.println(otp);
        return otp;
    }

    public String verifyOtp(String phoneNumber, String otp) {
        OtpEntry entry = otpStorage.get(phoneNumber);
        if (entry == null) return "Expire";
        if(entry.otp.equals(otp)){
            if (LocalDateTime.now().isAfter(entry.expiryTime)) return "Expire";
            return entry.email;
        }
        else {
            return "Expire";
        }
    }

    private static class OtpEntry {
        String otp;
        String email;
        LocalDateTime expiryTime;

        public OtpEntry(String otp, String email, LocalDateTime expiryTime) {
            this.otp = otp;
            this.email = email;
            this.expiryTime = expiryTime;
        }

        OtpEntry(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
        public void update(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }
}