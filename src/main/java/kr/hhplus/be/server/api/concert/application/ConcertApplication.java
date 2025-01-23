package kr.hhplus.be.server.api.concert.application;

import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.components.ConcertReader;
import kr.hhplus.be.server.domain.token.components.WaitingQueueReader;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.components.UserReader;
import kr.hhplus.be.server.api.concert.dto.GetAvailableConcertsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertApplication implements ConcertUsecase{
    private final UserReader userReader;
    private final WaitingQueueReader waitingQueueReader;
    private final ConcertReader concertReader;

    @Transactional
    @Override
    public List<Concert> getAvailableConcerts(LocalDate startDate, LocalDate endDate)
    {
        return concertReader.readByDateBetween(startDate, endDate);
    }
}
