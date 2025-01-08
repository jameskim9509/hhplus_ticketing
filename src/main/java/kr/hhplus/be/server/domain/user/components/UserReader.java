package kr.hhplus.be.server.domain.user.components;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.repositories.UserReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserReader {
    UserReaderRepository userReaderRepository;

    public User readById(Long userId)
    {
        return userReaderRepository.readById(userId).get();
    }

    public User readByUuid(String uuid)
    {
        return userReaderRepository.readByUuid(uuid).get();
    }

    public User readByUuidWithLock(String uuid)
    {
        return userReaderRepository.readByUuidWithLock(uuid).get();
    }
}
