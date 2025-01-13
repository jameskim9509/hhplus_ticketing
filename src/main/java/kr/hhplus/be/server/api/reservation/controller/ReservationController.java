package kr.hhplus.be.server.api.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.api.reservation.application.ReservationUsecase;
import kr.hhplus.be.server.api.reservation.dto.ReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationUsecase reservationUsecase;

    @Operation(description = "예약 정보를 조회합니다.")
    @GetMapping("/reservation/{reservationId}")
    public ReservationResponse getReservation(
            Long reservationId,
            @RequestParam("uuid") String uuid
    ) {
        return ReservationResponse.from(
                reservationUsecase.getReservation(reservationId, uuid)
        );
    }

    @Operation(description = "좌석을 예약합니다.")
    @PostMapping("/reservation/seat")
    public ReservationResponse reserveSeat(
            @RequestParam("date") LocalDate date,
            @RequestParam("number") Long seatNumber,
            @RequestParam("uuid") String uuid
    )
    {
        return ReservationResponse.from(
                reservationUsecase.reserveSeat(date, seatNumber, uuid)
        );
    }
}
