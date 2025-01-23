package kr.hhplus.be.server.infrastructure.core.seat;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.seat.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {
    List<Seat> findAllByConcertId(Long concertId);

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Lock(LockModeType.OPTIMISTIC)
    @Query("select s from Seat s where s.concert.id=:concertId and s.number=:seatNumber")
    Optional<Seat> findByConcertIdAndNumberWithLock(@Param("concertId") Long concertId, @Param("seatNumber") Long seatNumber);
}
