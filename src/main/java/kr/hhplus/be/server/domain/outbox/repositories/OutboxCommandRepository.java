package kr.hhplus.be.server.domain.outbox.repositories;

import kr.hhplus.be.server.domain.outbox.Outbox;

public interface OutboxCommandRepository {
    public void writeOutbox(Outbox outbox);
}
