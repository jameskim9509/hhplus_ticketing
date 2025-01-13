package kr.hhplus.be.server.api.seat.dto;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.type.SeatStatus;

public record SeatResponse(
        Long seatId, Long seatNumber, Long seatCost, SeatStatus status
) {
    public static SeatResponse of(Seat seat)
    {
        return new SeatResponse(
                seat.getId(),
                seat.getConcert().getId(),
                seat.getNumber(),
                seat.getStatus()
        );
    }
}
