package kr.hhplus.be.server.infrastructure.core.user;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.repositories.UserModifierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserModifierRepositoryImpl implements UserModifierRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public User modifyUser(User user) {
        return userJpaRepository.save(user);
    }
}
