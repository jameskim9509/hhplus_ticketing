package kr.hhplus.be.server.api.token.application;

import kr.hhplus.be.server.api.token.dto.CreateTokenResponse;
import kr.hhplus.be.server.api.token.dto.GetTokenResponse;
import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.core.user.UserJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@ActiveProfiles("test")
@SpringBootTest
class TokenApplicationIntegrationTest {
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private TokenApplication tokenApplication;

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
        WaitingQueue waitingQueue = tokenApplication.createToken(user.getId());
        String uuid = waitingQueue.getUser().getUuid();

        Long waitingNumber = tokenApplication.getToken(uuid);

        // then
        Assertions.assertThat(waitingNumber).isEqualTo(0L);
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

        // when
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
}