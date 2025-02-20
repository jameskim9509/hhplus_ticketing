package kr.hhplus.be.server.domain.outbox.repositories;

import kr.hhplus.be.server.domain.outbox.Outbox;
import kr.hhplus.be.server.domain.outbox.type.OutboxStatus;

import java.util.List;
import java.util.Optional;

public interface OutboxReaderRepository {
    public List<Outbox> readAllByStatus(OutboxStatus status);
    public Optional<Outbox> readByReservationId(Long reservationId);
}
