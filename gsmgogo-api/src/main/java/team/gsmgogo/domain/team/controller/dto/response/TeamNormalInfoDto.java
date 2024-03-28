package team.gsmgogo.domain.team.controller.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.gsmgogo.domain.normalteamparticipate.enums.NormalTeamType;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TeamNormalInfoDto {
    private NormalTeamType teamType;
    private List<NormalTeamParticipateDto> participates;
}
