package kr.hhplus.be.server.domain.user.components;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.repositories.UserModifierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserModifier {
    public static final Long MAX_POINT = 1_000_000L;

    private final UserModifierRepository userModifierRepository;

    public User modifyUser(User user)
    {
        return userModifierRepository.modifyUser(user);
    }
    public int modifyUserWithoutVersion(User user)
    {
        return userModifierRepository.modifyUserWithoutVersion(user);
    }
}
