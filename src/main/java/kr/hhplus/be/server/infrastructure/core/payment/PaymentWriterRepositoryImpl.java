package kr.hhplus.be.server.infrastructure.core.payment;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.repositories.PaymentWriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentWriterRepositoryImpl implements PaymentWriterRepository {
    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment createPayment(Payment payment) {
        return paymentJpaRepository.save(payment);
    }
}
