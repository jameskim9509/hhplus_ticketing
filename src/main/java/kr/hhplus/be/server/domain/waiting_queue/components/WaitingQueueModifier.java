package kr.hhplus.be.server.domain.waiting_queue.components;

import kr.hhplus.be.server.domain.waiting_queue.WaitingQueue;
import kr.hhplus.be.server.domain.waiting_queue.repositories.WaitingQueueModifierRepository;
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

    public WaitingQueue deleteAllTokens(List<WaitingQueue> tokenList)
    {
        return waitingQueueModifierRepository.deleteAllTokens(tokenList);
    }
}
