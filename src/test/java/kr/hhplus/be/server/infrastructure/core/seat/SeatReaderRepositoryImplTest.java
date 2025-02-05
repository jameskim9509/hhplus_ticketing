package kr.hhplus.be.server.infrastructure.core.seat;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.infrastructure.redis.SeatRedisRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static kr.hhplus.be.server.infrastructure.redis.SeatRedisRepository.UNAVAILABLE_SEAT_SET_PREFIX;

@ActiveProfiles("test")
@SpringBootTest
class SeatReaderRepositoryImplTest {
    @Autowired
    private SeatReaderRepositoryImpl seatReaderRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;
    @Autowired
    private SeatRedisRepository seatRedisRepository;

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

    @Test
    void getUnavailableSeatsByDate() {
        // given
        seatRedisRepository.SetAdd(UNAVAILABLE_SEAT_SET_PREFIX + LocalDate.now(), String.valueOf(1));

        // when
        List<Long> seatNumberList = seatReaderRepository.getUnavailableSeatsByDate(LocalDate.now());

        // then
        Assertions.assertThat(seatNumberList.contains(1L)).isTrue();

        // after
        seatRedisRepository.SetRemove(
                UNAVAILABLE_SEAT_SET_PREFIX + LocalDate.now(), String.valueOf(1)
        );
    }
}