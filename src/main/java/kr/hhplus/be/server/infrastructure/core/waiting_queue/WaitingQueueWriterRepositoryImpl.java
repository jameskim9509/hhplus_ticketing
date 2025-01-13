package kr.hhplus.be.server.infrastructure.core.waiting_queue;

import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.repositories.WaitingQueueWriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WaitingQueueWriterRepositoryImpl implements WaitingQueueWriterRepository {
    private final WaitingQueueJpaRepository waitingQueueJpaRepository;

    @Override
    public WaitingQueue writeToken(WaitingQueue token) {
        return waitingQueueJpaRepository.save(token);
    }
}
