package kr.hhplus.be.server.domain.seat.components;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.repositories.SeatReaderRepository;
import kr.hhplus.be.server.domain.seat.type.SeatStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SeatReaderTest {
    @Mock
    SeatReaderRepository seatReaderRepository;
    @InjectMocks
    SeatReader seatReader;

    @Test
    void getAvailableSeats() {
        // given
        Concert concert = Concert.builder()
                .seatList(
                        List.of(
                                Seat.builder().status(SeatStatus.AVAILABLE).build(),
                                Seat.builder().status(SeatStatus.AVAILABLE).build(),
                                Seat.builder().status(SeatStatus.AVAILABLE).build(),
                                Seat.builder().status(SeatStatus.RESERVED).build()
                        )
                )
                .build();

        // when
        List<Seat> seatList = seatReader.getAvailableSeats(concert);

        // then
        Assertions.assertThat(seatList.size()).isEqualTo(3);
    }

    @Test
    void 이용가능한_좌석이_없으면_에러()
    {
        // given
        Concert concert = Concert.builder()
                .seatList(
                        List.of(
                                Seat.builder().status(SeatStatus.RESERVED).build(),
                                Seat.builder().status(SeatStatus.RESERVED).build(),
                                Seat.builder().status(SeatStatus.RESERVED).build(),
                                Seat.builder().status(SeatStatus.RESERVED).build()
                        )
                )
                .build();

        // when
        // then
        Assertions.assertThatThrownBy(
                () -> seatReader.getAvailableSeats(concert)
        ).isInstanceOf(RuntimeException.class)
        .hasMessage("이용가능한 좌석이 없습니다.");
    }

    @Test
    void 해당_좌석이_없으면_에러_by_concertId_and_seatNumber()
    {
        // given
        Mockito.doReturn(Optional.empty()).when(seatReaderRepository).getSeatByConcertIdAndNumberWithLock(Mockito.anyLong(), Mockito.anyLong());

        // when, then
        Assertions.assertThatThrownBy(
                () -> seatReader.readAvailableSeatByConcertIdAndNumberWithLock(1L, 100L)
        ).isInstanceOf(RuntimeException.class)
        .hasMessage("없는 좌석입니다.");
    }

    @Test
    void 해당_좌석은_있지만_사용가능하지_않으면_에러_by_concertId_and_seatNumber()
    {
        // given
        Mockito.doReturn(Optional.of(
                Seat.builder().status(SeatStatus.RESERVED).build()
        )).when(seatReaderRepository).getSeatByConcertIdAndNumberWithLock(Mockito.anyLong(), Mockito.anyLong());

        // when, then
        Assertions.assertThatThrownBy(
                        () -> seatReader.readAvailableSeatByConcertIdAndNumberWithLock(1L, 100L)
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("사용 가능한 좌석이 아닙니다.");
    }

    @Test
    void 해당_좌석이_없으면_에러_by_seatId()
    {
        // given
        Mockito.doReturn(Optional.empty()).when(seatReaderRepository).getById(Mockito.anyLong());

        // when, then
        Assertions.assertThatThrownBy(
                        () -> seatReader.getById(100L)
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("존재하지 않는 좌석입니다.");
    }
}