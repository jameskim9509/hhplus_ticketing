package kr.hhplus.be.server.infrastructure.core.user;

import kr.hhplus.be.server.domain.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class UserModifierRepositoryImplTest {

    @Autowired
    private UserModifierRepositoryImpl userModifierRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Transactional
    @Test
    void modifyUser() {
        // given
        User user = userJpaRepository.save(User.builder().balance(0L).build());

        // when
        user.chargePoint(1000L);
        userModifierRepository.modifyUser(user);

        userJpaRepository.flush();

        // then
        Assertions.assertThat(
                userJpaRepository.findById(user.getId()).get().getBalance()
        ).isEqualTo(1000L);
    }
}