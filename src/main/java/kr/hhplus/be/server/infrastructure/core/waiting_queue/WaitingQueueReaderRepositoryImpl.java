package kr.hhplus.be.server.infrastructure.core.waiting_queue;

import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.repositories.WaitingQueueReaderRepository;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository.ACTIVE_TOKEN_SET_NAME;
import static kr.hhplus.be.server.infrastructure.redis.TokenRedisRepository.WAIT_TOKEN_SET_NAME;

@Repository
@RequiredArgsConstructor
public class WaitingQueueReaderRepositoryImpl implements WaitingQueueReaderRepository {
    private final WaitingQueueJpaRepository waitingQueueJpaRepository;
    private final TokenRedisRepository tokenRedisRepository;

    @Override
    public List<WaitingQueue> readTokensByUuidWithLock(String uuid) {
        return waitingQueueJpaRepository.findAllByUuidWithLock(uuid);
    }

    @Override
    public List<WaitingQueue> readAllExpiredTokens() {
        return waitingQueueJpaRepository.findAllByStatus(WaitingQueueStatus.EXPIRED);
    }

    @Override
    public List<WaitingQueue> readAllActiveTokens() {
        return waitingQueueJpaRepository.findAllByStatus(WaitingQueueStatus.ACTIVE);
    }

    @Override
    public List<WaitingQueue> readAllActiveTokensWithLock() {
        return waitingQueueJpaRepository.findAllByStatusWithLock(WaitingQueueStatus.ACTIVE);
    }

    @Override
    public List<WaitingQueue> readWaitTokensLimitBy(Pageable pageable) {
        return waitingQueueJpaRepository.findAllByStatusASCWithPage(WaitingQueueStatus.WAIT, pageable);
    }

    @Override
    public Optional<WaitingQueue> readActiveTokenLimitBy(Pageable pageable) {
        return waitingQueueJpaRepository.findAllByStatusDESCWithPage(WaitingQueueStatus.ACTIVE, pageable).stream().findFirst();
    }

    @Override
    public Optional<Double> getWaitingToken(String uuid) {
        return Optional.ofNullable(
                tokenRedisRepository.zSetScore(WAIT_TOKEN_SET_NAME, uuid)
        );
    }

    @Override
    public Optional<Double> getActiveToken(String uuid) {
        return Optional.ofNullable(
                tokenRedisRepository.zSetScore(ACTIVE_TOKEN_SET_NAME, uuid)
        );
    }

    @Override
    public Optional<Long> getWaitingNumber(String uuid) {
        return Optional.ofNullable(
                tokenRedisRepository.zSetRank(WAIT_TOKEN_SET_NAME, uuid)
        );
    }

    @Override
    public long getActiveTokensCount() {
        return tokenRedisRepository.zSetCard(ACTIVE_TOKEN_SET_NAME);
    }
}
