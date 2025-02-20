package kr.hhplus.be.server.domain.outbox.components;

import kr.hhplus.be.server.domain.outbox.Outbox;
import kr.hhplus.be.server.domain.outbox.repositories.OutboxCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxCommander {
    private final OutboxCommandRepository outboxCommandRepository;

    public void writeOutbox(Outbox outbox)
    {
        outboxCommandRepository.writeOutbox(outbox);
    }
}
