package com.saz.se.goat.fcm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FcmDeviceRepository extends JpaRepository<FcmDevice, Long> {
    Optional<FcmDevice> findByDevice(String device);
}