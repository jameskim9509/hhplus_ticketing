package kr.hhplus.be.server.domain.token.repositories;

import kr.hhplus.be.server.domain.token.WaitingQueue;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface WaitingQueueReaderRepository {
    public List<WaitingQueue> readTokensByUuidWithLock(String uuid);
    public List<WaitingQueue> readAllExpiredTokens();
    public List<WaitingQueue> readAllActiveTokens();
    public List<WaitingQueue> readAllActiveTokensWithLock();
    public List<WaitingQueue> readWaitTokensLimitBy(Pageable pageable);
    public Optional<WaitingQueue> readActiveTokenLimitBy(Pageable pageable);
}
