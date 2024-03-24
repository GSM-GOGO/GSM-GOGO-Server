package team.gsmgogo.domain.teamparticipate.entity;

import jakarta.persistence.*;
import lombok.*;
import team.gsmgogo.domain.team.entity.TeamEntity;
import team.gsmgogo.domain.user.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "team_participate")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@ToString
public class TeamParticipateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_participate_id")
    private Long teamParticipateId;

    @OneToMany
    @PrimaryKeyJoinColumn(name = "user_id")
    private List<UserEntity> user = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn(name = "team_id")
    private TeamEntity team;

    @Column(name = "position_x")
    private Double positionX;

    @Column(name = "position_y")
    private Double positionY;
}
