package kr.hhplus.be.server.application;

import kr.hhplus.be.server.domain.reservation.type.ReservationStatus;
import kr.hhplus.be.server.domain.seat.type.SeatStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.waiting_queue.WaitingQueue;
import kr.hhplus.be.server.domain.waiting_queue.type.WaitingQueueStatus;
import kr.hhplus.be.server.dto.*;
import kr.hhplus.be.server.infrastructure.core.user.UserJpaRepository;
import kr.hhplus.be.server.infrastructure.core.waiting_queue.WaitingQueueJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@ActiveProfiles("test")
@SpringBootTest
class ConcertUsecaseIntegrationTest {
    @Autowired
    ConcertUsecase concertUsecase;

    @Autowired
    UserJpaRepository userJpaRepository;
    @Autowired
    WaitingQueueJpaRepository waitingQueueJpaRepository;

    @Transactional
    @Test
    void 토큰_생성_후_조회_성공() {
        // given
        User user = userJpaRepository.save(
                User.builder()
                .build()
        );
        userJpaRepository.flush();

        // when
        CreateTokenResponse createTokenResponse = concertUsecase.createToken(user.getId());
        String uuid = createTokenResponse.uuid();

        GetTokenResponse getTokenResponse = concertUsecase.getToken(uuid);

        // then
        Assertions.assertThat(getTokenResponse.waitingNumber()).isEqualTo(0L);
    }

    @Transactional
    @Test
    void getAvailableConcerts() {
        // given
        User user = userJpaRepository.save(
                User.builder()
                        .uuid("1234-5678")
                        .build()
        );
        userJpaRepository.flush();

        WaitingQueue token = WaitingQueue.builder()
                .status(WaitingQueueStatus.ACTIVE)
                .build();
        token.setUser(user);
        waitingQueueJpaRepository.save(token);
        waitingQueueJpaRepository.flush();

        // when
        GetAvailableConcertsResponse response = concertUsecase.getAvailableConcerts(
                LocalDate.of(2025,7, 1),
                LocalDate.of(2025,8,1),
                "1234-5678"
        );

        // then
        Assertions.assertThat(response.availableConcerts().size()).isEqualTo(3);
    }

    @Transactional
    @Test
    void getAvailableSeatsByDate() {
        // given
        User user = userJpaRepository.save(
                User.builder()
                        .uuid("1234-5678")
                        .build()
        );
        userJpaRepository.flush();

        WaitingQueue token = WaitingQueue.builder()
                .status(WaitingQueueStatus.ACTIVE)
                .build();
        token.setUser(user);
        waitingQueueJpaRepository.save(token);
        waitingQueueJpaRepository.flush();

        // when
        GetAvailableSeatsResponse response = concertUsecase.getAvailableSeatsByDate(
                LocalDate.of(2025,7, 1),
                "1234-5678"
        );

        // then
        Assertions.assertThat(response.availableSeats().size()).isEqualTo(50);
    }

    @Transactional
    @Test
    void 예약_후_결제_성공() {
        // given
        User user = userJpaRepository.save(
                User.builder()
                        .balance(100_000L)
                        .uuid("1234-5678")
                        .build()
        );
        userJpaRepository.flush();

        WaitingQueue token = WaitingQueue.builder()
                .status(WaitingQueueStatus.ACTIVE)
                .build();
        token.setUser(user);
        waitingQueueJpaRepository.save(token);
        waitingQueueJpaRepository.flush();

        // when
        ReserveSeatResponse reserveSeatResponse =
                concertUsecase.reserveSeat(
                        LocalDate.of(2025,7,1),
                        30L,
                        "1234-5678"
                );

        // then
        Assertions.assertThat(reserveSeatResponse.result().getStatus()).isEqualTo(ReservationStatus.PAYMENT_REQUIRED);
        Assertions.assertThat(reserveSeatResponse.result().getSeat().getStatus()).isEqualTo(SeatStatus.RESERVED);

        // when
        ReserveSeatResponse payResponse =
                concertUsecase.pay(
                        reserveSeatResponse.result().getId(),
                        "1234-5678"
                );

        // then
        Assertions.assertThat(payResponse.result().getStatus()).isEqualTo(ReservationStatus.RESERVED);
        Assertions.assertThat(payResponse.result().getUser().getBalance()).isEqualTo(70_000L);
    }

    @Transactional
    @Test
    void chargePoint() {
        // given
        User user = userJpaRepository.save(
                User.builder()
                        .balance(0L)
                        .uuid("1234-5678")
                        .build()
        );

        // when
        ChargePointResponse response =
                concertUsecase.chargePoint(
                50_000L, "1234-5678"
        );

        // then
        Assertions.assertThat(response.balance()).isEqualTo(50_000L);
    }
}