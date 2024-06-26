package team.gsmgogo.domain.user.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.gsmgogo.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import team.gsmgogo.domain.user.enums.ClassEnum;
import team.gsmgogo.domain.user.enums.GradeEnum;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserEmail(String userEmail);
    Optional<UserEntity> findByUserId(Long userId);
    List<UserEntity> findTop5ByUserNameContainingAndUserGradeInOrderByUserNameAsc(String userName, List<GradeEnum> userGrade);
    List<UserEntity> findAllByUserGradeInAndUserClassIn(List<GradeEnum> userGrades, List<ClassEnum> userClasses);
    List<UserEntity> findAllByOrderByPointDesc();
}
