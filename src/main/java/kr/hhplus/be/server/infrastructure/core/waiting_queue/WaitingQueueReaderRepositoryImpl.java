package kr.hhplus.be.server.infrastructure.core.waiting_queue;

import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.repositories.WaitingQueueReaderRepository;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WaitingQueueReaderRepositoryImpl implements WaitingQueueReaderRepository {
    private final WaitingQueueJpaRepository waitingQueueJpaRepository;

    @Override
    public List<WaitingQueue> readTokensByUuidWithLock(String uuid) {
        return waitingQueueJpaRepository.findAllByUuidWithLock(uuid);
    }

    @Override
    public List<WaitingQueue> readAllExpiredTokens() {
        return waitingQueueJpaRepository.findAllByStatus(WaitingQueueStatus.EXPIRED);
    }

    @Override
    public List<WaitingQueue> readAllActiveTokens() {
        return waitingQueueJpaRepository.findAllByStatus(WaitingQueueStatus.ACTIVE);
    }

    @Override
    public List<WaitingQueue> readAllActiveTokensWithLock() {
        return waitingQueueJpaRepository.findAllByStatusWithLock(WaitingQueueStatus.ACTIVE);
    }

    @Override
    public List<WaitingQueue> readWaitTokensLimitBy(Pageable pageable) {
        return waitingQueueJpaRepository.findAllByStatusASCWithPage(WaitingQueueStatus.WAIT, pageable);
    }

    @Override
    public Optional<WaitingQueue> readActiveTokenLimitBy(Pageable pageable) {
        return waitingQueueJpaRepository.findAllByStatusDESCWithPage(WaitingQueueStatus.ACTIVE, pageable).stream().findFirst();
    }
}
