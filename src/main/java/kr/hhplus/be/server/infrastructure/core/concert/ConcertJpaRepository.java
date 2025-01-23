package kr.hhplus.be.server.infrastructure.core.concert;

import kr.hhplus.be.server.domain.concert.Concert;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
    public Optional<Concert> findByDate(LocalDate date);

    public List<Concert> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
}
//    public Page<Concert> findAllByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
