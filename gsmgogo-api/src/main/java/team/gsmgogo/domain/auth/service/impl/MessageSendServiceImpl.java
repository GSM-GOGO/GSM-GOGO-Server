package team.gsmgogo.domain.auth.service.impl;

import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.gsmgogo.domain.auth.entity.VerifyCodeRedisEntity;
import team.gsmgogo.domain.auth.repository.VerifyCodeJpaRepository;
import team.gsmgogo.domain.auth.service.MessageSendService;
import team.gsmgogo.domain.user.entity.UserEntity;
import team.gsmgogo.domain.user.enums.IsVerify;
import team.gsmgogo.domain.user.repository.UserJpaRepository;
import team.gsmgogo.global.exception.error.ExpectedException;
import team.gsmgogo.global.facade.UserFacade;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class MessageSendServiceImpl implements MessageSendService {
    private final UserFacade userFacade;
    private final DefaultMessageService messageService;
    private final VerifyCodeJpaRepository verifyCodeJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Value("${message.send}")
    private String sendNumber;

    @Override
    @Transactional
    public void execute(String phoneNumber) {
        UserEntity user = userFacade.getCurrentUser();

        if(user.getIsVerify() == IsVerify.VERIFY)
            throw new ExpectedException("이미 인증된 전화번호가 존재합니다.", HttpStatus.BAD_REQUEST);

        if(user.getVerifyCount() >= 5)
            throw new ExpectedException("하루에 인증은 5번만 할 수 있습니다.", HttpStatus.NO_CONTENT);

        String generatedCode = generateCode();

        Message message = new Message();
        message.setFrom(sendNumber);
        message.setTo(phoneNumber);
        message.setText("GSM GOGO v2 [" + generatedCode + "]를 입력해주세요!");

        messageService.sendOne(new SingleMessageSendingRequest(message));

        VerifyCodeRedisEntity verifyCode = VerifyCodeRedisEntity.builder()
            .userId(user.getUserId())
            .code(generatedCode)
            .expiredAt(600000L)
            .build();

        user.plusCount();
        user.setPhoneNumber(phoneNumber);

        verifyCodeJpaRepository.save(verifyCode);
        userJpaRepository.save(user);
    }

    @Override
    public String test(String phoneNumber) {
        Long id = userFacade.getCurrentUser().getUserId();
        String generatedCode = generateCode();

        VerifyCodeRedisEntity verifyCode = VerifyCodeRedisEntity.builder()
                .userId(id)
                .code(generatedCode)
                .expiredAt(600000L)
                .build();

        verifyCodeJpaRepository.save(verifyCode);

        return "GSM GOGO v2 [" + generatedCode + "]를 입력해주세요!";
    }

    private String generateCode(){
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }
}
