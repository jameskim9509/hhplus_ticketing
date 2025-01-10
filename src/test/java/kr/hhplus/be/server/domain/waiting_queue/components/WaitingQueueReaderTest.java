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
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
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

    @Test
    void 유효한_토큰이_없으면_에러()
    {
        // given
        User user = User.builder()
                .waitingQueueList(
                        List.of(
                                WaitingQueue.builder().status(WaitingQueueStatus.EXPIRED).build(),
                                WaitingQueue.builder().status(WaitingQueueStatus.EXPIRED).build(),
                                WaitingQueue.builder().status(WaitingQueueStatus.EXPIRED).build()
                        )
                ).build();

        // then
        Assertions.assertThatThrownBy(
                () -> waitingQueueReader.readValidToken(user))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("존재하지 않는 토큰입니다.");
    }

    @Test
    void 유효한_토큰이_없으면_에러_with_lock()
    {
        // given
        Mockito.doReturn(
                List.of(
                        WaitingQueue.builder().status(WaitingQueueStatus.EXPIRED).build(),
                        WaitingQueue.builder().status(WaitingQueueStatus.EXPIRED).build(),
                        WaitingQueue.builder().status(WaitingQueueStatus.EXPIRED).build()
                )
        ).when(waitingQueueReaderRepository).readTokensByUuidWithLock(Mockito.anyString());

        // when, then
        Assertions.assertThatThrownBy(
                () -> waitingQueueReader.readValidTokenByUuidWithLock(UUID.randomUUID().toString())
        ).isInstanceOf(RuntimeException.class)
        .hasMessage("존재하지 않는 토큰입니다.");
    }

    @Test
    void 활성화된_토큰이_없으면_에러()
    {
        // given
        Mockito.doReturn(Optional.empty())
                .when(waitingQueueReaderRepository)
                .readActiveTokenLimitBy(Mockito.any());

        // when, then
        Assertions.assertThatThrownBy(
                () -> waitingQueueReader.readActiveTokenWithMaxId()
        ).isInstanceOf(RuntimeException.class)
        .hasMessage("활성화된 토큰이 없습니다.");
    }
}