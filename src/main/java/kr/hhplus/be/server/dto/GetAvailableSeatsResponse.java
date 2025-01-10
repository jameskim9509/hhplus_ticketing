package kr.hhplus.be.server.dto;

import kr.hhplus.be.server.domain.seat.Seat;

import java.util.List;

public record GetAvailableSeatsResponse(List<Seat> availableSeats) {
}
