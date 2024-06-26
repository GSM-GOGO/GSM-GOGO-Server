package team.gsmgogo.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.PlatformTransactionManager;
import team.gsmgogo.domain.buttongame.entity.ButtonGameEntity;
import team.gsmgogo.domain.buttongame.enums.ButtonType;
import team.gsmgogo.domain.buttongame.repository.ButtonGameRepository;
import team.gsmgogo.domain.buttongameparticipate.repository.ButtonGameParticipateQueryDslRepository;
import team.gsmgogo.domain.buttongameparticipate.repository.dto.ButtonGameResultDto;
import team.gsmgogo.domain.user.entity.UserEntity;
import team.gsmgogo.domain.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CalculateButtonGameJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final ButtonGameRepository buttonGameRepository;
    private final UserJpaRepository userJpaRepository;
    private final ButtonGameParticipateQueryDslRepository buttonGameParticipateQueryDslRepository;

    private final Integer TOTAL_POINT = 2000000;

    @Bean(name = "buttonGameCalculateJob")
    public Job calculateButtonGameJob(){
        return new JobBuilder("calculate-button-game-job", jobRepository)
                .start(calculateButtonGameStep(jobRepository, platformTransactionManager))
                .build();
    }

    @Bean
    public Step calculateButtonGameStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("calculate-button-game-step", jobRepository)
                .tasklet((contribution, chunkContext) -> {

                            ButtonGameEntity buttonGame = buttonGameRepository.findByIsActiveIsTrue()
                                    .orElseThrow(RuntimeException::new);

                            ButtonGameResultDto buttonGameResultDto = buttonGameParticipateQueryDslRepository
                                    .findWinUserList(buttonGame);

                            ButtonType winType = buttonGameResultDto.getWinType();
                            List<UserEntity> winUserList = buttonGameResultDto.getWinUserList();

                            addPointWinUser(winUserList);
                            endGame(buttonGame, winType);
                            addNewButtonGame();

                            return RepeatStatus.FINISHED;
                        },
                        platformTransactionManager)
                .build();
    }

    private void addPointWinUser(List<UserEntity> winUserList) {
        Integer ADD_POINT = (int) Math.ceil((double) TOTAL_POINT / winUserList.size());
        winUserList.forEach(user -> user.addPoint(ADD_POINT));
        userJpaRepository.saveAll(winUserList);
    }

    private void endGame(ButtonGameEntity buttonGame, ButtonType winType) {
        buttonGame.setWinType(winType);
        buttonGame.endGame();
        buttonGameRepository.save(buttonGame);
    }

    private void addNewButtonGame() {
        buttonGameRepository.save(
                ButtonGameEntity.builder()
                        .isActive(true)
                        .createDate(LocalDateTime.now().plusDays(1L))
                        .build()
        );
    }

}
