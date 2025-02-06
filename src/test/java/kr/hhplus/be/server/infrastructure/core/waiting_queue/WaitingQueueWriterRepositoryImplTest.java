package kr.hhplus.be.server.infrastructure.core.waiting_queue;

import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository.ACTIVE_TOKEN_SET_NAME;
import static kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository.WAIT_TOKEN_SET_NAME;

@ActiveProfiles("test")
@SpringBootTest
class WaitingQueueWriterRepositoryImplTest {
    @Autowired
    private WaitingQueueWriterRepositoryImpl waitingQueueWriterRepository;

    @Autowired
    private WaitingQueueJpaRepository waitingQueueJpaRepository;
    @Autowired
    private TokenRedisRepository tokenRedisRepository;

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
    void writeToken() {
        // when
        WaitingQueue token = waitingQueueWriterRepository.writeToken(
                WaitingQueue.builder().build()
        );
        waitingQueueJpaRepository.flush();

        // then
        Assertions.assertThat(waitingQueueJpaRepository.findById(token.getId())).isNotEmpty();
    }

    @Test
    void writeWaitingToken() {
        // when
        waitingQueueWriterRepository.writeWaitingToken("1234", 1);

        // then
        Assertions.assertThat(
                tokenRedisRepository.zSetCard(WAIT_TOKEN_SET_NAME)
        ).isEqualTo(1L);
    }

    @Test
    void writeActiveToken() {
        // when
        waitingQueueWriterRepository.writeActiveToken("1234", 1);

        // then
        Assertions.assertThat(
                tokenRedisRepository.zSetCard(ACTIVE_TOKEN_SET_NAME)
        ).isEqualTo(1L);
    }
}