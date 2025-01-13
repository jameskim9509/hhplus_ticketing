package kr.hhplus.be.server.api.reservation.dto;

import kr.hhplus.be.server.domain.reservation.Reservation;
import kr.hhplus.be.server.domain.reservation.type.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse (
        Long id,
        String concertName,
        Long seatNumber,
        Long seatCost,
        ReservationStatus status,
        LocalDateTime expiredTime
){
    public static ReservationResponse from(Reservation reservation)
    {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getConcertName(),
                reservation.getSeatNumber(),
                reservation.getSeatCost(),
                reservation.getStatus(),
                reservation.getExpiredAt()
        );
    }
}
