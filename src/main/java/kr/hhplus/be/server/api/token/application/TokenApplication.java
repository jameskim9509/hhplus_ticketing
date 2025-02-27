package kr.hhplus.be.server.api.token.application;

import kr.hhplus.be.server.common.Interceptor.UserContext;
import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.token.components.WaitingQueueModifier;
import kr.hhplus.be.server.domain.token.components.WaitingQueueReader;
import kr.hhplus.be.server.domain.token.components.WaitingQueueWriter;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.components.UserModifier;
import kr.hhplus.be.server.domain.user.components.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenApplication implements TokenUsecase{
    private final UserReader userReader;
    private final UserModifier userModifier;
    private final WaitingQueueReader waitingQueueReader;
    private final WaitingQueueWriter waitingQueueWriter;
    private final WaitingQueueModifier waitingQueueModifier;

    public static final Long MAX_ACTIVE_USER = 30L;
    public static final Long ACTIVE_TOKEN_LIFETIME_IN_MINUTES = 10L;

    @Override
    @Transactional
    public String createToken(Long userId)
    {
        User user = userReader.readByIdWithOptimisticLock(userId);
        String uuid = user.getUuid();
        if (Objects.isNull(uuid)) {
            uuid = UUID.randomUUID().toString();
            user.setUuid(uuid);
            userModifier.modifyUser(user);
        }

        if (waitingQueueReader.isWaitingTokenExists(uuid)
                || waitingQueueReader.isActiveTokenExists(uuid))
        {
            throw new ConcertException(ErrorCode.TOKEN_ALREADY_EXISTS);
        }

        LocalDateTime now = LocalDateTime.now();
        waitingQueueWriter.writeWaitingToken(
                uuid,
                now.toEpochSecond(ZoneOffset.UTC) * 1_000_000_000 + now.getNano()
        );

        return uuid;
    }

    @Override
    @Transactional
    public Long getToken()
    {
        User user = UserContext.getContext();
        return waitingQueueReader.getWaitingNumber(user.getUuid());
    }

    @Transactional
    public void validateToken(User user)
    {
        if (!waitingQueueReader.isActiveTokenExists(user.getUuid()))
            throw new ConcertException(ErrorCode.TOKEN_IS_INVALID);
    }

    @Transactional
//    @Scheduled(cron = "0/30 * * * * *")
    @Scheduled(cron = "*/5 * * * * *") // for LoadTest
    public void updateWaitingQueue()
    {
        // 큐토큰 갱신 -> 제거의 순서로써 제거를 지연하여 다른 비즈니스 로직과의 동시성 문제 방지
        // 다음 갱신될 대기자가 스케줄링을 한번 더 기다려야하는 문제가 발생할 수 있음 (동시성 문제에 비해 감수할 만한 문제라고 판단)
        Long activeTokensCount = waitingQueueReader.getActiveTokensCount();

        if (activeTokensCount < MAX_ACTIVE_USER)
        {
            LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(ACTIVE_TOKEN_LIFETIME_IN_MINUTES);
            waitingQueueModifier.deleteWaitTokens(MAX_ACTIVE_USER - activeTokensCount)
                    .forEach(
                            uuid -> waitingQueueWriter.writeActiveToken(
                                    uuid,
                                    expiredAt.toEpochSecond(ZoneOffset.UTC) * 1_000_000_000 + expiredAt.getNano()
                            )
                    );
        }

        // delete Token 스케줄러를 updateToken 스케줄러와 합침으로써 스케줄러간 충돌 해결
        LocalDateTime now = LocalDateTime.now();
        waitingQueueModifier.deleteAllExpiredTokens(
                now.toEpochSecond(ZoneOffset.UTC) * 1_000_000_000 + now.getNano()
        );
    }
}
