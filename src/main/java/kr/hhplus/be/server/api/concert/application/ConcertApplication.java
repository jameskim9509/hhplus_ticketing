package kr.hhplus.be.server.api.concert.application;

import kr.hhplus.be.server.api.concert.dto.AvailableConcertDto;
import kr.hhplus.be.server.api.concert.dto.AvailableConcertDtoList;
import kr.hhplus.be.server.common.aop.DistributedLock;
import kr.hhplus.be.server.domain.concert.components.ConcertReader;
import kr.hhplus.be.server.domain.token.components.WaitingQueueReader;
import kr.hhplus.be.server.domain.user.components.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ConcertApplication implements ConcertUsecase{
    private final UserReader userReader;
    private final WaitingQueueReader waitingQueueReader;
    private final ConcertReader concertReader;

    @DistributedLock(key ="T(String).format('%s-%s', #startDate, #endDate)")
    @Cacheable(value="available-concerts", key="T(String).format('%s-%s', #startDate, #endDate)")
    @Transactional
    @Override
    // List 반환시에 클래스로 Wrapping 해주어야 함
    public AvailableConcertDtoList getAvailableConcerts(LocalDate startDate, LocalDate endDate)
    {
        return AvailableConcertDtoList.from(
                concertReader.readByDateBetween(startDate, endDate).stream()
                        .map(AvailableConcertDto::from)
                        .toList()
        );
    }

    @CacheEvict(value = "available-concerts", allEntries = true)
    public void clearAvailableConcerts()
    {

    }
}
