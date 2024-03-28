package team.gsmgogo.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.gsmgogo.domain.auth.controller.dto.request.AuthSendCodeRequest;
import team.gsmgogo.domain.auth.controller.dto.response.AuthCallBackCodeResponse;
import team.gsmgogo.domain.auth.controller.dto.response.AuthLoginUrlResponse;
import team.gsmgogo.domain.auth.controller.dto.response.ReissueTokenDto;
import team.gsmgogo.domain.auth.controller.dto.response.TokenDto;
import team.gsmgogo.domain.auth.service.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MessageSendService messageSendService;
    private final CheckVerifyCodeService checkVerifyCodeService;
    private final GauthLoginService gauthLoginService;
    private final TokenReissueService tokenReissueService;
    private final SkipVerifyService skipVerifyService;

    @Value("${gauth.clientId}")
    private String clientId;
    @Value("${gauth.redirectUrl}")
    private String redirectUri;
    @Value("${spring.jwt.accessExp}")
    public Long accessExp;
    @Value("${spring.jwt.refreshExp}")
    public Long refreshExp;

    @GetMapping("/login")
    public ResponseEntity<AuthLoginUrlResponse> login(HttpServletResponse response) throws IOException {
        String loginUrl = "https://gauth.co.kr/login?" +
                "client_id=" + clientId + "&" +
                "redirect_uri=" + redirectUri;

        return ResponseEntity.ok(new AuthLoginUrlResponse(loginUrl));
    }

    @GetMapping("/callback")
    public ResponseEntity<AuthCallBackCodeResponse> callback(@RequestParam("code") String code, HttpServletResponse response) {
        TokenDto tokenDto = gauthLoginService.execute(code);
        response.addHeader("Authorization", tokenDto.getAccessToken());
        response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
        return ResponseEntity.ok(new AuthCallBackCodeResponse(tokenDto.getIsSignup()));
    }

    @GetMapping("/refresh")
    public ResponseEntity<Void> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = request.getHeader("Refresh-Token");
        ReissueTokenDto reissueTokenDto = tokenReissueService.execute(refreshToken);
        response.addHeader("Authorization", reissueTokenDto.getAccessToken());
        response.addHeader("Refresh-Token", reissueTokenDto.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sms")
    public ResponseEntity<Void> sendCodeMessage(@RequestBody AuthSendCodeRequest request){
        messageSendService.execute(request.getPhoneNumber());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sms/test")
    public ResponseEntity<String> sendCodeMessageTest(@RequestBody AuthSendCodeRequest request){
        return ResponseEntity.ok(messageSendService.test(request.getPhoneNumber()));
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> checkVerifyCode(@RequestParam("code") String verifyCode){
        checkVerifyCodeService.execute(verifyCode);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/sms/skip")
    public ResponseEntity<Void> skipVerify(){
        skipVerifyService.execute();
        return ResponseEntity.ok().build();
    }
}