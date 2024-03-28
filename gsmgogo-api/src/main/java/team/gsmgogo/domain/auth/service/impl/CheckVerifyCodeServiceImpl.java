package team.gsmgogo.domain.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.gsmgogo.domain.auth.entity.VerifyCodeRedisEntity;
import team.gsmgogo.domain.auth.repository.VerifyCodeJpaRepository;
import team.gsmgogo.domain.auth.service.CheckVerifyCodeService;
import team.gsmgogo.domain.user.entity.UserEntity;
import team.gsmgogo.domain.user.enums.IsVerify;
import team.gsmgogo.domain.user.repository.UserJpaRepository;
import team.gsmgogo.global.exception.error.ExpectedException;
import team.gsmgogo.global.facade.UserFacade;

@Service
@RequiredArgsConstructor
public class CheckVerifyCodeServiceImpl implements CheckVerifyCodeService {
    private final UserFacade userFacade;
    private final VerifyCodeJpaRepository verifyCodeJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    @Transactional
    public void execute(String code) {
        UserEntity user = userFacade.getCurrentUser();
        Long id = user.getUserId();

        VerifyCodeRedisEntity verifyCode = verifyCodeJpaRepository.findByUserId(id)
            .orElseThrow(() -> new ExpectedException("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if(!verifyCode.getCode().equals(code)){
            throw new ExpectedException("인증 코드가 같지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        user.setVerify(IsVerify.VERIFY);
        userJpaRepository.save(user);
    }
}
