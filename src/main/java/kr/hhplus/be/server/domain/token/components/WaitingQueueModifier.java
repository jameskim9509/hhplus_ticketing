package kr.hhplus.be.server.domain.token.components;

import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.repositories.WaitingQueueModifierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WaitingQueueModifier {
    private final WaitingQueueModifierRepository waitingQueueModifierRepository;

    public WaitingQueue modifyToken(WaitingQueue token)
    {
        return waitingQueueModifierRepository.modifyToken(token);
    }

    public void deleteAllTokens(List<WaitingQueue> tokenList)
    {
        waitingQueueModifierRepository.deleteAllTokens(tokenList);
    }

    public List<String> deleteWaitTokens(long count)
    {
        return waitingQueueModifierRepository.deleteWaitTokens(count);
    }

    public void deleteAllExpiredTokens(double nanoSeconds)
    {
        waitingQueueModifierRepository.deleteAllExpiredTokens(nanoSeconds);
    }

    public void changeExpiredTime(String uuid, double expiredAt)
    {
        waitingQueueModifierRepository.changeExpiredTime(uuid, expiredAt);
    }
}
