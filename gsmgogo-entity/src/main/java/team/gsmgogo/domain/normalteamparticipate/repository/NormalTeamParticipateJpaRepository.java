package team.gsmgogo.domain.normalteamparticipate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.gsmgogo.domain.normalteamparticipate.entity.NormalTeamParticipateEntity;
import team.gsmgogo.domain.team.entity.TeamEntity;

import java.util.List;

public interface NormalTeamParticipateJpaRepository extends JpaRepository<NormalTeamParticipateEntity, Long> {
    List<NormalTeamParticipateEntity> findByTeamTeamId(Long id);
}
