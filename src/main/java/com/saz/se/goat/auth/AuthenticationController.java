package com.saz.se.goat.auth;

import com.saz.se.goat.model.ErrorModel;
import com.saz.se.goat.model.ResendOTPRequest;
import com.saz.se.goat.model.ResponseWrapper;
import com.saz.se.goat.model.UserForgetPassResponse;
import com.saz.se.goat.requestModel.ForgetPasswordRequest;
import com.saz.se.goat.requestModel.SignInRequest;
import com.saz.se.goat.requestModel.SignUpRequest;
import com.saz.se.goat.user.UserDTO;
import com.saz.se.goat.utils.JsonUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    JsonUtils jsonUtils;

    @Value("${app.base.url}")
    private String baseUrl;

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @CrossOrigin
    @PostMapping("/resendotp")
    public ResponseEntity<?> resendOTP(@RequestBody ResendOTPRequest request, @RequestHeader HttpHeaders header) throws MessagingException {
        ResponseWrapper response = authenticationService.resendOTP(request);
        return jsonUtils.responseAsJson(response);
    }

    @CrossOrigin
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest request, @RequestHeader HttpHeaders header) throws MessagingException {
        ResponseWrapper response = authenticationService.signup(request);
        return jsonUtils.responseAsJson(response);
    }

    @CrossOrigin
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SignInRequest request, @RequestHeader HttpHeaders header)
    {
        ResponseWrapper<UserDTO> response = new ResponseWrapper<>();
        UserDTO userDTO = authenticationService.signIn(request);

        if (userDTO != null)
        {
            response.setData(userDTO);
        }
        else
        {
            response.addError(new ErrorModel("14464", "Invalid User name or Password"));
            return jsonUtils.responseAsJson(response);
        }
        return jsonUtils.responseAsJsonWithToken(response,userDTO.getEmail());
    }

    /*@CrossOrigin
    @GetMapping("/confirmEmail")
    public void activeAccount(@RequestParam String token, @RequestHeader HttpHeaders header, HttpServletResponse response) throws IOException {
        ResponseWrapper responseWrapper = authenticationService.activeAccount(token);
        if (responseWrapper.getErrors().size() < 1) {
            response.sendRedirect(baseUrl+"/signIn?status=activated");
        } else {
            response.sendRedirect(baseUrl+"/signIn?status=activation_failed");
        }
    }*/

    @CrossOrigin
    @GetMapping("/varifyOTP")
    public ResponseEntity<?> activeAccount(@RequestParam String phoneNo,@RequestParam String otp, @RequestHeader HttpHeaders header) throws IOException {
        ResponseWrapper<String> response = authenticationService.activeAccount(phoneNo,otp);
        return jsonUtils.responseAsJson(response);
    }

    @CrossOrigin
    @PostMapping("/forgetPassword")
    public ResponseEntity<?> forgetPassword(@RequestBody ForgetPasswordRequest forgetPasswordRequest, @RequestHeader HttpHeaders header) throws IOException
    {
        ResponseWrapper<String> response = authenticationService.forgetPassword(forgetPasswordRequest);
        return jsonUtils.responseAsJson(response);

    }

    @CrossOrigin
    @GetMapping("/sendEmailForRestPassword")
    public ResponseEntity<?> sendEmailForRestPassword(@RequestParam String email, @RequestHeader HttpHeaders header) throws IOException, MessagingException {
        ResponseWrapper<UserForgetPassResponse> response = authenticationService.sendEmailForRestPassword(email);
        return jsonUtils.responseAsJson(response);

    }
    @CrossOrigin
    @GetMapping("/renewToken")
    public ResponseEntity<?> renewToken(@RequestParam String email, @RequestHeader HttpHeaders header) throws IOException, MessagingException {
        ResponseWrapper<UserDTO> response = new ResponseWrapper<>();
        response = authenticationService.renewToken(email);
        return jsonUtils.responseAsJsonWithToken(response,email);

    }
}