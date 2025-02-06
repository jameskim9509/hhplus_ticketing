package kr.hhplus.be.server.api.concert.application;

import kr.hhplus.be.server.api.concert.dto.AvailableConcertDtoList;
import kr.hhplus.be.server.domain.concert.components.ConcertReader;
import kr.hhplus.be.server.infrastructure.core.user.UserJpaRepository;
import kr.hhplus.be.server.infrastructure.core.waiting_queue.WaitingQueueJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@ActiveProfiles("test")
@SpringBootTest
class ConcertApplicationIntegrationTest {
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private WaitingQueueJpaRepository waitingQueueJpaRepository;

    @MockitoSpyBean
    private ConcertReader concertReader;

    @Autowired
    private ConcertApplication concertApplication;

    @AfterEach
    void clearCache()
    {
        concertApplication.clearAvailableConcerts();
    }

    @Test
    void 이용가능한_콘서트_조회_캐싱_테스트_성공()
    {
        // when
        for(int i = 0; i < 3; i++ )
        {
            concertApplication.getAvailableConcerts(
                    LocalDate.of(2025,7, 1),
                    LocalDate.of(2025,8 ,1)
            );
        }

        // then
        Mockito.verify(concertReader, Mockito.times(1))
                .readByDateBetween(Mockito.any(), Mockito.any());
    }

    @Test
    void getAvailableConcerts() {
        // when
        AvailableConcertDtoList concertList = concertApplication.getAvailableConcerts(
                LocalDate.of(2025,7, 1),
                LocalDate.of(2025,8,1)
        );

        // then
        Assertions.assertThat(concertList.getAvailableConcertDtoList().size()).isEqualTo(3);
    }

    @Test
    void 동시에_콘서트_조회시_캐시스탬피드현상_방지_성공() throws InterruptedException {
        int threadCount = 3;
        ExecutorService excecutorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++)
        {
            excecutorService.submit(() -> {
                try{
                    concertApplication.getAvailableConcerts(
                            LocalDate.of(2025,7, 1),
                            LocalDate.of(2025,8 ,1)
                    );
                }
                finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        excecutorService.shutdown();

        // then
        Mockito.verify(concertReader, Mockito.times(1))
                .readByDateBetween(Mockito.any(), Mockito.any());
    }
}