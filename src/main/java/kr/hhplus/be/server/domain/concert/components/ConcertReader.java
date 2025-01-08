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
        return concertReaderRepository.getByDate(date).get();
    }

    public Concert getById(Long id)
    {
        return concertReaderRepository.getById(id).get();
    }

    public List<Concert> readByDateBetween(LocalDate startDate, LocalDate endDate){
        return  concertReaderRepository.getByDateBetween(startDate, endDate);
    }
}
