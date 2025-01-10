package kr.hhplus.be.server.infrastructure.core.waiting_queue;

import kr.hhplus.be.server.domain.waiting_queue.WaitingQueue;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class WaitingQueueWriterRepositoryImplTest {
    @Autowired
    private WaitingQueueWriterRepositoryImpl waitingQueueWriterRepository;

    @Autowired
    private WaitingQueueJpaRepository waitingQueueJpaRepository;

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
}