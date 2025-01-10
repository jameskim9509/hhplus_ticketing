package kr.hhplus.be.server.infrastructure.core.seat;

import kr.hhplus.be.server.domain.seat.Seat;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
class SeatReaderRepositoryImplTest {
    @Autowired
    private SeatReaderRepositoryImpl seatReaderRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @Transactional
    @Test
    void getSeatsByConcertId() {
        // when
        List<Seat> seatList = seatReaderRepository.getSeatsByConcertId(1L);

        // then
        Assertions.assertThat(seatList.size()).isEqualTo(50);
    }

    @Transactional
    @Test
    void getSeatByConcertIdAndNumberWithLock() {
        // when
        Seat seat = seatReaderRepository.getSeatByConcertIdAndNumberWithLock(1L, 10L).get();

        // then
        Assertions.assertThat(seat.getCost()).isEqualTo(50000L);
    }

    @Transactional
    @Test
    void getById() {
        // given
        Seat seat = seatJpaRepository.save(Seat.builder().build());
        seatJpaRepository.flush();

        // when
        Seat expectedSeat = seatReaderRepository.getById(seat.getId()).get();

        // then
        Assertions.assertThat(expectedSeat).isNotNull();
    }
}