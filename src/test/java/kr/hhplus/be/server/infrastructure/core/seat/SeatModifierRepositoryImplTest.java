package kr.hhplus.be.server.infrastructure.core.seat;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.type.SeatStatus;
import kr.hhplus.be.server.infrastructure.core.concert.ConcertJpaRepository;
import kr.hhplus.be.server.infrastructure.redis.SeatRedisRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static kr.hhplus.be.server.infrastructure.redis.SeatRedisRepository.UNAVAILABLE_SEAT_SET_PREFIX;
import static kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository.ACTIVE_TOKEN_SET_NAME;
import static kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository.WAIT_TOKEN_SET_NAME;

@ActiveProfiles("test")
@SpringBootTest
class SeatModifierRepositoryImplTest {
    @Autowired
    private SeatModifierRepositoryImpl seatModifierRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;
    @Autowired
    private SeatRedisRepository seatRedisRepository;
    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Transactional
    @Test
    void modifySeat() {
        // given
        Seat seat = seatJpaRepository.save(
                Seat.builder().status(SeatStatus.AVAILABLE).build()
        );

        // when
        seat.setStatus(SeatStatus.RESERVED);
        seatModifierRepository.modifySeat(seat);

        seatJpaRepository.flush();

        // then
        Assertions.assertThat(
                seatJpaRepository.findById(seat.getId()).get().getStatus()
        ).isEqualTo(SeatStatus.RESERVED);
    }

    @Transactional
    @Test
    void setReservedAndAvailable() {
        // given
        Seat seat = seatJpaRepository.findById(1L).get();

        // when
        seatModifierRepository.setReserved(seat);

        // then
        Assertions.assertThat(
                seatRedisRepository
                        .SetMembers(UNAVAILABLE_SEAT_SET_PREFIX + seat.getConcert().getDate())
                        .contains(String.valueOf(seat.getNumber()))
        ).isTrue();

        // when
        seatModifierRepository.setAvailable(seat);

        // then
        Assertions.assertThat(
                seatRedisRepository
                        .SetMembers(UNAVAILABLE_SEAT_SET_PREFIX + seat.getConcert().getDate())
                        .contains(String.valueOf(seat.getNumber()))
        ).isFalse();
    }
}