package kr.hhplus.be.server.api.seat.application;

import kr.hhplus.be.server.domain.seat.Seat;

import java.time.LocalDate;
import java.util.List;

public interface SeatUsecase {
    public List<Seat> getAvailableSeatsByDate(LocalDate date);
}
