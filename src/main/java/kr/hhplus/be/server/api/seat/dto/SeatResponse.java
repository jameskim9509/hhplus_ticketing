package kr.hhplus.be.server.api.seat.dto;

import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.seat.type.SeatStatus;

public record SeatResponse(Long seatNumber) {
    public static SeatResponse of(Long seatNumber)
    {
        return new SeatResponse(seatNumber);
    }
}
