package kr.hhplus.be.server.infrastructure.core.outbox;

import kr.hhplus.be.server.domain.outbox.Outbox;
import kr.hhplus.be.server.domain.outbox.repositories.OutboxReaderRepository;
import kr.hhplus.be.server.domain.outbox.type.OutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OutboxReaderRepositoryImpl implements OutboxReaderRepository {
    private final OutboxJpaRepository outboxJpaRepository;

    @Override
    public List<Outbox> readAllByStatus(OutboxStatus status) {
        return outboxJpaRepository.findAllByStatus(status);
    }

    @Override
    public Optional<Outbox> readByReservationId(Long reservationId) {
        return outboxJpaRepository.findByReservationId(reservationId);
    }
}
