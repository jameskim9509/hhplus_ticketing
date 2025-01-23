package kr.hhplus.be.server.infrastructure.core.user;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.repositories.UserReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserReaderRepositoryImpl implements UserReaderRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> readByIdWithOptimisticLock(Long userId) {
        return userJpaRepository.findByIdWithOptimisticLock(userId);
    }

    @Override
    public Optional<User> readByIdWithPessimisticLock(Long userId) {
        return userJpaRepository.findByIdWithPessimisticLock(userId);
    }

    @Override
    public Optional<User> readById(Long userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public Optional<User> readByUuid(String uuid) {
        return userJpaRepository.findByUuid(uuid);
    }

    @Override
    public Optional<User> readByUuidWithLock(String uuid) {
        return userJpaRepository.findByUuidWithLock(uuid);
    }
}
