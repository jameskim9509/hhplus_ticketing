package kr.hhplus.be.server.domain.payment.components;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.repositories.PaymentWriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentWriter {
    private final PaymentWriterRepository paymentWriterRepository;

    public Payment createPayment(Payment payment)
    {
        return paymentWriterRepository.createPayment(payment);
    }
}
