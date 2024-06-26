package team.gsmgogo.domain.auth.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthSendCodeRequest {
    @JsonProperty(value = "phone_number")
    @Pattern(regexp = "^010\\d{8}$", message = "전화번호 형식이 잘못되었습니다.")
    private String phoneNumber;
}
