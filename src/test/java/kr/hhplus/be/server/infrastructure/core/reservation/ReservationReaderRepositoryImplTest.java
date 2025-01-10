package kr.hhplus.be.server.infrastructure.core.reservation;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.type.ReservationStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class ReservationReaderRepositoryImplTest {
    @Autowired
    private ReservationReaderRepositoryImpl reservationReaderRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @Test
    @Transactional
    void readById() {
        // given
        Reservation reservation = reservationJpaRepository.save(Reservation.builder().build());
        reservationJpaRepository.flush();

        // when
        Reservation expectedReservation = reservationReaderRepository.readById(reservation.getId()).get();

        // then
        Assertions.assertThat(expectedReservation).isNotNull();
    }

    @Test
    @Transactional
    void readByIdWithLock() {
        // given
        Reservation reservation = reservationJpaRepository.save(Reservation.builder().build());
        reservationJpaRepository.flush();

        // when
        Reservation expectedReservation = reservationReaderRepository.readByIdWithLock(reservation.getId()).get();

        // then
        Assertions.assertThat(expectedReservation).isNotNull();
    }

    @Test
    @Transactional
    void readAllPaymentRequiredWithLock() {
        // given
        List<Reservation> reservationList = List.of(
                Reservation.builder().status(ReservationStatus.RESERVED).build(),
                Reservation.builder().status(ReservationStatus.PAYMENT_REQUIRED).build(),
                Reservation.builder().status(ReservationStatus.PAYMENT_REQUIRED).build(),
                Reservation.builder().status(ReservationStatus.PAYMENT_REQUIRED).build()
        );
        reservationJpaRepository.saveAll(reservationList);

        reservationJpaRepository.flush();

        // when
        List<Reservation> expectedReservationList = reservationReaderRepository.readAllPaymentRequiredWithLock();

        // then
        Assertions.assertThat(expectedReservationList.size()).isEqualTo(3);
    }
}