package kr.hhplus.be.server.infrastructure.core.waiting_queue;

import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository.ACTIVE_TOKEN_SET_NAME;
import static kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository.WAIT_TOKEN_SET_NAME;

@ActiveProfiles("test")
@SpringBootTest
class WaitingQueueModifierRepositoryImplTest {
    @Autowired
    private WaitingQueueModifierRepositoryImpl waitingQueueModifierRepository;

    @Autowired
    private TokenRedisRepository tokenRedisRepository;
    @Autowired
    private WaitingQueueJpaRepository waitingQueueJpaRepository;

    @AfterEach
    void clearRedisDB()
    {
        tokenRedisRepository.zSetRemoveRangeByScore(
                ACTIVE_TOKEN_SET_NAME, Double.MIN_VALUE, Double.MAX_VALUE
        );
        tokenRedisRepository.zSetRemoveRangeByScore(
                WAIT_TOKEN_SET_NAME, Double.MIN_VALUE, Double.MAX_VALUE
        );
    }

    @Transactional
    @Test
    void modifyToken() {
        // given
        WaitingQueue token = waitingQueueJpaRepository.save(
                WaitingQueue.builder().build()
        );
        waitingQueueJpaRepository.flush();

        // when
        token.setStatus(WaitingQueueStatus.ACTIVE);
        waitingQueueModifierRepository.modifyToken(token);
        waitingQueueJpaRepository.flush();

        // then
        Assertions.assertThat(
                waitingQueueJpaRepository.findById(token.getId()).get().getStatus()
        ).isEqualTo(WaitingQueueStatus.ACTIVE);

    }

    @Transactional
    @Test
    void deleteAllTokens() {
        // given
        WaitingQueue token = waitingQueueJpaRepository.save(
                WaitingQueue.builder().build()
        );
        waitingQueueJpaRepository.flush();

        // when
        waitingQueueModifierRepository.deleteAllTokens(List.of(token));
        waitingQueueJpaRepository.flush();

        // then
        Assertions.assertThat(waitingQueueJpaRepository.findById(token.getId())).isEmpty();
    }

    @Test
    void deleteWaitTokens() {
        // given
        tokenRedisRepository.zSetAdd(WAIT_TOKEN_SET_NAME, "1234", 1);
        tokenRedisRepository.zSetAdd(WAIT_TOKEN_SET_NAME, "2345", 2);
        tokenRedisRepository.zSetAdd(WAIT_TOKEN_SET_NAME, "3456", 3);

        // when
        List<String> deletedTokens = waitingQueueModifierRepository.deleteWaitTokens(2);

        // then
        Assertions.assertThat(deletedTokens.size()).isEqualTo(2L);
        Assertions.assertThat(
                tokenRedisRepository.zSetScore(WAIT_TOKEN_SET_NAME, "3456")
        ).isEqualTo(3);
    }

    @Test
    void deleteAllExpiredTokens() {
        // given
        tokenRedisRepository.zSetAdd(ACTIVE_TOKEN_SET_NAME, "1234", 1);
        tokenRedisRepository.zSetAdd(ACTIVE_TOKEN_SET_NAME, "2345", 2);
        tokenRedisRepository.zSetAdd(ACTIVE_TOKEN_SET_NAME, "3456", 10);

        // when
        waitingQueueModifierRepository.deleteAllExpiredTokens(5);

        // then
        Assertions.assertThat(
                tokenRedisRepository.zSetScore(ACTIVE_TOKEN_SET_NAME,"3456")
        ).isEqualTo(10);
    }

    @Test
    void changeExpiredTime() {
        // given
        tokenRedisRepository.zSetAdd(ACTIVE_TOKEN_SET_NAME, "1234", 1);

        // when
        waitingQueueModifierRepository.changeExpiredTime("1234", 10);

        // then
        Assertions.assertThat(
                tokenRedisRepository.zSetScore(ACTIVE_TOKEN_SET_NAME, "1234")
        ).isEqualTo(10);
    }
}