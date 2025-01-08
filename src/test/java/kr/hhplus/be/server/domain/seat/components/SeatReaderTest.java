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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
}