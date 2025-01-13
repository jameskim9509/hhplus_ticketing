package kr.hhplus.be.server.api.concert.application;

import kr.hhplus.be.server.domain.concert.components.ConcertReader;
import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.components.WaitingQueueReader;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import kr.hhplus.be.server.domain.user.components.UserReader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ConcertApplicationUnitTest {
    @Mock
    private WaitingQueueReader waitingQueueReader;
    @Mock
    private UserReader userReader;

    @InjectMocks
    private ConcertApplication concertApplication;

    @Test
    void 활성화되지_않은_토큰으로_콘서트_조회시_에러()
    {
        // given
        Mockito.doReturn(
                WaitingQueue.builder().status(WaitingQueueStatus.WAIT).build()
        ).when(waitingQueueReader).readValidToken(Mockito.any());

        // when, then
        Assertions.assertThatThrownBy(
                        () -> concertApplication.getAvailableConcerts(
                                LocalDate.now(),
                                LocalDate.now().plusDays(1),
                                UUID.randomUUID().toString()
                        )
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("활성화되지 않은 토큰입니다.");
    }
}