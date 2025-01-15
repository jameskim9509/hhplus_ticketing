package kr.hhplus.be.server.api.reservation.dto;

import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;

import java.time.LocalDate;

public record ReserveSeatRequest(LocalDate date, Long seatNumber) {
    public ReserveSeatRequest(LocalDate date, Long seatNumber)
    {
        validate(date, seatNumber);
        this.date = date;
        this.seatNumber = seatNumber;
    }

    private void validate(LocalDate date, Long seatNumber)
    {
        if(date == null || seatNumber == null)
            throw new ConcertException(ErrorCode.PARAMETER_NOT_VALID);
        if(seatNumber < 1)
            throw new ConcertException(ErrorCode.PARAMETER_NOT_VALID);
        if(date.isBefore(LocalDate.now()))
            throw new ConcertException(ErrorCode.DATE_IS_INVALID);
    }
}
