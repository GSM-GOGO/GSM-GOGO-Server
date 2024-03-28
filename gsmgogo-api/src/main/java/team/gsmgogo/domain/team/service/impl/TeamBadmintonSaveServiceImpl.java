package team.gsmgogo.domain.team.service.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.gsmgogo.domain.team.controller.dto.request.TeamBadmintonSaveRequest;
import team.gsmgogo.domain.team.entity.TeamEntity;
import team.gsmgogo.domain.team.enums.BadmintonRank;
import team.gsmgogo.domain.team.enums.TeamType;
import team.gsmgogo.domain.team.repository.TeamJpaRepository;
import team.gsmgogo.domain.team.service.TeamBadmintonSaveService;
import team.gsmgogo.domain.teamparticipate.entity.TeamParticipateEntity;
import team.gsmgogo.domain.teamparticipate.repository.TeamParticipateJpaRepository;
import team.gsmgogo.domain.user.entity.UserEntity;
import team.gsmgogo.domain.user.enums.Gender;
import team.gsmgogo.domain.user.enums.GradeEnum;
import team.gsmgogo.domain.user.repository.UserJpaRepository;
import team.gsmgogo.global.exception.error.ExpectedException;
import team.gsmgogo.global.facade.UserFacade;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TeamBadmintonSaveServiceImpl implements TeamBadmintonSaveService {

    private final TeamJpaRepository teamJpaRepository;
    private final TeamParticipateJpaRepository teamParticipateJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final UserFacade userFacade;

    @Override
    @Transactional
    public void saveBadmintonTeam(TeamBadmintonSaveRequest request) {
        UserEntity currentUser = userFacade.getCurrentUser();

        if (request.getParticipates().size() != 2)
            throw new ExpectedException("배드민턴 참가 가능 인원은 2명입니다.", HttpStatus.BAD_REQUEST);

        if (request.getParticipates().stream().noneMatch(user -> user.getUserId().equals(currentUser.getUserId())))
            throw new ExpectedException("참가 인원에 본인을 포함해주세요.", HttpStatus.BAD_REQUEST);

        UserEntity participateA = userJpaRepository.findByUserId(request.getParticipates().get(0).getUserId())
                .orElseThrow(() -> new ExpectedException("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        UserEntity participateB = userJpaRepository.findByUserId(request.getParticipates().get(1).getUserId())
                .orElseThrow(() -> new ExpectedException("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (Objects.equals(participateA.getUserId(), participateB.getUserId()))
            throw new ExpectedException("참가 인원이 중복되서는 안됩니다.", HttpStatus.BAD_REQUEST);

        if (participateA.getGender() != participateB.getGender()) {
            throw new ExpectedException("참가 인원의 성별은 같아야 합니다.", HttpStatus.BAD_REQUEST);
        }

        if (teamJpaRepository.existsByTeamGradeAndTeamClassAndTeamType
                (participateA.getUserGrade(), participateA.getUserClass(), TeamType.BADMINTON)
            ||
                teamJpaRepository.existsByTeamGradeAndTeamClassAndTeamType
                        (participateB.getUserGrade(), participateB.getUserClass(), TeamType.BADMINTON)
        )
            throw new ExpectedException("이미 등록된 팀이 있습니다.", HttpStatus.BAD_REQUEST);

        BadmintonRank rank = getBadmintonRank(participateA, participateB);

        TeamEntity newTeam = TeamEntity.builder()
                .teamName(request.getTeamName())
                .author(currentUser)
                .winCount(0)
                .isSurvived(true)
                .teamClass(currentUser.getUserClass())
                .teamGrade(currentUser.getUserGrade())
                .teamType(TeamType.BADMINTON)
                .badmintonRank(rank)
                .build();

        TeamEntity savedTeam = teamJpaRepository.save(newTeam);

        TeamParticipateEntity newParticipateA = TeamParticipateEntity.builder()
                        .team(savedTeam)
                        .user(participateA)
                        .positionX(request.getParticipates().get(0).getPositionX())
                        .positionY(request.getParticipates().get(0).getPositionY())
                        .build();

        TeamParticipateEntity newParticipateB = TeamParticipateEntity.builder()
                .team(savedTeam)
                .user(participateB)
                .positionX(request.getParticipates().get(1).getPositionX())
                .positionY(request.getParticipates().get(1).getPositionY())
                .build();

        teamParticipateJpaRepository.save(newParticipateA);
        teamParticipateJpaRepository.save(newParticipateB);

    }

    @Nullable
    private static BadmintonRank getBadmintonRank(UserEntity participateA, UserEntity participateB) {
        BadmintonRank rank = null;

        if ( // 1학년 남자: C
            participateA.getGender() == Gender.MALE && participateB.getGender() == Gender.MALE
                    &&
            participateA.getUserGrade() == GradeEnum.ONE && participateB.getUserGrade() == GradeEnum.ONE ) {
            rank = BadmintonRank.C;
        } else if ( // 학년무관 여자: D
                participateA.getGender() == Gender.FEMALE && participateB.getGender() == Gender.FEMALE
        ) {
            rank = BadmintonRank.D;
        } else if ( // 2, 3학년 남자: B
                participateA.getGender() == Gender.MALE && participateB.getGender() == Gender.MALE
                        &&
                (participateA.getUserGrade() == GradeEnum.TWO || participateA.getUserGrade() == GradeEnum.THREE)
                        &&
                (participateB.getUserGrade() == GradeEnum.TWO || participateB.getUserGrade() == GradeEnum.THREE)
        ) {
            rank = BadmintonRank.B;
        }

        return rank;
    }
}
