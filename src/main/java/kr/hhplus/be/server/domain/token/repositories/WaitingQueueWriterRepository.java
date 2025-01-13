package kr.hhplus.be.server.domain.token.repositories;

import kr.hhplus.be.server.domain.token.WaitingQueue;

public interface WaitingQueueWriterRepository {
    public WaitingQueue writeToken(WaitingQueue waitingQueue);
}
