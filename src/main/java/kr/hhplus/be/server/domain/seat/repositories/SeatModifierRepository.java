package kr.hhplus.be.server.domain.seat.repositories;

import kr.hhplus.be.server.domain.seat.Seat;

import java.time.LocalDate;

public interface SeatModifierRepository {
    public Seat modifySeat(Seat seat);
    public void setAvailable(Seat seat);
    public void setReserved(Seat seat);
}
