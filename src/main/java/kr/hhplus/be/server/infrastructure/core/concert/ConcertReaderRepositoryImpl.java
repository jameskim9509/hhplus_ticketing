package kr.hhplus.be.server.infrastructure.core.concert;

import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.concert.repositories.ConcertReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConcertReaderRepositoryImpl implements ConcertReaderRepository {
    private final ConcertJpaRepository concertJpaRepository;

    @Override
    public Optional<Concert> getByDate(LocalDate date) {
        return concertJpaRepository.findByDate(date);
    }

    @Override
    public Optional<Concert> getById(Long id) {
        return concertJpaRepository.findById(id);
    }

    @Override
    public List<Concert> getByDateBetween(LocalDate startDate, LocalDate endDate) {
        return concertJpaRepository.findAllByDateBetween(startDate, endDate);
    }
}
