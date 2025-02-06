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
        ).when(userReader).readByIdWithOptimisticLock(Mockito.anyLong());
        Mockito.doReturn(false)
                .when(waitingQueueReader).isWaitingTokenExists(Mockito.any());
        Mockito.doReturn(false)
                .when(waitingQueueReader).isActiveTokenExists(Mockito.any());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);

        // when
        tokenApplication.createToken(1L);

        Mockito.verify(userModifier).modifyUser(userCaptor.capture());
        Mockito.verify(waitingQueueWriter).writeWaitingToken(tokenCaptor.capture(), Mockito.anyDouble());
        User capturedUser = userCaptor.getValue();
        String capturedToken = tokenCaptor.getValue();

        // then
        Assertions.assertThat(capturedUser.getUuid()).isNotNull();
        Assertions.assertThat(capturedToken).isEqualTo(capturedUser.getUuid());
    }

    @Test
    void updateWaitingQueue() {
        // given
        Mockito.doReturn(8L)
                        .when(waitingQueueReader).getActiveTokensCount();
        ArgumentCaptor<Long> changeCountCaptor = ArgumentCaptor.forClass(Long.class);

        // when
        tokenApplication.updateWaitingQueue();

        // then
        Mockito.verify(waitingQueueModifier).deleteWaitTokens(changeCountCaptor.capture());
        Assertions.assertThat(changeCountCaptor.getValue()).isEqualTo(2L);
    }

    @Test
    void 활성화되지_않은_토큰_에러()
    {
        // given
        Mockito.doReturn(false)
                .when(waitingQueueReader).isActiveTokenExists(Mockito.any());

        // when, then
        Assertions.assertThatThrownBy(
                        () -> tokenApplication.validateToken(
                                User.builder().uuid(UUID.randomUUID().toString()).build()
                        )
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("활성화되지 않은 토큰입니다.");
    }

    @Test
    void 대기_토큰이_존재하는데_재발급_받을_경우_에러()
    {
        // given
        Mockito.doReturn(User.builder().build())
                .when(userReader).readByIdWithOptimisticLock(Mockito.anyLong());
        Mockito.doReturn(true)
                .when(waitingQueueReader).isWaitingTokenExists(Mockito.any());

        // when
        Assertions.assertThatThrownBy(
                        () -> tokenApplication.createToken(1L)
                ).isInstanceOf(RuntimeException.class).hasMessage("토큰이 이미 존재합니다.");
    }

    @Test
    void 활성_토큰이_존재하는데_재발급_받을_경우_에러()
    {
        // given
        Mockito.doReturn(User.builder().build())
                .when(userReader).readByIdWithOptimisticLock(Mockito.anyLong());
        Mockito.doReturn(false)
                .when(waitingQueueReader).isWaitingTokenExists(Mockito.any());
        Mockito.doReturn(true)
                .when(waitingQueueReader).isActiveTokenExists(Mockito.any());

        // then, when
        Assertions.assertThatThrownBy(
                () -> tokenApplication.createToken(1L)
        ).isInstanceOf(RuntimeException.class).hasMessage("토큰이 이미 존재합니다.");
    }
}