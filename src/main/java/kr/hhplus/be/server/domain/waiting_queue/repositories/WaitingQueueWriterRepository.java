package kr.hhplus.be.server.domain.waiting_queue.repositories;

import kr.hhplus.be.server.domain.waiting_queue.WaitingQueue;

public interface WaitingQueueWriterRepository {
    public WaitingQueue writeToken(WaitingQueue waitingQueue);
}
