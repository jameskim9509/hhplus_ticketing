package kr.hhplus.be.server.domain.seat.repositories;

import kr.hhplus.be.server.domain.seat.Seat;

public interface SeatModifierRepository {
    public Seat modifySeat(Seat seat);
}
