package team.gsmgogo.domain.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import team.gsmgogo.domain.auth.service.SkipVerifyService;
import team.gsmgogo.domain.user.entity.UserEntity;
import team.gsmgogo.domain.user.enums.IsVerify;
import team.gsmgogo.domain.user.repository.UserJpaRepository;
import team.gsmgogo.global.exception.error.ExpectedException;
import team.gsmgogo.global.facade.UserFacade;

@Service
@RequiredArgsConstructor
public class SkipVerifyServiceImpl implements SkipVerifyService {
    private final UserFacade userFacade;
    private final UserJpaRepository userJpaRepository;

    @Override
    public void execute(){
        UserEntity currentUser = userFacade.getCurrentUser();

        if(currentUser.getIsVerify() == IsVerify.VERIFY)
            throw new ExpectedException("이미 인증된 전화번호가 존재합니다.", HttpStatus.BAD_REQUEST);

        currentUser.setVerify(IsVerify.SKIP);
        userJpaRepository.save(currentUser);
    }
}
