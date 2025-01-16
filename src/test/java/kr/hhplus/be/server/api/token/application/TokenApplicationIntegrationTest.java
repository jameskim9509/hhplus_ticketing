package kr.hhplus.be.server.api.token.application;

import kr.hhplus.be.server.api.token.dto.CreateTokenResponse;
import kr.hhplus.be.server.api.token.dto.GetTokenResponse;
import kr.hhplus.be.server.common.Interceptor.UserContext;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@ActiveProfiles("test")
@SpringBootTest
class TokenApplicationIntegrationTest {
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private TokenApplication tokenApplication;

    @Autowired
    private WaitingQueueJpaRepository waitingQueueJpaRepository;

    @Transactional
    @Test
    void 토큰_생성_후_조회_성공() {
        // given
        User user = userJpaRepository.save(
                User.builder()
                        .build()
        );
        userJpaRepository.flush();

        UserContext.setContext(user);

        // when
        WaitingQueue waitingQueue = tokenApplication.createToken(user.getId());
        String uuid = waitingQueue.getUser().getUuid();

        Long waitingNumber = tokenApplication.getToken();

        // then
        Assertions.assertThat(waitingNumber).isNotNull();
    }

    @Test
    void 동시에_3번_토큰_생성시_2번_오류()
    {
        // given
        User user = userJpaRepository.save(
                User.builder()
                        .build()
        );
        userJpaRepository.flush();

        // when, then
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            futures.add(CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            tokenApplication.createToken(user.getId());
                            return true;
                        } catch (RuntimeException re) {
                            System.out.println(re.getMessage());
                            return false;
                        }
                    }
            ));
        }

        CompletableFuture[] futuresArray = futures.toArray(new CompletableFuture[futures.size()]);

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

                    Assertions.assertThat(success_count).isEqualTo(2L);
                }).join();
    }

    @Test
    void 토큰_생성_요청이_10개_이상일_경우_10명만_활성화() throws InterruptedException {
        // given
        List<Long> userIdList = new ArrayList<>();
        for (int i = 0; i < 15; i++)
        {
            User user = userJpaRepository.save(
                    User.builder().build()
            );
            userIdList.add(user.getId());
        }

        userJpaRepository.flush();

        int threadCount = userIdList.size();
        ExecutorService excecutorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++)
        {
            final Long userId = userIdList.get(i);
            excecutorService.submit(() -> {
                try{
                    tokenApplication.createToken(userId);
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
                waitingQueueJpaRepository.findAllByStatus(WaitingQueueStatus.ACTIVE).size()
        ).isEqualTo(10);
    }
}