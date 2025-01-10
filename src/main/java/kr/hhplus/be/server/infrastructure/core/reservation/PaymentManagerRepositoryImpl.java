package kr.hhplus.be.server.infrastructure.core.reservation;

import kr.hhplus.be.server.domain.reservation.Payment;
import kr.hhplus.be.server.domain.reservation.repositories.PaymentManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentManagerRepositoryImpl implements PaymentManagerRepository {
    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment createPayment(Payment payment) {
        return paymentJpaRepository.save(payment);
    }
}
