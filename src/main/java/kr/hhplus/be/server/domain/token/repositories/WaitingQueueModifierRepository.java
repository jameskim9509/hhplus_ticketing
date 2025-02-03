package kr.hhplus.be.server.domain.token.repositories;

import kr.hhplus.be.server.domain.token.WaitingQueue;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Set;

public interface WaitingQueueModifierRepository {
    public WaitingQueue modifyToken(WaitingQueue waitingQueue);
    public void deleteAllTokens(List<WaitingQueue> token);
    public List<String> deleteWaitTokens(long count);
    public void deleteAllExpiredTokens(double nanoSeconds);
    public void changeExpiredTime(String uuid, double expiredAt);
}
