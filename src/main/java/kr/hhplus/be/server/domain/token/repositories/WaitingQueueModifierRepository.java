package kr.hhplus.be.server.domain.token.repositories;

import kr.hhplus.be.server.domain.token.WaitingQueue;

import java.util.List;

public interface WaitingQueueModifierRepository {
    public WaitingQueue modifyToken(WaitingQueue waitingQueue);
    public void deleteAllTokens(List<WaitingQueue> token);
}
