package team.gsmgogo.domain.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.gsmgogo.domain.auth.controller.dto.response.TokenDto;
import team.gsmgogo.domain.auth.entity.RefreshTokenRedisEntity;
import team.gsmgogo.domain.auth.repository.RefreshTokenJpaRepository;
import team.gsmgogo.domain.auth.service.GauthLoginService;
import team.gsmgogo.domain.user.entity.UserEntity;
import team.gsmgogo.domain.user.enums.*;
import team.gsmgogo.domain.user.repository.UserJpaRepository;
import team.gsmgogo.global.exception.error.ExpectedException;
import team.gsmgogo.global.feign.GauthInfo;
import team.gsmgogo.global.feign.GauthToken;
import team.gsmgogo.global.feign.dto.GauthTokenDto;
import team.gsmgogo.global.feign.dto.GauthTokenRequest;
import team.gsmgogo.global.feign.dto.GauthUserDto;
import team.gsmgogo.global.feign.dto.UserSchoolRole;
import team.gsmgogo.global.security.jwt.TokenProvider;
import team.gsmgogo.global.security.jwt.dto.TokenResponse;

@Service
@RequiredArgsConstructor
public class GauthLoginServiceImpl implements GauthLoginService {

    private final GauthToken gauthToken;
    private final GauthInfo gauthInfo;
    private final UserJpaRepository userJpaRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Value("${spring.jwt.refreshExp}")
    private Long refreshExp;
    @Value("${gauth.clientId}")
    private String clientId;
    @Value("${gauth.clientSecret}")
    private String clientSecret;
    @Value("${gauth.redirectUrl}")
    private String redirectUri;

    @Override
    @Transactional
    public TokenDto execute(String code) {

        GauthTokenDto gauthTokenDto = gauthToken.getToken(GauthTokenRequest.builder()
                .code(code)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri(redirectUri)
                .build());

        GauthUserDto gauthUserDto = gauthInfo.getInfo("Bearer " + gauthTokenDto.getAccessToken());

        Gender gender = gauthUserDto.getGender();
        Integer grade = gauthUserDto.getGrade();
        Integer classNum = gauthUserDto.getClassNum();
        Integer userNum = gauthUserDto.getNum();
        UserSchoolRole userSchoolRole = gauthUserDto.getRole();
        String name = gauthUserDto.getName();
        String email = gauthUserDto.getEmail();

        UserEntity currentUser = userJpaRepository.findByUserEmail(email).orElse(null);

        boolean isSignedUp = true;
        long userId;

        if (currentUser == null) {
            UserEntity newUser = UserEntity.builder()
                    .isVerify(IsVerify.NONE)
                    .userName(name)
                    .userEmail(email)
                    .userGrade(
                        switch (grade) {
                            case 1 -> GradeEnum.ONE;
                            case 2 -> GradeEnum.TWO;
                            case 3 -> GradeEnum.THREE;
                            default -> throw new ExpectedException("유저의 학년을 엔티티와 매핑시키는 도중 발생한 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    )
                    .userClass(
                        switch (classNum) {
                            case 1 -> ClassEnum.ONE;
                            case 2 -> ClassEnum.TWO;
                            case 3 -> ClassEnum.THREE;
                            case 4 -> ClassEnum.FOUR;
                            default -> throw new ExpectedException("유저의 반을 엔티티와 매핑시키는 도중 발생한 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    )
                    .schoolRole(
                            switch (userSchoolRole) {
                                case ROLE_STUDENT -> SchoolRole.STUDENT;
                                case ROLE_TEACHER -> SchoolRole.TEACHER;
                            }

                    )
                    .role(Role.USER)
                    .verifyCount(0L)
                    .userNum(userNum)
                    .gender(gender)
                    .point(30000).build();

            userId = userJpaRepository.save(newUser).getUserId();

            isSignedUp = false;
        } else if (
            currentUser.getIsVerify() == IsVerify.VERIFY ||
            currentUser.getIsVerify() == IsVerify.SKIP
        ) {
            userId = currentUser.getUserId();
        } else {
            isSignedUp = false;
            userId = currentUser.getUserId();
        }

        TokenResponse token = tokenProvider.getToken(userId);

        RefreshTokenRedisEntity refreshToken = RefreshTokenRedisEntity.builder()
                .userId(userId)
                .refreshToken(token.getRefreshToken())
                .expiredAt(refreshExp).build();

        String savedRefreshToken = refreshTokenJpaRepository.save(refreshToken).getRefreshToken();

        return new TokenDto(token.getAccessToken(), savedRefreshToken, isSignedUp);

    }

}
