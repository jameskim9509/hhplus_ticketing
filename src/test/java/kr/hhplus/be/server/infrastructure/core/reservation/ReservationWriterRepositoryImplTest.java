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
class ReservationWriterRepositoryImplTest {
    @Autowired
    private ReservationWriterRepositoryImpl reservationWriterRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @Transactional
    @Test
    void writeReservation()
    {
        // given, when
        Reservation reservation = reservationWriterRepository.writeReservation(
                Reservation.builder().status(ReservationStatus.RESERVED).build()
        );

        reservationJpaRepository.flush();

        // then
        Assertions.assertThat(
                reservationJpaRepository.findById(reservation.getId()).get().getStatus()
        ).isEqualTo(ReservationStatus.RESERVED);
    }
}