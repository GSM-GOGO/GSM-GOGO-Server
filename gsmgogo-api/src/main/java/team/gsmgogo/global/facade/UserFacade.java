package team.gsmgogo.global.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import team.gsmgogo.domain.user.entity.UserEntity;
import team.gsmgogo.domain.user.repository.UserJpaRepository;
import team.gsmgogo.global.exception.error.ExpectedException;

@Configuration
@RequiredArgsConstructor
public class UserFacade {
    private final UserJpaRepository userJpaRepository;

    public UserEntity getCurrentUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if(userId.equals("anonymousUser")) throw new ExpectedException("게스트의 유저 정보를 가져올 수 없습니다.", HttpStatus.FORBIDDEN);

        return getUserByEmail(userId);
    }

    public UserEntity getUserByEmail(String userId) {
        return userJpaRepository.findByUserId(Long.valueOf(userId))
                .orElseThrow(() -> new ExpectedException("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }

}
