package kr.hhplus.be.server.api.seat.application;

import kr.hhplus.be.server.api.seat.dto.GetAvailableSeatsResponse;
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
        // given
        User user = userJpaRepository.save(
                User.builder()
                        .uuid(UUID.randomUUID().toString())
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
        List<Seat> seatList = seatApplication.getAvailableSeatsByDate(
                LocalDate.of(2025,7, 15),
                user.getUuid()
        );

        // then
        Assertions.assertThat(seatList.size()).isEqualTo(50);
    }
}