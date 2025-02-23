package kr.hhplus.be.server.api.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.api.payment.application.PaymentUsecase;
import kr.hhplus.be.server.api.payment.dto.PaymentRequest;
import kr.hhplus.be.server.api.reservation.dto.ReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/concerts")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentUsecase paymentUsecase;

    @Operation(description = "예약된 좌석을 결제합니다.")
    @PatchMapping("/payment")
    public ReservationResponse pay(
            @ModelAttribute PaymentRequest paymentRequest
            )
    {
        return ReservationResponse.from(
                paymentUsecase.pay(
                        paymentRequest.reservationId()
                ).getReservation()
        );
    }
}
