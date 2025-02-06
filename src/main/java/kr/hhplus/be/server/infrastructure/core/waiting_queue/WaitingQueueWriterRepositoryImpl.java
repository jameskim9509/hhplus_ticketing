package kr.hhplus.be.server.infrastructure.core.waiting_queue;

import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.repositories.WaitingQueueWriterRepository;
import kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository.ACTIVE_TOKEN_SET_NAME;
import static kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository.WAIT_TOKEN_SET_NAME;

@Repository
@RequiredArgsConstructor
public class WaitingQueueWriterRepositoryImpl implements WaitingQueueWriterRepository {
    private final WaitingQueueJpaRepository waitingQueueJpaRepository;
    private final TokenRedisRepository tokenRedisRepository;

    @Override
    public WaitingQueue writeToken(WaitingQueue token) {
        return waitingQueueJpaRepository.save(token);
    }

    @Override
    public boolean writeWaitingToken(String uuid, double createdAt) {
        return tokenRedisRepository.zSetAdd(WAIT_TOKEN_SET_NAME, uuid, createdAt);
    }

    @Override
    public boolean writeActiveToken(String uuid, double expiredAt) {
        return tokenRedisRepository.zSetAdd(ACTIVE_TOKEN_SET_NAME, uuid, expiredAt);
    }
}
