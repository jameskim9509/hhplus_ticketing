package kr.hhplus.be.server.domain.token.components;

import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.repositories.WaitingQueueWriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaitingQueueWriter {
    private final WaitingQueueWriterRepository waitingQueueWriterRepository;

    public WaitingQueue writeToken(WaitingQueue waitingQueue)
    {
        return waitingQueueWriterRepository.writeToken(waitingQueue);
    }

    public boolean writeWaitingToken(String uuid, double createdAt)
    {
        return waitingQueueWriterRepository.writeWaitingToken(uuid, createdAt);
    }

    public boolean writeActiveToken(String uuid, double expiredAt)
    {
        return waitingQueueWriterRepository.writeActiveToken(uuid, expiredAt);
    }
}
