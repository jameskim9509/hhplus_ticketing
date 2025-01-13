package kr.hhplus.be.server.api.concert.application;

import kr.hhplus.be.server.api.concert.dto.GetAvailableConcertsResponse;
import kr.hhplus.be.server.domain.concert.Concert;
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


@ActiveProfiles("test")
@SpringBootTest
class ConcertApplicationIntegrationTest {
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private WaitingQueueJpaRepository waitingQueueJpaRepository;

    @Autowired
    private ConcertApplication concertApplication;

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
        List<Concert> concertList = concertApplication.getAvailableConcerts(
                LocalDate.of(2025,7, 1),
                LocalDate.of(2025,8,1),
                "1234-5678"
        );

        // then
        Assertions.assertThat(concertList.size()).isEqualTo(3);
    }
}