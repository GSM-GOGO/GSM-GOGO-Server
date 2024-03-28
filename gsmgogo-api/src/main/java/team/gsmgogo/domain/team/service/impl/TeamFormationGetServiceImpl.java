package team.gsmgogo.domain.team.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import team.gsmgogo.domain.team.controller.dto.response.FormationParticipateDto;
import team.gsmgogo.domain.team.controller.dto.response.TeamClassType;
import team.gsmgogo.domain.team.controller.dto.response.TeamFormationResponse;
import team.gsmgogo.domain.team.entity.TeamEntity;
import team.gsmgogo.domain.team.repository.TeamJpaRepository;
import team.gsmgogo.domain.team.service.TeamFormationGetService;
import team.gsmgogo.domain.teamparticipate.entity.TeamParticipateEntity;
import team.gsmgogo.domain.teamparticipate.repository.TeamParticipateJpaRepository;
import team.gsmgogo.domain.user.entity.UserEntity;
import team.gsmgogo.domain.user.enums.ClassEnum;
import team.gsmgogo.global.exception.error.ExpectedException;
import team.gsmgogo.global.facade.UserFacade;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamFormationGetServiceImpl implements TeamFormationGetService {
    private final TeamJpaRepository teamJpaRepository;
    private final TeamParticipateJpaRepository teamParticipateJpaRepository;
    private final UserFacade userFacade;

    @Override
    public TeamFormationResponse execute(String teamId) {
        UserEntity currentUser = userFacade.getCurrentUser();

        TeamEntity team = teamJpaRepository.findByTeamId(Long.valueOf(teamId))
            .orElseThrow(() -> new ExpectedException("해당 팀을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        List<TeamParticipateEntity> participateList = teamParticipateJpaRepository.findByTeamTeamId(team.getTeamId());

        return new TeamFormationResponse(
            team.getTeamId(),
            team.getTeamType(),
            team.getTeamGrade(),
            toTeamClassType(team.getTeamClass()),
            team.getAuthor().getUserId().equals(currentUser.getUserId()),
            team.getWinCount(),
            participateList.stream().map(teamParticipate -> new FormationParticipateDto(
                teamParticipate.getUser().getUserId(),
                teamParticipate.getUser().getUserName(),
                teamParticipate.getPositionX(),
                teamParticipate.getPositionY()
            )).toList(),
            team.getBadmintonRank()
        );
    }

    private TeamClassType toTeamClassType(ClassEnum classEnum) {
        if (classEnum == ClassEnum.ONE || classEnum == ClassEnum.TWO)
            return TeamClassType.SW;
        else
            return TeamClassType.EB;
    }
}
