package team.gsmgogo.domain.game.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import team.gsmgogo.domain.bet.entity.BetEntity;
import team.gsmgogo.domain.bet.repository.BetJpaRepository;
import team.gsmgogo.domain.buttongame.entity.ButtonGameEntity;
import team.gsmgogo.domain.buttongame.enums.ButtonType;
import team.gsmgogo.domain.buttongame.repository.ButtonGameRepository;
import team.gsmgogo.domain.buttongameparticipate.entity.ButtonGameParticipate;
import team.gsmgogo.domain.buttongameparticipate.repository.ButtonGameParticipateRepository;
import team.gsmgogo.domain.game.controller.dto.request.ButtonGameRequest;
import team.gsmgogo.domain.game.service.ButtonGameService;
import team.gsmgogo.domain.user.entity.UserEntity;
import team.gsmgogo.global.exception.error.ExpectedException;
import team.gsmgogo.global.facade.UserFacade;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ButtonGameServiceImpl implements ButtonGameService {

    private final UserFacade userFacade;
    private final ButtonGameRepository buttonGameRepository;
    private final ButtonGameParticipateRepository buttonGameParticipateRepository;
    private final BetJpaRepository buttonGameParticipate;

    private final int LIMIT_POINT = 500_000;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void execute(ButtonGameRequest request) {

        UserEntity currentUser = userFacade.getCurrentUser();

        ButtonGameEntity buttonGame = buttonGameRepository.findByIsActiveIsTrue()
                .orElseThrow(() -> new ExpectedException("버튼 게임을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (buttonGameParticipateRepository.existsByButtonGameAndUser(buttonGame, currentUser))
            throw new ExpectedException("이미 버튼을 눌렀습니다.", HttpStatus.BAD_REQUEST);

        List<ButtonType> typeList = List.of(
                ButtonType.ONE,
                ButtonType.TWO,
                ButtonType.THREE,
                ButtonType.FOUR,
                ButtonType.FIVE);

        if (request.getButtonType() == null || !typeList.stream().anyMatch(type -> type.equals(request.getButtonType())))
            throw new ExpectedException("올바른 버튼 종류를 입력해주세요.", HttpStatus.BAD_REQUEST);

        AtomicInteger betPoint = new AtomicInteger();
        buttonGameParticipate.findByUser(currentUser)
                .stream()
                .filter(bet -> !bet.getMatch().getIsEnd()).toList()
                .forEach(bet -> betPoint.addAndGet(bet.getBetPoint().intValue()));

        if ((currentUser.getPoint() + betPoint.get()) >= LIMIT_POINT)
            throw new ExpectedException("50만 포인트 이상 보유한 유저는 버튼게임에 참여할 수 없습니다.", HttpStatus.BAD_REQUEST);

        ButtonGameParticipate buttonGameParticipate = ButtonGameParticipate.builder()
                .buttonGame(buttonGame)
                .user(currentUser)
                .buttonType(request.getButtonType())
                .build();

        buttonGameParticipateRepository.save(buttonGameParticipate);
    }

}
