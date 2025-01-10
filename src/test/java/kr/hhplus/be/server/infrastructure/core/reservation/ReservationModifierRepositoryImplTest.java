package kr.hhplus.be.server.infrastructure.core.reservation;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.type.ReservationStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class ReservationModifierRepositoryImplTest {
    @Autowired
    private ReservationModifierRepositoryImpl reservationModifierRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @Transactional
    @Test
    void modifyReservation() {
        // given
        Reservation reservation = reservationJpaRepository.save(
                Reservation.builder().build()
        );

        // when
        reservation.setStatus(ReservationStatus.RESERVED);
        reservationModifierRepository.modifyReservation(reservation);

        reservationJpaRepository.flush();

        // then
        Assertions.assertThat(
                reservationJpaRepository.findById(reservation.getId()).get().getStatus()
        ).isEqualTo(ReservationStatus.RESERVED);
    }
}