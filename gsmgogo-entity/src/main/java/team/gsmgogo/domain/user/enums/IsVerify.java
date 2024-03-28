package team.gsmgogo.domain.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IsVerify {
    VERIFY("VERIFY"),
    NONE("NONE"),
    SKIP("SKIP");

    private final String description;
}
