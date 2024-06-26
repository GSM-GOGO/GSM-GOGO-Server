package team.gsmgogo.domain.team.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.gsmgogo.domain.team.controller.dto.request.TeamDeleteRequest;
import team.gsmgogo.domain.team.entity.TeamEntity;
import team.gsmgogo.domain.team.repository.TeamJpaRepository;
import team.gsmgogo.domain.team.service.TeamDeleteService;
import team.gsmgogo.domain.user.entity.UserEntity;
import team.gsmgogo.global.exception.error.ExpectedException;
import team.gsmgogo.global.facade.UserFacade;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TeamDeleteServiceImpl implements TeamDeleteService {

    private final TeamJpaRepository teamJpaRepository;
    private final UserFacade userFacade;

    @Override
    @Transactional
    public void deleteTeam(TeamDeleteRequest request) {
        TeamEntity team = teamJpaRepository.findByTeamId(request.getTeamId())
                .orElseThrow(() -> new ExpectedException("팀을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        UserEntity currentUser = userFacade.getCurrentUser();

        if (!Objects.equals(team.getAuthor().getUserId(), currentUser.getUserId())) {
            throw new ExpectedException("팀을 동록한 사람이 아닙니다.", HttpStatus.BAD_REQUEST);
        }

        teamJpaRepository.delete(team);
    }
}
