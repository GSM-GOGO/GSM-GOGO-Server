package team.gsmgogo.domain.user.entity;

import team.gsmgogo.domain.normalteamparticipate.entity.NormalTeamParticipateEntity;
import team.gsmgogo.domain.teamparticipate.entity.TeamParticipateEntity;
import team.gsmgogo.domain.user.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@ToString
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "user_grade")
    @Enumerated(EnumType.STRING)
    private GradeEnum userGrade;

    @Column(name = "user_class")
    @Enumerated(EnumType.STRING)
    private ClassEnum userClass;

    @Column(name = "user_num")
    private Integer userNum;

    @Column(name = "user_gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "school_role")
    @Enumerated(EnumType.STRING)
    private SchoolRole schoolRole;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "point")
    private Integer point;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_verify", nullable = false)
    private IsVerify isVerify = IsVerify.NONE;


    @Column(name = "verify_count")
    private Long verifyCount;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamParticipateEntity> teamParticipates = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NormalTeamParticipateEntity> normalTeamParticipateEntities = new ArrayList<>();

    public void setVerify(IsVerify isVerify) {
        this.isVerify = isVerify;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void plusCount(){
        verifyCount += 1;
    }

    public void resetCount(){ verifyCount = 0L; }
}
