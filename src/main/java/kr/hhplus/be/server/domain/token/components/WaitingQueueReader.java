package kr.hhplus.be.server.domain.token.components;

import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.repositories.WaitingQueueReaderRepository;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WaitingQueueReader {
    private final WaitingQueueReaderRepository waitingQueueReaderRepository;

    public WaitingQueue readValidToken(User user)
    {
        return user.getWaitingQueueList().stream()
                .filter(t -> t.getStatus() != WaitingQueueStatus.EXPIRED)
                .findFirst()
                .orElseThrow(() -> new ConcertException(ErrorCode.TOKEN_NOT_FOUND));
    }

    public WaitingQueue readValidTokenByUuidWithLock(String uuid)
    {
        return waitingQueueReaderRepository.readTokensByUuidWithLock(uuid).stream()
                .filter(t -> t.getStatus() != WaitingQueueStatus.EXPIRED)
                .findFirst()
                .orElseThrow(() -> new ConcertException(ErrorCode.TOKEN_NOT_FOUND));
    }

    public List<WaitingQueue> readAllExpiredTokens()
    {
        return waitingQueueReaderRepository.readAllExpiredTokens();
    }

    public List<WaitingQueue> readAllActiveTokens()
    {
        return waitingQueueReaderRepository.readAllActiveTokens();
    }

    public List<WaitingQueue> readAllActiveTokensWithLock()
    {
        return waitingQueueReaderRepository.readAllActiveTokensWithLock();
    }


    public List<WaitingQueue> readWaitTokensLimitBy(Pageable pageable)
    {
        return waitingQueueReaderRepository.readWaitTokensLimitBy(pageable);
    }

    public WaitingQueue readActiveTokenWithMaxId()
    {
        return waitingQueueReaderRepository.readActiveTokenLimitBy(PageRequest.of(0, 1)).orElseThrow(() -> new ConcertException(ErrorCode.ACTIVE_TOKEN_NOT_FOUND));
    }

    public boolean isWaitingTokenExists(String uuid)
    {
        return waitingQueueReaderRepository.getWaitingToken(uuid).isPresent();
    }

    public boolean isActiveTokenExists(String uuid)
    {
        return waitingQueueReaderRepository.getActiveToken(uuid).isPresent();
    }

    public long getWaitingNumber(String uuid)
    {
        if(waitingQueueReaderRepository.getActiveToken(uuid).isPresent())
            return 0L;

        return waitingQueueReaderRepository.getWaitingNumber(uuid)
                .orElseThrow(() -> new ConcertException(ErrorCode.TOKEN_NOT_FOUND));
    }

    public long getActiveTokensCount()
    {
        return waitingQueueReaderRepository.getActiveTokensCount();
    }
}
