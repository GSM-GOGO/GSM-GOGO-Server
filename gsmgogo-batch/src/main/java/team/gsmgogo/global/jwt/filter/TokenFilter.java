package team.gsmgogo.global.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import team.gsmgogo.domain.auth.entity.BlackListRedisEntity;
import team.gsmgogo.domain.auth.repository.BlackListJpaRepository;
import team.gsmgogo.global.jwt.TokenProvider;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final BlackListJpaRepository blackListJpaRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/auth/refresh");
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String token = request.getHeader("Authorization");

        if (token != null) {

            Optional<BlackListRedisEntity> blackList = blackListJpaRepository.findByAccessToken(token);
            if (blackList.isPresent() && token.equals(blackList.get().getAccessToken())) {
                throw new RuntimeException("해당 AccessToken은 사용할 수 없습니다.");
            }

            Authentication authentication = tokenProvider.authorization(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request,response);
    }

}
