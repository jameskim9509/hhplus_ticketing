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
class UserReaderRepositoryImplTest {
    @Autowired
    UserReaderRepositoryImpl userReaderRepository;

    @Autowired
    UserJpaRepository userJpaRepository;

    @Transactional
    @Test
    void readById() {
        // given
        User user = userJpaRepository.save(User.builder().build());
        userJpaRepository.flush();

        // when, then
        Assertions.assertThat(
                userReaderRepository.readById(user.getId())
        ).isNotEmpty();
    }

    @Transactional
    @Test
    void readByIdWithLock() {
        // given
        User user = userJpaRepository.save(User.builder().build());
        userJpaRepository.flush();

        // when, then
        Assertions.assertThat(
                userReaderRepository.readByIdWithOptimisticLock(user.getId())
        ).isNotEmpty();
    }

    @Transactional
    @Test
    void readByUuid() {
        // given
        User user = userJpaRepository.save(User.builder().uuid("1234-5678").build());
        userJpaRepository.flush();

        // when, then
        Assertions.assertThat(
                userReaderRepository.readByUuid("1234-5678")
        ).isNotEmpty();
    }

    @Transactional
    @Test
    void readByUuidWithLock() {
        // given
        User user = userJpaRepository.save(User.builder().uuid("1234-5678").build());
        userJpaRepository.flush();

        // when, then
        Assertions.assertThat(
                userReaderRepository.readByUuidWithLock("1234-5678")
        ).isNotEmpty();
    }
}