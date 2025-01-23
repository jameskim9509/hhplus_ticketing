package kr.hhplus.be.server.domain.user.components;

import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;
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
                .orElseThrow(() -> new ConcertException(ErrorCode.USER_NOT_FOUND));
    }

    public User readByIdWithOptimisticLock(Long userId)
    {
        return userReaderRepository.readByIdWithOptimisticLock(userId)
                .orElseThrow(() -> new ConcertException(ErrorCode.USER_NOT_FOUND));
    }

    public User readByIdWithPessimisticLock(Long userId)
    {
        return userReaderRepository.readByIdWithPessimisticLock(userId)
                .orElseThrow(() -> new ConcertException(ErrorCode.USER_NOT_FOUND));
    }

    public User readByUuid(String uuid)
    {
        return userReaderRepository.readByUuid(uuid)
                .orElseThrow(() -> new ConcertException(ErrorCode.USER_NOT_FOUND));
    }

    public User readByUuidWithLock(String uuid)
    {
        return userReaderRepository.readByUuidWithLock(uuid)
                .orElseThrow(() -> new ConcertException(ErrorCode.USER_NOT_FOUND));
    }
}
