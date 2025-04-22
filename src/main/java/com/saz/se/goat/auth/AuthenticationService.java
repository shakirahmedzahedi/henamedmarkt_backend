package com.saz.se.goat.auth;

import com.saz.se.goat.fcm.FcmDevice;
import com.saz.se.goat.fcm.FcmDeviceRepository;
import com.saz.se.goat.model.ErrorModel;
import com.saz.se.goat.model.ResendOTPRequest;
import com.saz.se.goat.model.ResponseWrapper;
import com.saz.se.goat.model.UserForgetPassResponse;
import com.saz.se.goat.otp.FCMService;
import com.saz.se.goat.otp.OtpService;
import com.saz.se.goat.requestModel.ForgetPasswordRequest;
import com.saz.se.goat.requestModel.SignInRequest;
import com.saz.se.goat.requestModel.SignUpRequest;
import com.saz.se.goat.response.UserResponse;
import com.saz.se.goat.user.UserDTO;
import com.saz.se.goat.user.UserEntity;
import com.saz.se.goat.user.UserRepository;
import com.saz.se.goat.utils.CommonDTO;
import com.saz.se.goat.utils.EmailService;
import com.saz.se.goat.utils.JWTService;
import io.jsonwebtoken.JwtException;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private OtpService otpService;
    @Autowired
    private FCMService fcmService;
    @Autowired
    private FcmDeviceRepository fcmDeviceRepository;
    CommonDTO commonDTO = new CommonDTO();

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);


    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Transactional
    public ResponseWrapper<String> signup(SignUpRequest request) throws MessagingException {

        ResponseWrapper<String> response = new ResponseWrapper<>();

        boolean isExit = userRepository.findByEmail(request.getEmail()).isPresent();

        if (isExit) {
            response.addError(new ErrorModel("14461", "E-mail Already is use! "));
            return response;
        }

        try
        {
            UserEntity user = new UserEntity (
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    encoder.encode(request.getPassword()),
                    request.getPhoneNo(),
                    request.getAddress());

            userRepository.save(user);
            //emailService.sendConfirmationEmail(user);
            String otp = otpService.generateOtp(user.getPhoneNo(), user.getEmail());
            String fcmtoken = fcmDeviceRepository.findByDevice("admin-phone").get().getFcmToken();
            fcmService.sendOtpNotification(fcmtoken,otp,user.getPhoneNo());
            response.setData("User registered. OTP verification needed.");
        }
        catch (Exception ex)
        {
            response.addError(new ErrorModel("14462", "Could not send OTP"));

        }
        //emailService.sendConfirmationEmail(user);

        return response;

    }


    public String verify(UserEntity user) {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getEmail());
        } else {
            return "fail";
        }
    }

    public ResponseWrapper<String> activeAccount(String phoneNo,String otp) {
        ResponseWrapper<String> response = new ResponseWrapper<>();
        String email;
        try
        {
            email = otpService.verifyOtp(phoneNo,otp);//jwtService.extractUserName(token);
            if(email.equals("Expire")){
                response.addError(new ErrorModel("14468", "Invalid or Expire OTP"));
                return response;
            }
        }
        catch (JwtException ex)
        {
            response.addError(new ErrorModel("14463", ex.getMessage()));
            return response;
        }
        UserEntity user  = userRepository.findByEmail(email).orElse(null);

        if (user == null)
        {
            response.addError(new ErrorModel("14462", "User not valid"));
            return response;
        }
        else
        {
           user.setActive(true);
           userRepository.save(user);
        }
        response.setData("User Active. Please Log in");
        return  response;
    }

    public UserDTO signIn(SignInRequest request)
    {
        try
        {
            Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            UserEntity user = userRepository.findByEmail(request.getEmail()).orElse(null);
            if (authentication.isAuthenticated() && user.isActive())
            {
                return commonDTO.toUserDTO(user);

            }

        } catch (BadCredentialsException e) {
            // Handle invalid credentials
             ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");

        }


        return  null;
    }


    public ResponseWrapper forgetPassword(ForgetPasswordRequest forgetPasswordRequest)
    {
        ResponseWrapper<String> response = new ResponseWrapper<>();
        Optional<UserEntity> userEntity = userRepository.findByEmail(forgetPasswordRequest.getEmail());

        if (userEntity.isPresent())
        {
           userEntity.get().setPassword( encoder.encode(forgetPasswordRequest.getPassword()));
           userRepository.save(userEntity.get());
           response.setData("Successfully Reset the password.");
        }
        else
        {
            response.addError(new ErrorModel("14462", "Your are not Register User!"));
        }

        return  response;
    }

    public ResponseWrapper<UserForgetPassResponse> sendEmailForRestPassword(String email) throws MessagingException
    {
        ResponseWrapper<UserForgetPassResponse> response = new ResponseWrapper<>();
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);

        if (userEntity.isPresent())
        {
           //emailService.sendForgetPasswordEmail(userEntity.get());
            String otp = otpService.generateOtp(userEntity.get().getPhoneNo(), userEntity.get().getEmail());
            String fcmtoken = fcmDeviceRepository.findByDevice("admin-phone").get().getFcmToken();
            fcmService.sendOtpNotification(fcmtoken,otp,userEntity.get().getPhoneNo());
           response.setData(new UserForgetPassResponse(userEntity.get().getEmail(), userEntity.get().getPhoneNo()));
        }
        else
        {
            response.addError(new ErrorModel("14462", "Your are not Register User!"));
        }

        return  response;

    }

    public ResponseWrapper<UserDTO>  renewToken(String email)
    {
        ResponseWrapper<UserDTO> response = new ResponseWrapper<>();
        Optional<UserEntity> userEntity = userRepository.findByEmail(email);
        if (userEntity.isPresent())
        {
           UserDTO userDTO = userEntity.map(commonDTO::toUserDTO).orElse(null);
           response.setData(userDTO);
        }
        else
        {
            response.addError(new ErrorModel("14462", "Your are not Register User!"));
        }

        return  response;
    }

    public ResponseWrapper<String> resendOTP(ResendOTPRequest request)
    {
        ResponseWrapper<String> response = new ResponseWrapper<>();
        try {

            String otp = otpService.generateOtp(request.getPhoneNo(),request.getEmail());
            String fcmtoken = fcmDeviceRepository.findByDevice("admin-phone").get().getFcmToken();
            fcmService.sendOtpNotification(fcmtoken,otp,request.getPhoneNo());
            response.setData("User registered. Please check your email for verification.");
        }
        catch (Exception ex){
            response.addError(new ErrorModel("14462", "Could not send OTP"));
        }
        return response;
    }
}
