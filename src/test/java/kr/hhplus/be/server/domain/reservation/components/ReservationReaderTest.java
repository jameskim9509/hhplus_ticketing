package kr.hhplus.be.server.domain.reservation.components;

import kr.hhplus.be.server.domain.reservation.repositories.ReservationReaderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ReservationReaderTest {
    @Mock
    private ReservationReaderRepository reservationReaderRepository;

    @InjectMocks
    ReservationReader reservationReader;

    @Test
    void 예약이_존재하지_않으면_에러_by_id()
    {
        // given
        Mockito.doReturn(Optional.empty()).when(reservationReaderRepository).readByIdWithLock(Mockito.anyLong());

        // when, then
        Assertions.assertThatThrownBy(
                () -> reservationReader.readByIdWithLock(30L)
        ).isInstanceOf(RuntimeException.class)
        .hasMessage("존재하지 않는 예약입니다.");
    }
}