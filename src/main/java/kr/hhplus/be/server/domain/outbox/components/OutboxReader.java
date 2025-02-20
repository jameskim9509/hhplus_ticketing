package kr.hhplus.be.server.domain.outbox.components;

import kr.hhplus.be.server.domain.outbox.Outbox;
import kr.hhplus.be.server.domain.outbox.repositories.OutboxReaderRepository;
import kr.hhplus.be.server.domain.outbox.type.OutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxReader {
    private final OutboxReaderRepository outboxReaderRepository;

    public List<Outbox> readAllByStatus(OutboxStatus status)
    {
        return outboxReaderRepository.readAllByStatus(status);
    }

    public Outbox readByReservationId(Long reservationId)
    {
        return outboxReaderRepository.readByReservationId(reservationId)
                .orElseThrow(() -> new RuntimeException(""));
    }
}
