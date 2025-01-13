package kr.hhplus.be.server.api.token.application;

import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.components.WaitingQueueModifier;
import kr.hhplus.be.server.domain.token.components.WaitingQueueReader;
import kr.hhplus.be.server.domain.token.components.WaitingQueueWriter;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.components.UserModifier;
import kr.hhplus.be.server.domain.user.components.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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

    public static final Integer MAX_ACTIVE_USER = 10;

    @Override
    @Transactional
    public WaitingQueue createToken(Long userId)
    {
        User user = userReader.readByIdWithLock(userId);
        String uuid = user.getUuid();
        if (Objects.isNull(uuid)) {
            uuid = UUID.randomUUID().toString();
            userModifier.modifyUser(
                    User.builder()
                            .id(user.getId())
                            .balance(user.getBalance())
                            .uuid(uuid)
                            .build()
            );
        }

        if (waitingQueueReader.isValidTokenExists(user))
        {
            throw new RuntimeException("토큰이 이미 존재합니다.");
        }

        List<WaitingQueue> tokenList = waitingQueueReader.readAllActiveTokens();

        WaitingQueue token = WaitingQueue.builder()
                .build();
        token.setUser(user);
        if (tokenList.size() < MAX_ACTIVE_USER) {
            token.setStatus(WaitingQueueStatus.ACTIVE);
            token.setExpiredAt(LocalDateTime.now().plusMinutes(10));
        }
        else
            token.setStatus(WaitingQueueStatus.WAIT);

        waitingQueueWriter.writeToken(token);

        // write Return 바로 가능??
        return token;
    }

    @Override
    @Transactional
    public Long getToken(String uuid)
    {
        User user = userReader.readByUuid(uuid);
        WaitingQueue token = waitingQueueReader.readValidToken(user);

        if (token.getStatus() == WaitingQueueStatus.ACTIVE)
            return 0L;

        WaitingQueue activeToken = waitingQueueReader.readActiveTokenWithMaxId();

        return token.getId() - activeToken.getId();
    }

    @Transactional
    @Scheduled(cron = "")
    public void updateWaitingQueue()
    {
        List<WaitingQueue> tokenList = waitingQueueReader.readAllActiveTokensWithLock();
        tokenList.stream()
                .filter(t -> t.getExpiredAt().isBefore(LocalDateTime.now()))
                .forEach(t -> t.setStatus(WaitingQueueStatus.EXPIRED));

        List<WaitingQueue> activeTokenList = tokenList.stream()
                .filter(t -> t.getStatus() == WaitingQueueStatus.ACTIVE)
                .toList();
        if (activeTokenList.size() < MAX_ACTIVE_USER)
        {
            waitingQueueReader.readWaitTokensLimitBy(PageRequest.of(0, MAX_ACTIVE_USER - activeTokenList.size())).stream()
                    .forEach(t ->
                    {
                        t.setStatus(WaitingQueueStatus.ACTIVE);
                        t.setExpiredAt(LocalDateTime.now().plusMinutes(10));
                        waitingQueueModifier.modifyToken(t);
                    });
        }
    }

    @Transactional
    @Scheduled(cron = "")
    public void deleteExpiredToken()
    {
        waitingQueueModifier.deleteAllTokens(waitingQueueReader.readAllExpiredTokens());
    }
}
