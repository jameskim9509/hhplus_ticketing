package kr.hhplus.be.server.api.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.api.reservation.application.ReservationUsecase;
import kr.hhplus.be.server.api.reservation.dto.GetReservationRequest;
import kr.hhplus.be.server.api.reservation.dto.ReservationResponse;
import kr.hhplus.be.server.api.reservation.dto.ReserveSeatRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("concerts")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationUsecase reservationUsecase;

    @Operation(description = "예약 정보를 조회합니다.")
    @GetMapping("/reservation/{reservationId}")
    public ReservationResponse getReservation(
            @ModelAttribute GetReservationRequest getReservationRequest
            ) {
        return ReservationResponse.from(
                reservationUsecase.getReservation(
                        getReservationRequest.reservationId()
                )
        );
    }

    @Operation(description = "좌석을 예약합니다.")
    @PostMapping("/reservation/seat")
    public ReservationResponse reserveSeat(
            @ModelAttribute ReserveSeatRequest reserveSeatRequest
            )
    {
        return ReservationResponse.from(
                reservationUsecase.reserveSeat(
                        reserveSeatRequest.date(),
                        reserveSeatRequest.seatNumber()
                )
        );
    }
}
