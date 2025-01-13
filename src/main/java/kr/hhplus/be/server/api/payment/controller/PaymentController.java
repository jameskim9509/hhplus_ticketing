package kr.hhplus.be.server.api.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.api.payment.application.PaymentUsecase;
import kr.hhplus.be.server.api.reservation.dto.ReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PaymentController {
    PaymentUsecase paymentUsecase;

    @Operation(description = "예약된 좌석을 결제합니다.")
    @PatchMapping("/payment")
    public ReservationResponse pay(
            @RequestParam("reservationId") Long reservationId,
            @RequestParam("uuid") String uuid
    )
    {
        return ReservationResponse.from(
                paymentUsecase.pay(reservationId, uuid).getReservation()
        );
    }
}
