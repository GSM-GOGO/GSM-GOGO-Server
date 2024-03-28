package team.gsmgogo.domain.team.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.gsmgogo.domain.normalteamparticipate.entity.NormalTeamParticipateEntity;
import team.gsmgogo.domain.normalteamparticipate.repository.NormalTeamParticipateJpaRepository;
import team.gsmgogo.domain.team.controller.dto.request.TeamNormalSaveRequest;
import team.gsmgogo.domain.team.controller.dto.response.TeamClassType;
import team.gsmgogo.domain.team.entity.TeamEntity;
import team.gsmgogo.domain.team.enums.TeamType;
import team.gsmgogo.domain.team.repository.TeamJpaRepository;
import team.gsmgogo.domain.team.service.TeamNormalSaveService;
import team.gsmgogo.domain.user.entity.UserEntity;
import team.gsmgogo.domain.user.enums.ClassEnum;
import team.gsmgogo.domain.user.repository.UserJpaRepository;
import team.gsmgogo.global.exception.error.ExpectedException;
import team.gsmgogo.global.facade.UserFacade;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO 일반경기 확정되면 인원수 체크하는 로직 추가

@Service
@RequiredArgsConstructor
public class TeamNormalSaveServiceImpl implements TeamNormalSaveService {

    private final TeamJpaRepository teamJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final NormalTeamParticipateJpaRepository normalTeamParticipateJpaRepository;
    private final UserFacade userFacade;

    @Override
    @Transactional
    public void saveNormalTeam(List<TeamNormalSaveRequest> request) {
        UserEntity currentUser = userFacade.getCurrentUser();

        if (teamJpaRepository.existsByTeamGradeAndTeamClassAndTeamType
                (currentUser.getUserGrade(), currentUser.getUserClass(), TeamType.NORMAL))
            throw new ExpectedException("이미 등록된 팀이 있습니다.", HttpStatus.BAD_REQUEST);

        Set<Long> chackDublicate = new HashSet<>();
        request.forEach(
                participate -> {
                    if (!chackDublicate.add(participate.getUserId())) {
                        throw new ExpectedException("참가 인원이 중복되서는 안됩니다.", HttpStatus.BAD_REQUEST);
                    }
                }
        );

        request.stream().map(user -> {
            UserEntity findUser = userJpaRepository.findByUserId(user.getUserId())
                    .orElseThrow(() -> new ExpectedException("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

            if (
                    findUser.getUserGrade() != currentUser.getUserGrade() ||
                    toTeamClassType(findUser.getUserClass()) != toTeamClassType(currentUser.getUserClass())
            ) {
                throw new ExpectedException("참가 인원이 같은 학년, 과의 학생 확인해주세요.", HttpStatus.BAD_REQUEST);
            }

            return findUser;
        }).toList();


        TeamEntity newTeam = TeamEntity.builder()
                .author(currentUser)
                .teamClass(currentUser.getUserClass())
                .teamGrade(currentUser.getUserGrade())
                .teamType(TeamType.NORMAL)
                .build();

        TeamEntity savedTeam = teamJpaRepository.save(newTeam);

        List<NormalTeamParticipateEntity> normalTeamParticipateEntities = request.stream().flatMap(
                participate -> participate.getTeamTypes().stream().map(team ->
                        NormalTeamParticipateEntity.builder()
                                .team(savedTeam)
                                .user(userJpaRepository.findByUserId(participate.getUserId())
                                        .orElseThrow(() -> new ExpectedException("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND)))
                                .normalTeamType(team).build())).toList();

        normalTeamParticipateJpaRepository.saveAll(normalTeamParticipateEntities);
    }

    private TeamClassType toTeamClassType(ClassEnum classEnum) {
        if (classEnum == ClassEnum.ONE || classEnum == ClassEnum.TWO)
            return TeamClassType.SW;
        else
            return TeamClassType.EB;
    }

}

