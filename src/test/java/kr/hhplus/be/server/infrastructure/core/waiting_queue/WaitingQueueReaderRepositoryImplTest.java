package kr.hhplus.be.server.infrastructure.core.waiting_queue;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import kr.hhplus.be.server.infrastructure.core.user.UserJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
class WaitingQueueReaderRepositoryImplTest {
    @Autowired
    private WaitingQueueReaderRepositoryImpl waitingQueueReaderRepository;

    @Autowired
    private WaitingQueueJpaRepository waitingQueueJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

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
}