package kr.hhplus.be.server.domain.waiting_queue.components;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.waiting_queue.WaitingQueue;
import kr.hhplus.be.server.domain.waiting_queue.repositories.WaitingQueueReaderRepository;
import kr.hhplus.be.server.domain.waiting_queue.type.WaitingQueueStatus;
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
                .orElseThrow(() -> new RuntimeException("존재하지 않는 토큰입니다."));
    }

    public WaitingQueue readValidTokenByUuidWithLock(String uuid)
    {
        return waitingQueueReaderRepository.readTokensByUuidWithLock(uuid).stream()
                .filter(t -> t.getStatus() != WaitingQueueStatus.EXPIRED)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("존재하지 않는 토큰입니다."));
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

    public WaitingQueue readActiveTokenWithMaxId() {
        return waitingQueueReaderRepository.readActiveTokenLimitBy(PageRequest.of(0, 1)).orElseThrow(() -> new RuntimeException("활성화된 토큰이 없습니다."));
    }

    public boolean isValidTokenExists(User user)
    {
        return user.getWaitingQueueList().stream()
                .anyMatch(t -> t.getStatus() != WaitingQueueStatus.EXPIRED);
    }
}
