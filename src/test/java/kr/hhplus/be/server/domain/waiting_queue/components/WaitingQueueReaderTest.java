package kr.hhplus.be.server.domain.waiting_queue.components;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.waiting_queue.WaitingQueue;
import kr.hhplus.be.server.domain.waiting_queue.repositories.WaitingQueueReaderRepository;
import kr.hhplus.be.server.domain.waiting_queue.type.WaitingQueueStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WaitingQueueReaderTest {

    @Mock
    private WaitingQueueReaderRepository waitingQueueReaderRepository;
    @InjectMocks
    private WaitingQueueReader waitingQueueReader;

    @Test
    void readValidToken() {
        // given
        User user = User.builder()
                .waitingQueueList(
                        List.of(
                                WaitingQueue.builder().status(WaitingQueueStatus.EXPIRED).build(),
                                WaitingQueue.builder().status(WaitingQueueStatus.EXPIRED).build(),
                                WaitingQueue.builder().status(WaitingQueueStatus.WAIT).build()
                        )
                )
                .build();
        // when
        WaitingQueue token = waitingQueueReader.readValidToken(user);

        // then
        Assertions.assertThat(token.getStatus()).isEqualTo(WaitingQueueStatus.WAIT);
    }

    @Test
    void readValidTokenByUuidWithLock() {
        // given
        String uuid = UUID.randomUUID().toString();

        Mockito.doReturn(
                List.of(
                        WaitingQueue.builder().status(WaitingQueueStatus.EXPIRED).build(),
                        WaitingQueue.builder().status(WaitingQueueStatus.EXPIRED).build(),
                        WaitingQueue.builder().status(WaitingQueueStatus.WAIT).build()
                )
        ).when(waitingQueueReaderRepository).readTokensByUuidWithLock(Mockito.anyString());

        // when
        WaitingQueue token = waitingQueueReader.readValidTokenByUuidWithLock(uuid);

        // then
        Assertions.assertThat(token.getStatus()).isEqualTo(WaitingQueueStatus.WAIT);
    }

    @Test
    void isValidTokenExists() {
        // given
        User user = User.builder()
                .waitingQueueList(
                        List.of(
                                WaitingQueue.builder().status(WaitingQueueStatus.EXPIRED).build(),
                                WaitingQueue.builder().status(WaitingQueueStatus.EXPIRED).build(),
                                WaitingQueue.builder().status(WaitingQueueStatus.EXPIRED).build()
                        )
                ).build();
        // when
        boolean isValidTokenExists = waitingQueueReader.isValidTokenExists(user);

        // then
        Assertions.assertThat(isValidTokenExists).isFalse();
    }
}