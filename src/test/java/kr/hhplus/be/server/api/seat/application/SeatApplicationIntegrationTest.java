package kr.hhplus.be.server.api.seat.application;

import kr.hhplus.be.server.api.seat.dto.GetAvailableSeatsResponse;
import kr.hhplus.be.server.common.Interceptor.UserContext;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.core.user.UserJpaRepository;
import kr.hhplus.be.server.infrastructure.core.waiting_queue.WaitingQueueJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@ActiveProfiles("test")
@SpringBootTest
class SeatApplicationIntegrationTest {
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private WaitingQueueJpaRepository waitingQueueJpaRepository;

    @Autowired
    private SeatApplication seatApplication;


    @Transactional
    @Test
    void getAvailableSeatsByDate() {
        // when
        List<Long> seatNumberList = seatApplication.getAvailableSeatsByDate(
                LocalDate.of(2025,7, 15)
        );

        // then
        Assertions.assertThat(seatNumberList.size()).isEqualTo(50);
    }
}