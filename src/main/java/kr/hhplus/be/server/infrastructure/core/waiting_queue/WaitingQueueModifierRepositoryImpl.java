package kr.hhplus.be.server.infrastructure.core.waiting_queue;

import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.repositories.WaitingQueueModifierRepository;
import kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository.ACTIVE_TOKEN_SET_NAME;
import static kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository.WAIT_TOKEN_SET_NAME;

@Repository
@RequiredArgsConstructor
public class WaitingQueueModifierRepositoryImpl implements WaitingQueueModifierRepository {
    private final WaitingQueueJpaRepository waitingQueueJpaRepository;
    private final TokenRedisRepository tokenRedisRepository;

    @Override
    public List<String> deleteWaitTokens(long count) {
        return tokenRedisRepository.zSetPopMin(WAIT_TOKEN_SET_NAME, count).stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .toList();
    }

    @Override
    public WaitingQueue modifyToken(WaitingQueue token) {
        return waitingQueueJpaRepository.save(token);
    }

    @Override
    public void deleteAllTokens(List<WaitingQueue> tokenList) {
        waitingQueueJpaRepository.deleteAll(tokenList);
    }

    @Override
    public void deleteAllExpiredTokens(double nanoSeconds) {
        tokenRedisRepository.zSetRemoveRangeByScore(ACTIVE_TOKEN_SET_NAME, 0, nanoSeconds);
    }

    @Override
    public void changeExpiredTime(String uuid, double expiredAt) {
        tokenRedisRepository.zSetModify(ACTIVE_TOKEN_SET_NAME, uuid, expiredAt);
    }
}
