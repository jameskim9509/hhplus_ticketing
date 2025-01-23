package kr.hhplus.be.server.api.seat.dto;

import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.Getter;

import java.time.LocalDate;

public record GetAvailableSeatRequest(LocalDate date) {
    public GetAvailableSeatRequest(LocalDate date)
    {
        validate(date);
        this.date = date;
    }

    private void validate(LocalDate date)
    {
        if(date == null)
            throw new ConcertException(ErrorCode.PARAMETER_NOT_VALID);
        if(date.isBefore(LocalDate.now()))
            throw new ConcertException(ErrorCode.DATE_IS_INVALID);
    }
}
