package team.gsmgogo.domain.auth.entity;

import jakarta.persistence.Column;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("refreshToken")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshTokenRedisEntity {
    @Id
    @Column(name = "user_email")
    private String userEmail;

    @Indexed
    @Column(name = "refresh_token")
    private String refreshToken;

    @TimeToLive
    @Column(name = "expired_at")
    private Long expiredAt;
}
