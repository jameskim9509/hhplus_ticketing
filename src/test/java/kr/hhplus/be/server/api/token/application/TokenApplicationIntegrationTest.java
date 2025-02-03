package kr.hhplus.be.server.api.token.application;

import kr.hhplus.be.server.api.token.dto.CreateTokenResponse;
import kr.hhplus.be.server.api.token.dto.GetTokenResponse;
import kr.hhplus.be.server.common.Interceptor.UserContext;
import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.components.WaitingQueueReader;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.core.user.UserJpaRepository;
import kr.hhplus.be.server.infrastructure.core.waiting_queue.WaitingQueueJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;


@ActiveProfiles("test")
@SpringBootTest
class TokenApplicationIntegrationTest {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private TokenApplication tokenApplication;

    @Autowired
    private WaitingQueueReader waitingQueueReader;

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
        tokenApplication.createToken(user.getId());

        Long waitingNumber = tokenApplication.getToken();

        // then
        Assertions.assertThat(waitingNumber).isNotNull();
    }

    @Test
    void 동시에_30번_토큰_생성시_29번_오류()
    {
        // given
        User user = userJpaRepository.save(
                User.builder()
                        .build()
        );
        userJpaRepository.flush();

        // when, then
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for(int i = 0; i < 30; i++) {
            futures.add(CompletableFuture.supplyAsync(
                    () -> {
                        StopWatch timer = new StopWatch();
                        timer.start();
                        try {
                            tokenApplication.createToken(user.getId());

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

        tokenApplication.updateWaitingQueue();

        //then
        Assertions.assertThat(
                waitingQueueReader.getActiveTokensCount()
        ).isEqualTo(10L);
    }
}