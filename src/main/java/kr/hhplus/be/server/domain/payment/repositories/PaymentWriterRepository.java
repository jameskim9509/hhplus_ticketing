package kr.hhplus.be.server.domain.payment.repositories;

import kr.hhplus.be.server.domain.payment.Payment;

public interface PaymentWriterRepository {
    public Payment createPayment(Payment payment);
}
