package kr.hhplus.be.server.api.seat.dto;

import kr.hhplus.be.server.domain.seat.Seat;

import java.util.List;

public record GetAvailableSeatsResponse(List<SeatResponse> availableSeats) {
    public static GetAvailableSeatsResponse from(List<Seat> availableSeats)
    {
        return new GetAvailableSeatsResponse(
                availableSeats.stream().map(SeatResponse::of).toList()
        );
    }
}
