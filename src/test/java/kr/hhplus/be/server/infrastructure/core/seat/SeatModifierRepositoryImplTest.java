package kr.hhplus.be.server.infrastructure.core.seat;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.type.SeatStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class SeatModifierRepositoryImplTest {
    @Autowired
    private SeatModifierRepositoryImpl seatModifierRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;

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
}