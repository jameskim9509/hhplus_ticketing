package kr.hhplus.be.server.api.user.application;

import kr.hhplus.be.server.api.user.dto.ChargePointResponse;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.core.user.UserJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@ActiveProfiles("test")
@SpringBootTest
class UserApplicationIntegrationTest {
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserApplication userApplication;

    @Transactional
    @Test
    void chargePoint() {
        // given
        User user = userJpaRepository.save(
                User.builder()
                        .balance(0L)
                        .uuid(UUID.randomUUID().toString())
                        .build()
        );
        userJpaRepository.flush();

        // when
        User returnedUser =
                userApplication.chargePoint(
                        50_000L, user.getUuid()
                );

        // then
        Assertions.assertThat(returnedUser.getBalance()).isEqualTo(50_000L);
    }

    @Test
    void 동시에_3번_충전시_순차적_충전() throws InterruptedException {
        // given
        User user = userJpaRepository.save(
                User.builder()
                        .balance(0L)
                        .uuid(UUID.randomUUID().toString())
                        .build()
        );
        userJpaRepository.flush();

        int threadCount = 3;
        ExecutorService excecutorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++)
        {
            excecutorService.submit(() -> {
                try{
                    userApplication.chargePoint(
                            10000L,
                            user.getUuid()
                    );
                }
                finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        excecutorService.shutdown();

        //then
        Assertions.assertThat(
                userJpaRepository.findById(user.getId()).get().getBalance()
        ).isEqualTo(30000L);
    }
}