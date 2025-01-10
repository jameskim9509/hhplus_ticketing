package kr.hhplus.be.server.domain.concert.repositories;

import kr.hhplus.be.server.domain.concert.Concert;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConcertReaderRepository {
    Optional<Concert> getByDate(LocalDate date);
    Optional<Concert> getById(Long id);
    List<Concert> getByDateBetween(LocalDate startDate, LocalDate endDate);
}
