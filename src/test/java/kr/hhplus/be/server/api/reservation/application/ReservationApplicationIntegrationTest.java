package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.api.token.application.TokenApplication;
import kr.hhplus.be.server.common.Interceptor.UserContext;
import kr.hhplus.be.server.domain.outbox.components.OutboxReader;
import kr.hhplus.be.server.domain.outbox.type.OutboxStatus;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.type.ReservationStatus;
import kr.hhplus.be.server.domain.seat.type.SeatStatus;
import kr.hhplus.be.server.domain.token.components.WaitingQueueReader;
import kr.hhplus.be.server.domain.token.components.WaitingQueueWriter;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.core.outbox.OutboxJpaRepository;
import kr.hhplus.be.server.infrastructure.core.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.core.seat.SeatJpaRepository;
import kr.hhplus.be.server.infrastructure.core.user.UserJpaRepository;
import kr.hhplus.be.server.infrastructure.core.waiting_queue.WaitingQueueJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.org.apache.commons.lang3.time.StopWatch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


@ActiveProfiles("test")
@SpringBootTest
class ReservationApplicationIntegrationTest {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private WaitingQueueJpaRepository waitingQueueJpaRepository;
    @Autowired
    private ReservationJpaRepository reservationJpaRepository;
    @Autowired
    private SeatJpaRepository seatJpaRepository;
    @Autowired
    private OutboxJpaRepository outboxJpaRepository;

    @Autowired
    private WaitingQueueReader waitingQueueReader;
    @Autowired
    private WaitingQueueWriter waitingQueueWriter;
    @Autowired
    private OutboxReader outboxReader;

    @Autowired
    private ReservationApplication reservationApplication;
    @Autowired
    private TokenApplication tokenApplication;

    @Test
    void reserveSeat() throws InterruptedException {
        // given
        User user = userJpaRepository.save(
                User.builder()
                        .balance(100_000L)
                        .uuid(UUID.randomUUID().toString())
                        .build()
        );
        userJpaRepository.flush();

        waitingQueueWriter.writeActiveToken(user.getUuid(),1234 );

        UserContext.setContext(user);

        // when
        Reservation reservation =
                reservationApplication.reserveSeat(
                        LocalDate.of(2025, 7, 1),
                        25L
                );

        // kafka 발행을 기다림
        Thread.sleep(3000);

        // then
        Assertions.assertThat(
                outboxReader.readByReservationId(reservation.getId()).getStatus()
        ).isEqualTo(OutboxStatus.PUBLISHED);
        Assertions.assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PAYMENT_REQUIRED);
        Assertions.assertThat(reservation.getSeat().getStatus()).isEqualTo(SeatStatus.RESERVED);

        // data 원상 복구
        reservation.getSeat().setStatus(SeatStatus.AVAILABLE);
        seatJpaRepository.save(reservation.getSeat());
        userJpaRepository.delete(user);
        outboxJpaRepository.delete(outboxReader.readByReservationId(reservation.getId()));
        reservationJpaRepository.delete(reservation);
    }

    @Test
    void 동시에_30번_예약하면_29번_오류() {
        // given
        User user = userJpaRepository.save(
                User.builder()
                        .uuid(UUID.randomUUID().toString())
                        .build()
        );
        userJpaRepository.flush();

        waitingQueueWriter.writeActiveToken(user.getUuid(),1234 );

        // when
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            futures.add(CompletableFuture.supplyAsync(
                    () -> {
                        StopWatch timer = new StopWatch();
                        timer.start();
                        try {
                            UserContext.setContext(user);

                            reservationApplication.reserveSeat(
                                    LocalDate.of(2025, 7, 1),
                                    30L
                            );
                            timer.stop();
                            logger.info(
                                    "정상 메소드 실행 시간: {}ms", timer.getTime(TimeUnit.MILLISECONDS)
                            );
                            return true;
                        } catch (RuntimeException re) {
                            timer.stop();
                            logger.info(
                                    "비정상 메소드 실행 시간: {}ms", timer.getTime(TimeUnit.MILLISECONDS)
                            );
                            return false;
                        }
                    }
            ));
        }

        CompletableFuture[] futuresArray = futures.toArray(new CompletableFuture[futures.size()]);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        CompletableFuture.allOf(futuresArray)
                .thenRun(() ->
                {
                    Long fail_count = futures.stream()
                            .filter(f -> {
                                try {
                                    return !f.get();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .count();

                    Assertions.assertThat(fail_count).isEqualTo(29L);
                }).join();

        stopWatch.stop();
        logger.info("총 소요시간: {}ms", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }
}