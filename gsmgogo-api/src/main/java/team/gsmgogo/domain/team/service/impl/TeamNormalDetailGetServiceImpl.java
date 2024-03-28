package team.gsmgogo.domain.team.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import team.gsmgogo.domain.normalteamparticipate.entity.NormalTeamParticipateEntity;
import team.gsmgogo.domain.normalteamparticipate.enums.NormalTeamType;
import team.gsmgogo.domain.normalteamparticipate.repository.NormalTeamParticipateJpaRepository;
import team.gsmgogo.domain.team.controller.dto.response.NormalTeamParticipateDto;
import team.gsmgogo.domain.team.controller.dto.response.TeamClassType;
import team.gsmgogo.domain.team.controller.dto.response.TeamNormalInfoDto;
import team.gsmgogo.domain.team.controller.dto.response.TeamNormalListResponse;
import team.gsmgogo.domain.team.entity.TeamEntity;
import team.gsmgogo.domain.team.enums.TeamType;
import team.gsmgogo.domain.team.repository.TeamJpaRepository;
import team.gsmgogo.domain.team.service.TeamNormalDetailGetService;
import team.gsmgogo.domain.user.enums.ClassEnum;
import team.gsmgogo.global.exception.error.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamNormalDetailGetServiceImpl implements TeamNormalDetailGetService {
    private final TeamJpaRepository teamJpaRepository;
    private final NormalTeamParticipateJpaRepository normalTeamParticipateJpaRepository;

    @Override
    public TeamNormalListResponse execute(String teamId) {
        List<NormalTeamParticipateEntity> normalTeamParticipateList = normalTeamParticipateJpaRepository.findByTeamTeamId(Long.valueOf(teamId));

        TeamEntity team = teamJpaRepository.findByTeamTypeAndTeamId(TeamType.NORMAL, Long.valueOf(teamId))
            .orElseThrow(() -> new ExpectedException("해당 팀을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        List<TeamNormalInfoDto> teamNormalInfoDtoList = Arrays.stream(NormalTeamType.values())
                .map(type -> {
                    List<NormalTeamParticipateDto> participates = normalTeamParticipateList.stream()
                            .filter(participate -> participate.getNormalTeamType() == type)
                            .map(participate -> new NormalTeamParticipateDto(participate.getUser().getUserId(), participate.getUser().getUserName()))
                            .toList();

                    return new TeamNormalInfoDto(type, participates);
                })
                .toList();

        return new TeamNormalListResponse(
            team.getTeamId(),
            team.getTeamGrade(),
            toTeamClassType(team.getTeamClass()),
            teamNormalInfoDtoList
        );
    }

    private TeamClassType toTeamClassType(ClassEnum classEnum) {
        if (classEnum == ClassEnum.ONE || classEnum == ClassEnum.TWO)
            return TeamClassType.SW;
        else
            return TeamClassType.EB;
    }
}
