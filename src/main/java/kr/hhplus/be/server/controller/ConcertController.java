package kr.hhplus.be.server.controller;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/concert")
public class ConcertController {
    @GetMapping("/ticket/token/{userId}")
    public Object getToken(Long userId)
    {
        return "{\"UUID\":\"4d0d669c-0c50-4bf1-b063-447a7cb94a59\", \"waitingNumber\":1 }";
    }

    @GetMapping
    public Object getAvailableConcert(
            @RequestParam("startDate")LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate
    )
    {
        return "{ \"concertList\" : [ { \"id\":1, \"name\":\"cats\", \"date\":\"2025-01-02\" }, { \"id\":5, \"name\":\"phantom of opera\", \"date\":\"2025-01-03\" } ] }";
    }

    @GetMapping("/seat")
    public Object getAvailableSeats(
            @RequestParam("date") LocalDate date
    )
    {
        return "{ \"seatList\" : [{ \"id\":3, \"concertId\":1, \"number\":10, \"cost\":100000 }, { \"id\":5, \"concertId\":1, \"number\":11, \"cost\":100000 }] }";
    }

    @PostMapping("/reservation/seat")
    public Object reserveSeat(
            @RequestParam("date") LocalDate date,
            @RequestParam("id") Long id
    )
    {
        return "{ \"id\":2, \"userId\":1, \"seatId\":5, \"status\":\"PAYMENT REQUIRED\", \"expirationTime\":\"2025-01-01 12:15\" }";
    }

    @GetMapping("/balance/{userId}")
    public Object getBalance(Long userId)
    {
        return "{ \"id\":1, \"balance\":1000000 }";
    }

    @PatchMapping("/balance/{userId}")
    public Object chargePoint(
            Long userId,
            @RequestParam("point") Long point
    )
    {
        return "{ \"id\":1, \"balance\":1100000 }";
    }

    @PatchMapping("/payment")
    public Object pay(
            @RequestParam("reservationId") Long reservationId
    )
    {
        return "{ \"id\":2, \"userId\":1, \"seatId\":5, \"concertId\":1, \"status\":\"RESERVED\" }";
    }
}
