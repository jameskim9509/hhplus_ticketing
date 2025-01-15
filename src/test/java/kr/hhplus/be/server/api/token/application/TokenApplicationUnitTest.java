package kr.hhplus.be.server.api.token.application;

import kr.hhplus.be.server.common.Interceptor.UserContext;
import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.components.WaitingQueueModifier;
import kr.hhplus.be.server.domain.token.components.WaitingQueueReader;
import kr.hhplus.be.server.domain.token.components.WaitingQueueWriter;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.components.UserModifier;
import kr.hhplus.be.server.domain.user.components.UserReader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TokenApplicationUnitTest {
    @Mock
    private UserReader userReader;
    @Mock
    private UserModifier userModifier;
    @Mock
    private WaitingQueueReader waitingQueueReader;
    @Mock
    private WaitingQueueWriter waitingQueueWriter;
    @Mock
    private WaitingQueueModifier waitingQueueModifier;

    @InjectMocks
    private TokenApplication tokenApplication;

    @Test
    void createToken() {
        //given
        Mockito.doReturn(
                User.builder().build()
        ).when(userReader).readByIdWithLock(Mockito.anyLong());
        Mockito.doReturn(
                List.of(
                        WaitingQueue.builder().build(),
                        WaitingQueue.builder().build(),
                        WaitingQueue.builder().build(),
                        WaitingQueue.builder().build(),
                        WaitingQueue.builder().build(),
                        WaitingQueue.builder().build(),
                        WaitingQueue.builder().build(),
                        WaitingQueue.builder().build(),
                        WaitingQueue.builder().build(),
                        WaitingQueue.builder().build()
                )
        ).when(waitingQueueReader).readAllActiveTokens();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<WaitingQueue> tokenCaptor =ArgumentCaptor.forClass(WaitingQueue.class);

        // when
        tokenApplication.createToken(1L);

        Mockito.verify(userModifier).modifyUser(userCaptor.capture());
        Mockito.verify(waitingQueueWriter).writeToken(tokenCaptor.capture());
        User capturedUser = userCaptor.getValue();
        WaitingQueue capturedToken = tokenCaptor.getValue();

        // then
        Assertions.assertThat(capturedUser.getUuid()).isNotNull();
        Assertions.assertThat(capturedToken.getStatus()).isEqualTo(WaitingQueueStatus.WAIT);
    }

    @Test
    void getActiveToken() {
        // given
        UserContext.setContext(
                User.builder().uuid(UUID.randomUUID().toString()).build()
        );

        Mockito.doReturn(WaitingQueue.builder().status(WaitingQueueStatus.ACTIVE).build()).when(waitingQueueReader).readValidToken(Mockito.any());

        // when
        Long waitingNumber = tokenApplication.getToken();

        // then
        Assertions.assertThat(waitingNumber).isEqualTo(0L);
    }

    @Test
    void getWaitToken() {
        // given
        UserContext.setContext(
                User.builder().uuid(UUID.randomUUID().toString()).build()
        );

        Mockito.doReturn(
                WaitingQueue.builder()
                        .id(20L)
                        .status(WaitingQueueStatus.WAIT)
                        .build()
        ).when(waitingQueueReader).readValidToken(Mockito.any());

        Mockito.doReturn(
                WaitingQueue.builder()
                        .id(10L)
                        .status(WaitingQueueStatus.ACTIVE)
                        .build()
        ).when(waitingQueueReader).readActiveTokenWithMaxId();

        // when
        Long waitingNumber = tokenApplication.getToken();

        // then
        Assertions.assertThat(waitingNumber).isEqualTo(10L);
    }

    @Test
    void updateWaitingQueue() {
        // given
        Mockito.doReturn(
                List.of(
                        WaitingQueue.builder()
                                .status(WaitingQueueStatus.ACTIVE)
                                .expiredAt(LocalDateTime.now().minusMinutes(1))
                                .build(),
                        WaitingQueue.builder()
                                .status(WaitingQueueStatus.ACTIVE)
                                .expiredAt(LocalDateTime.now().minusMinutes(1))
                                .build(),
                        WaitingQueue.builder()
                                .status(WaitingQueueStatus.ACTIVE)
                                .expiredAt(LocalDateTime.now().plusMinutes(1))
                                .build(),
                        WaitingQueue.builder()
                                .status(WaitingQueueStatus.ACTIVE)
                                .expiredAt(LocalDateTime.now().plusMinutes(1))
                                .build(),
                        WaitingQueue.builder()
                                .status(WaitingQueueStatus.ACTIVE)
                                .expiredAt(LocalDateTime.now().plusMinutes(1))
                                .build()
                )
        ).when(waitingQueueReader).readAllActiveTokensWithLock();
        Mockito.doReturn(
                List.of(
                        WaitingQueue.builder()
                                .status(WaitingQueueStatus.WAIT)
                                .build(),
                        WaitingQueue.builder()
                                .status(WaitingQueueStatus.WAIT)
                                .build(),
                        WaitingQueue.builder()
                                .status(WaitingQueueStatus.WAIT)
                                .build()
                )
        ).when(waitingQueueReader).readWaitTokensLimitBy(Mockito.any());

        ArgumentCaptor<PageRequest> pageCaptor = ArgumentCaptor.forClass(PageRequest.class);

        // when
        tokenApplication.updateWaitingQueue();

        // then
        Mockito.verify(waitingQueueReader).readWaitTokensLimitBy(pageCaptor.capture());
        Mockito.verify(waitingQueueModifier, Mockito.times(3)).modifyToken(Mockito.any());

        PageRequest pageRequest = pageCaptor.getValue();
        Assertions.assertThat(pageRequest.getPageSize()).isEqualTo(7);
    }

    @Test
    void 활성화되지_않은_토큰으로_콘서트_조회시_에러()
    {
        // given
        Mockito.doReturn(
                WaitingQueue.builder().status(WaitingQueueStatus.WAIT).build()
        ).when(waitingQueueReader).readValidToken(Mockito.any());

        // when, then
        Assertions.assertThatThrownBy(
                        () -> tokenApplication.validateToken(
                                User.builder().uuid(UUID.randomUUID().toString()).build()
                        )
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("활성화되지 않은 토큰입니다.");
    }

    @Test
    void 이미_토큰이_존재하는데_재발급_받을_경우_에러()
    {
        // given
        Mockito.doReturn(User.builder().build())
                .when(userReader).readByIdWithLock(Mockito.anyLong());
        Mockito.doReturn(true)
                .when(waitingQueueReader)
                .isValidTokenExists(Mockito.any());

        // when
        Assertions.assertThatThrownBy(
                        () -> tokenApplication.createToken(1L)
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("토큰이 이미 존재합니다.");
    }
}