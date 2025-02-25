package kr.hhplus.be.server.api.payment.application;

import kr.hhplus.be.server.api.reservation.application.ReservationApplication;
import kr.hhplus.be.server.common.Interceptor.UserContext;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.type.ReservationStatus;
import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.components.WaitingQueueWriter;
import kr.hhplus.be.server.domain.token.repositories.WaitingQueueWriterRepository;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.core.reservation.ReservationJpaRepository;
import kr.hhplus.be.server.infrastructure.core.user.UserJpaRepository;
import kr.hhplus.be.server.infrastructure.core.waiting_queue.WaitingQueueJpaRepository;
import kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.apache.commons.lang3.time.StopWatch;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository.ACTIVE_TOKEN_SET_NAME;
import static kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository.WAIT_TOKEN_SET_NAME;


@ActiveProfiles("test")
@SpringBootTest
class PaymentApplicationIntegrationTest {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserJpaRepository userJpaRepository;
    @Autowired
    WaitingQueueJpaRepository waitingQueueJpaRepository;
    @Autowired
    ReservationJpaRepository reservationJpaRepository;
    @Autowired
    WaitingQueueWriter waitingQueueWriter;
    @Autowired
    TokenRedisRepository tokenRedisRepository;

    @Autowired
    PaymentApplication paymentApplication;

    @AfterEach
    void clearRedisDB()
    {
        tokenRedisRepository.zSetRemoveRangeByScore(
                ACTIVE_TOKEN_SET_NAME, Double.MIN_VALUE, Double.MAX_VALUE
        );
        tokenRedisRepository.zSetRemoveRangeByScore(
                WAIT_TOKEN_SET_NAME, Double.MIN_VALUE, Double.MAX_VALUE
        );
    }

    @Transactional
    @Test
    void pay() {
        // given
        User user = userJpaRepository.save(
                User.builder()
                        .balance(100_000L)
                        .uuid(UUID.randomUUID().toString())
                        .build()
        );
        userJpaRepository.flush();

        waitingQueueWriter.writeActiveToken(user.getUuid(), 1234);

        Reservation reservation = reservationJpaRepository.save(
                Reservation.builder()
                        .user(user)
                        .seatCost(30_000L)
                        .status(ReservationStatus.PAYMENT_REQUIRED)
                        .expiredAt(LocalDateTime.now().plusMinutes(1))
                        .build()
        );
        reservationJpaRepository.flush();

        UserContext.setContext(user);

        // when
        Payment payment =
                paymentApplication.pay(
                        reservation.getId()
                );

        // then
        Assertions.assertThat(payment.getReservation().getStatus()).isEqualTo(ReservationStatus.RESERVED);
        Assertions.assertThat(payment.getUser().getBalance()).isEqualTo(70_000L);
    }

    @Test
    void 동시에_30번_결제하면_29번_오류()
    {
        // given
        User user = userJpaRepository.save(
                User.builder()
                        .balance(100_000L)
                        .uuid(UUID.randomUUID().toString())
                        .build()
        );
        userJpaRepository.flush();

        waitingQueueWriter.writeActiveToken(user.getUuid(), 1234);

        Reservation reservation =
                reservationJpaRepository.save(
                        Reservation.builder()
                                .status(ReservationStatus.PAYMENT_REQUIRED)
                                .user(user)
                                .seatCost(10000L)
                                .expiredAt(LocalDateTime.now().plusMinutes(5))
                                .build()
                );
        reservationJpaRepository.flush();

        // when, then
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for(int i = 0; i < 30; i++) {
            futures.add(CompletableFuture.supplyAsync(
                    () -> {
                        StopWatch timer = new StopWatch();
                        timer.start();

                        try {
                            UserContext.setContext(user);

                            paymentApplication.pay(
                                    reservation.getId()
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
                    Long success_count = futures.stream()
                            .filter(f -> {
                                try {
                                    return !f.get();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .count();

                    Assertions.assertThat(success_count).isEqualTo(29L);
                }).join();

        stopWatch.stop();
        logger.info("총 소요시간: {}ms", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }
}