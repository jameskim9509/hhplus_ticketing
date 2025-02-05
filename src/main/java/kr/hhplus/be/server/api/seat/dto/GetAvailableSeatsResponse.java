package kr.hhplus.be.server.api.seat.dto;

import kr.hhplus.be.server.domain.seat.Seat;

import java.util.List;

public record GetAvailableSeatsResponse(List<SeatResponse> availableSeatNumber) {
    public static GetAvailableSeatsResponse from(List<Long> availableSeatNumber)
    {
        return new GetAvailableSeatsResponse(
                availableSeatNumber.stream().map(SeatResponse::of).toList()
        );
    }
}
