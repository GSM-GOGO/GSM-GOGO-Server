package team.gsmgogo.domain.team.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.gsmgogo.domain.follow.entity.FollowEntity;
import team.gsmgogo.domain.follow.repository.FollowJpaRepository;
import team.gsmgogo.domain.team.controller.dto.response.TeamGetResponse;
import team.gsmgogo.domain.team.controller.dto.response.TeamInfoDto;
import team.gsmgogo.domain.team.entity.TeamEntity;
import team.gsmgogo.domain.team.enums.TeamType;
import team.gsmgogo.domain.team.repository.TeamJpaRepository;
import team.gsmgogo.domain.team.service.TeamGetService;
import team.gsmgogo.domain.user.entity.UserEntity;
import team.gsmgogo.global.facade.UserFacade;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamGetServiceImpl implements TeamGetService {
    private final TeamJpaRepository teamJpaRepository;
    private final FollowJpaRepository followJpaRepository;
    private final UserFacade userFacade;

    @Override
    public TeamGetResponse getTeam(TeamType teamType) {
        UserEntity user = userFacade.getCurrentUser();
        List<TeamEntity> teamEntityList = teamJpaRepository.findByTeamType(teamType);

        return new TeamGetResponse(
            teamEntityList.stream().map(teamEntity -> new TeamInfoDto(
                teamEntity.getTeamId(),
                teamEntity.getTeamName(),
                teamEntity.getTeamGrade().toString(),
                teamEntity.getTeamType().getType(),
                teamEntity.getWinCount(),
                followJpaRepository.existsByUserAndTeam(user, teamEntity),
                "뭐용"
            )).toList()
        );
    }
}