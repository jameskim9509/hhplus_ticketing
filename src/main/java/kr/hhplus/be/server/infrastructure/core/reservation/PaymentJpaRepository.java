package kr.hhplus.be.server.infrastructure.core.reservation;

import kr.hhplus.be.server.domain.reservation.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
