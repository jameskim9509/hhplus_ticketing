package kr.hhplus.be.server.controller;

import kr.hhplus.be.server.application.ConcertUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/concerts")
@RequiredArgsConstructor
public class ConcertController {
    private final ConcertUsecase concertUsecase;

    @GetMapping("/tickets/tokens/")
    public Object getToken(@RequestParam("uuid") String uuid)
    {
        return concertUsecase.getToken(uuid);
    }

    @PostMapping("/tickets/tokens/{userId}")
    public Object createToken(Long userId)
    {
        return concertUsecase.createToken(userId);
    }

    @GetMapping
    public Object getAvailableConcert(
            @RequestParam("startDate")LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam("uuid") String uuid
    )
    {
        return concertUsecase.getAvailableConcerts(startDate, endDate, uuid);
    }

    @GetMapping("/seats")
    public Object getAvailableSeats(
            @RequestParam("date") LocalDate date,
            @RequestParam("uuid") String uuid
    )
    {
        return concertUsecase.getAvailableSeatsByDate(date, uuid);
    }

    @PostMapping("/reservation/seat")
    public Object reserveSeat(
            @RequestParam("date") LocalDate date,
            @RequestParam("number") Long seatNumber,
            @RequestParam("uuid") String uuid
    )
    {
        return concertUsecase.reserveSeat(date, seatNumber, uuid);
    }

    @GetMapping("/balance")
    public Object getBalance(
            @RequestParam("uuid") String uuid)
    {
        return concertUsecase.getBalance(uuid);
    }

    @PatchMapping("/balance/{userId}")
    public Object chargePoint(
            @RequestParam("uuid") String uuid,
            @RequestParam("point") Long point
    )
    {
        return concertUsecase.chargePoint(point, uuid);
    }

    @PatchMapping("/payment")
    public Object pay(
            @RequestParam("reservationId") Long reservationId,
            @RequestParam("uuid") String uuid
    )
    {
        return concertUsecase.pay(reservationId, uuid);
    }

    @GetMapping("/reservation/{reservationId}")
    public Object getReservation(
            Long reservationId,
            @RequestParam("uuid") String uuid
    )
    {
        return concertUsecase.getReservation(reservationId, uuid);
    }
}
