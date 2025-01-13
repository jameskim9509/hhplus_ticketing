package kr.hhplus.be.server.api.seat.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.api.seat.application.SeatUsecase;
import kr.hhplus.be.server.api.seat.dto.GetAvailableSeatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class SeatController {
    private final SeatUsecase seatUsecase;

    @Operation(description = "해당 날짜에 이용가능한 좌석을 조회합니다.")
    @GetMapping("/seats")
    public GetAvailableSeatsResponse getAvailableSeats(
            @RequestParam("date") LocalDate date,
            @RequestParam("uuid") String uuid
    )
    {
        return GetAvailableSeatsResponse.from(
                seatUsecase.getAvailableSeatsByDate(date, uuid)
        );
    }
}
