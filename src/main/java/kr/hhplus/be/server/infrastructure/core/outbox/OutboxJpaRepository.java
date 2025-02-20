package kr.hhplus.be.server.infrastructure.core.outbox;

import kr.hhplus.be.server.domain.outbox.Outbox;
import kr.hhplus.be.server.domain.outbox.type.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OutboxJpaRepository extends JpaRepository<Outbox, Long> {
    public List<Outbox> findAllByStatus(OutboxStatus status);
    public Optional<Outbox> findByReservationId(Long reservationId);
}
