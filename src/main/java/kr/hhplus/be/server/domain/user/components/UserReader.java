package kr.hhplus.be.server.domain.user.components;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.repositories.UserReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserReader {
    private final UserReaderRepository userReaderRepository;

    public User readById(Long userId)
    {
        return userReaderRepository.readById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
    }

    public User readByUuid(String uuid)
    {
        return userReaderRepository.readByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
    }

    public User readByUuidWithLock(String uuid)
    {
        return userReaderRepository.readByUuidWithLock(uuid)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
    }
}
