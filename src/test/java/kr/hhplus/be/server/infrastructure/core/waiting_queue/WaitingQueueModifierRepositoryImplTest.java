package kr.hhplus.be.server.infrastructure.core.waiting_queue;

import kr.hhplus.be.server.domain.waiting_queue.WaitingQueue;
import kr.hhplus.be.server.domain.waiting_queue.type.WaitingQueueStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
class WaitingQueueModifierRepositoryImplTest {
    @Autowired
    private WaitingQueueModifierRepositoryImpl waitingQueueModifierRepository;

    @Autowired
    private WaitingQueueJpaRepository waitingQueueJpaRepository;

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
}