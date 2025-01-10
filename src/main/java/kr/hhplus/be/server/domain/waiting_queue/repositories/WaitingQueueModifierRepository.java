package kr.hhplus.be.server.domain.waiting_queue.repositories;

import kr.hhplus.be.server.domain.waiting_queue.WaitingQueue;

import java.util.List;

public interface WaitingQueueModifierRepository {
    public WaitingQueue modifyToken(WaitingQueue waitingQueue);
    public void deleteAllTokens(List<WaitingQueue> token);
}
