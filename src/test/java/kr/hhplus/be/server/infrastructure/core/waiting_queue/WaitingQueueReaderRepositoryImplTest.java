package kr.hhplus.be.server.infrastructure.core.waiting_queue;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import kr.hhplus.be.server.infrastructure.core.user.UserJpaRepository;
import kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository.ACTIVE_TOKEN_SET_NAME;
import static kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository.WAIT_TOKEN_SET_NAME;

@ActiveProfiles("test")
@SpringBootTest
class WaitingQueueReaderRepositoryImplTest {
    @Autowired
    private WaitingQueueReaderRepositoryImpl waitingQueueReaderRepository;

    @Autowired
    private WaitingQueueJpaRepository waitingQueueJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
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
    void readTokensByUuidWithLock() {
        // given
        User user1 = userJpaRepository.save(User.builder().uuid("1234-5678").build());
        User user2 = userJpaRepository.save(User.builder().uuid("8765-4321").build());
        userJpaRepository.flush();

        waitingQueueJpaRepository.saveAll(
                List.of(
                        WaitingQueue.builder().user(user1).build(),
                        WaitingQueue.builder().user(user1).build(),
                        WaitingQueue.builder().user(user2).build()
                )
        );
        waitingQueueJpaRepository.flush();

        // when, then
        Assertions.assertThat(
                waitingQueueReaderRepository.readTokensByUuidWithLock("1234-5678").size()
        ).isEqualTo(2);
    }

    @Transactional
    @Test
    void readAllExpiredTokens() {
        // given
        waitingQueueJpaRepository.saveAll(
                List.of(
                        WaitingQueue.builder().status(WaitingQueueStatus.WAIT).build(),
                        WaitingQueue.builder().status(WaitingQueueStatus.EXPIRED).build(),
                        WaitingQueue.builder().status(WaitingQueueStatus.EXPIRED).build()
                )
        );
        waitingQueueJpaRepository.flush();

        // when, then
        Assertions.assertThat(
                waitingQueueReaderRepository.readAllExpiredTokens().size()
        ).isGreaterThanOrEqualTo(2);
    }

    @Transactional
    @Test
    void readAllActiveTokens() {
        // given
        waitingQueueJpaRepository.saveAll(
                List.of(
                        WaitingQueue.builder().status(WaitingQueueStatus.EXPIRED).build(),
                        WaitingQueue.builder().status(WaitingQueueStatus.ACTIVE).build(),
                        WaitingQueue.builder().status(WaitingQueueStatus.ACTIVE).build()
                )
        );
        waitingQueueJpaRepository.flush();

        // when, then
        Assertions.assertThat(
                waitingQueueReaderRepository.readAllActiveTokens().size()
        ).isGreaterThanOrEqualTo(2);
    }

    @Transactional
    @Test
    void readAllActiveTokensWithLock() {
        // given
        waitingQueueJpaRepository.saveAll(
                List.of(
                        WaitingQueue.builder().status(WaitingQueueStatus.WAIT).build(),
                        WaitingQueue.builder().status(WaitingQueueStatus.ACTIVE).build(),
                        WaitingQueue.builder().status(WaitingQueueStatus.ACTIVE).build()
                )
        );
        waitingQueueJpaRepository.flush();

        // when, then
        Assertions.assertThat(
                waitingQueueReaderRepository.readAllActiveTokensWithLock().size()
        ).isGreaterThanOrEqualTo(2);
    }

    @Transactional
    @Test
    void readWaitTokensLimitBy() {
        // given
        waitingQueueJpaRepository.saveAll(
                List.of(
                        WaitingQueue.builder().status(WaitingQueueStatus.WAIT).build(),
                        WaitingQueue.builder().status(WaitingQueueStatus.WAIT).build(),
                        WaitingQueue.builder().status(WaitingQueueStatus.WAIT).build()
                )
        );
        waitingQueueJpaRepository.flush();

        // when, then
        Assertions.assertThat(
                waitingQueueReaderRepository.readWaitTokensLimitBy(PageRequest.of(0, 2)).size()
        ).isEqualTo(2);
    }

    @Transactional
    @Test
    void readActiveTokenLimitBy() {
        // given
        waitingQueueJpaRepository.saveAll(
                List.of(
                        WaitingQueue.builder().status(WaitingQueueStatus.ACTIVE).build(),
                        WaitingQueue.builder().status(WaitingQueueStatus.ACTIVE).build(),
                        WaitingQueue.builder().status(WaitingQueueStatus.ACTIVE).build()
                )
        );
        waitingQueueJpaRepository.flush();

        // when, then
        Assertions.assertThat(
                waitingQueueReaderRepository.readActiveTokenLimitBy(PageRequest.of(0, 1))
        ).isNotEmpty();
    }

    @Test
    void getWaitingToken() {
        // given
        tokenRedisRepository.zSetAdd(WAIT_TOKEN_SET_NAME, "1234", 1);

        // when
        double score = waitingQueueReaderRepository.getWaitingToken("1234").get();


    }

    @Test
    void getActiveToken() {
        // given
        tokenRedisRepository.zSetAdd(ACTIVE_TOKEN_SET_NAME, "1234", 1);

        // when
        double score = waitingQueueReaderRepository.getActiveToken("1234").get();

        // then
        Assertions.assertThat(score).isEqualTo(1);
    }

    @Test
    void getWaitingNumber() {
        // given
        tokenRedisRepository.zSetAdd(WAIT_TOKEN_SET_NAME, "1234", 3);
        tokenRedisRepository.zSetAdd(WAIT_TOKEN_SET_NAME, "2345", 5);
        tokenRedisRepository.zSetAdd(WAIT_TOKEN_SET_NAME, "3456", 4);

        // when
        Long waitingNumber = waitingQueueReaderRepository.getWaitingNumber("1234").get();

        // then
        Assertions.assertThat(waitingNumber).isEqualTo(0L);
    }

    @Test
    void getActiveTokensCount() {
        // given
        tokenRedisRepository.zSetAdd(ACTIVE_TOKEN_SET_NAME, "1234", 3);
        tokenRedisRepository.zSetAdd(ACTIVE_TOKEN_SET_NAME, "2345", 4);
        tokenRedisRepository.zSetAdd(WAIT_TOKEN_SET_NAME, "3456", 5);

        // when
        Long tokenCount = waitingQueueReaderRepository.getActiveTokensCount();

        // then
        Assertions.assertThat(tokenCount).isEqualTo(2L);
    }
}