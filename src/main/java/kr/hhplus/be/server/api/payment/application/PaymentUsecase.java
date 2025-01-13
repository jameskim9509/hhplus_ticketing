package kr.hhplus.be.server.api.payment.application;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.reservation.Reservation;

public interface PaymentUsecase {
    public Payment pay(Long reservationId, String uuid);
}
