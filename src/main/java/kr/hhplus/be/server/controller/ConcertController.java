package kr.hhplus.be.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.ConcertUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "콘서트 티켓팅 API", description = "콘서트 표를 티켓팅하는 API 명세서를 제공합니다.")
@RestController
@RequestMapping("/concerts")
@RequiredArgsConstructor
public class ConcertController {
    private final ConcertUsecase concertUsecase;

    @Operation(description = "토큰을 조회합니다.")
    @GetMapping("/tickets/tokens/")
    public Object getToken(@RequestParam("uuid") String uuid)
    {
        return concertUsecase.getToken(uuid);
    }

    @Operation(description = "토큰을 생성합니다.")
    @PostMapping("/tickets/tokens/{userId}")
    public Object createToken(Long userId)
    {
        return concertUsecase.createToken(userId);
    }

    @Operation(description = "기간 내 존재하는 콘서트를 조회합니다.")
    @GetMapping
    public Object getAvailableConcert(
            @RequestParam("startDate")LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam("uuid") String uuid
    )
    {
        return concertUsecase.getAvailableConcerts(startDate, endDate, uuid);
    }

    @Operation(description = "해당 날짜에 이용가능한 좌석을 조회합니다.")
    @GetMapping("/seats")
    public Object getAvailableSeats(
            @RequestParam("date") LocalDate date,
            @RequestParam("uuid") String uuid
    )
    {
        return concertUsecase.getAvailableSeatsByDate(date, uuid);
    }

    @Operation(description = "좌석을 예약합니다.")
    @PostMapping("/reservation/seat")
    public Object reserveSeat(
            @RequestParam("date") LocalDate date,
            @RequestParam("number") Long seatNumber,
            @RequestParam("uuid") String uuid
    )
    {
        return concertUsecase.reserveSeat(date, seatNumber, uuid);
    }

    @Operation(description = "잔액을 조회합니다.")
    @GetMapping("/balance")
    public Object getBalance(
            @RequestParam("uuid") String uuid)
    {
        return concertUsecase.getBalance(uuid);
    }

    @Operation(description = "포인트를 충전합니다.")
    @PatchMapping("/balance/{userId}")
    public Object chargePoint(
            @RequestParam("uuid") String uuid,
            @RequestParam("point") Long point
    )
    {
        return concertUsecase.chargePoint(point, uuid);
    }

    @Operation(description = "예약 후 좌석을 결제합니다.")
    @PatchMapping("/payment")
    public Object pay(
            @RequestParam("reservationId") Long reservationId,
            @RequestParam("uuid") String uuid
    )
    {
        return concertUsecase.pay(reservationId, uuid);
    }

    @Operation(description = "예약 정보를 조회합니다.")
    @GetMapping("/reservation/{reservationId}")
    public Object getReservation(
            Long reservationId,
            @RequestParam("uuid") String uuid
    )
    {
        return concertUsecase.getReservation(reservationId, uuid);
    }
}
