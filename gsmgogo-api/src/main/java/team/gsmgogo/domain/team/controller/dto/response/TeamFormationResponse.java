package team.gsmgogo.domain.team.controller.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.annotation.Nullable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.gsmgogo.domain.team.enums.BadmintonRank;
import team.gsmgogo.domain.team.enums.TeamType;
import team.gsmgogo.domain.user.enums.GradeEnum;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TeamFormationResponse {
    private Long teamId;
    @Enumerated(EnumType.STRING)
    private TeamType teamType;
    @Enumerated(EnumType.STRING)
    private GradeEnum teamGrade;
    @Enumerated(EnumType.STRING)
    private TeamClassType teamClassType;
    private boolean authorMe;
    private Integer winCount;
    private List<FormationParticipateDto> participates;
    @Nullable
    @Enumerated(EnumType.STRING)
    private BadmintonRank badmintonRank;
}
