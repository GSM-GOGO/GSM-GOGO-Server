package team.gsmgogo.domain.buttongame.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import static team.gsmgogo.domain.buttongame.entity.QButtonGameEntity.buttonGameEntity;

import team.gsmgogo.domain.buttongame.entity.ButtonGameEntity;

import java.util.Optional;

import static team.gsmgogo.domain.buttongameparticipate.entity.QButtonGameParticipate.buttonGameParticipate;

@Repository
@RequiredArgsConstructor
public class ButtonGameQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<ButtonGameEntity> findByMonthAndDay(int month, int day) {
        return Optional.ofNullable(queryFactory
                .select(buttonGameEntity)
                .from(buttonGameEntity)
                .join(buttonGameEntity.participates, buttonGameParticipate).fetchJoin()
                .where(
                        buttonGameEntity.createDate.month().eq(month)
                                .and(buttonGameEntity.createDate.dayOfMonth().eq(day))
                                .and(buttonGameEntity.isActive.eq(true))
                )
                .fetchOne());
    }
}
