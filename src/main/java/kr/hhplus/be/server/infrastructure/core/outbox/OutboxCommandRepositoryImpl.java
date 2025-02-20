package kr.hhplus.be.server.infrastructure.core.outbox;

import kr.hhplus.be.server.domain.outbox.Outbox;
import kr.hhplus.be.server.domain.outbox.repositories.OutboxCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OutboxCommandRepositoryImpl implements OutboxCommandRepository {
    private final OutboxJpaRepository outboxJpaRepository;

    @Override
    public void writeOutbox(Outbox outbox) {
        outboxJpaRepository.save(outbox);
    }
}
