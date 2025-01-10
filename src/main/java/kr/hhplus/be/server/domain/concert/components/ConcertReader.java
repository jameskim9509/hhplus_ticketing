package kr.hhplus.be.server.domain.concert.components;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.repositories.ConcertReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertReader {
    private final ConcertReaderRepository concertReaderRepository;

    public Concert getByDate(LocalDate date)
    {
        return concertReaderRepository.getByDate(date)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 콘서트입니다."));
    }

    public Concert getById(Long id)
    {
        return concertReaderRepository.getById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 콘서트입니다."));
    }

    public List<Concert> readByDateBetween(LocalDate startDate, LocalDate endDate){
        return  concertReaderRepository.getByDateBetween(startDate, endDate);
    }
}
