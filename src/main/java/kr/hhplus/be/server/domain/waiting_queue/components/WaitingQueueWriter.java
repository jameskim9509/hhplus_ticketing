package kr.hhplus.be.server.domain.waiting_queue.components;

import kr.hhplus.be.server.domain.waiting_queue.WaitingQueue;
import kr.hhplus.be.server.domain.waiting_queue.repositories.WaitingQueueWriterRepository;
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
}
