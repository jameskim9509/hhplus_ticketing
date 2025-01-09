package kr.hhplus.be.server.infrastructure.core.waiting_queue;

import kr.hhplus.be.server.domain.waiting_queue.WaitingQueue;
import kr.hhplus.be.server.domain.waiting_queue.repositories.WaitingQueueModifierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WaitingQueueModifierRepositoryImpl implements WaitingQueueModifierRepository {
    private final WaitingQueueJpaRepository waitingQueueJpaRepository;

    @Override
    public WaitingQueue modifyToken(WaitingQueue token) {
        return waitingQueueJpaRepository.save(token);
    }

    @Override
    public void deleteAllTokens(List<WaitingQueue> tokenList) {
        waitingQueueJpaRepository.deleteAll(tokenList);
    }
}
