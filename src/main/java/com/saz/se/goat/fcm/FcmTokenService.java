package com.saz.se.goat.fcm;

import com.saz.se.goat.model.FcmTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FcmTokenService {

    @Autowired
    private FcmDeviceRepository repository;

    public void saveToken(FcmTokenRequest request) {
        FcmDevice device = repository.findByDevice(request.getDevice()).orElse(new FcmDevice());
        device.setDevice(request.getDevice());
        device.setFcmToken(request.getFcmToken());
        repository.save(device);
    }

    public String getTokenForDevice(String device) {
        return repository.findByDevice(device)
                .map(FcmDevice::getFcmToken)
                .orElseThrow(() -> new RuntimeException("Device not found"));
    }
}