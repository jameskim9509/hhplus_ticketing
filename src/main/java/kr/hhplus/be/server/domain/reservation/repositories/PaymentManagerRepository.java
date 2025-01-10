package kr.hhplus.be.server.domain.reservation.repositories;

import kr.hhplus.be.server.domain.reservation.Payment;

public interface PaymentManagerRepository {
    public Payment createPayment(Payment payment);
}
