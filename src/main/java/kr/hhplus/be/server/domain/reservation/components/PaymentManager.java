package kr.hhplus.be.server.domain.reservation.components;

import kr.hhplus.be.server.domain.reservation.Payment;
import kr.hhplus.be.server.domain.reservation.repositories.PaymentManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentManager {
    private final PaymentManagerRepository paymentManagerRepository;

    public Payment createPayment(Payment payment)
    {
        return paymentManagerRepository.createPayment(payment);
    }
}
