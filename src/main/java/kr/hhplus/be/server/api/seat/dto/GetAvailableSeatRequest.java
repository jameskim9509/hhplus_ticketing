package kr.hhplus.be.server.api.seat.dto;

import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.Getter;

import java.time.LocalDate;

public record GetAvailableSeatRequest(LocalDate date, String uuid) {
    public GetAvailableSeatRequest(LocalDate date, String uuid)
    {
        validate(date, uuid);
        this.date = date;
        this.uuid = uuid;
    }

    private void validate(LocalDate date, String uuid)
    {
        if(date == null || uuid == null)
            throw new ConcertException(ErrorCode.PARAMETER_NOT_VALID);
        if(date.isBefore(LocalDate.now()))
            throw new ConcertException(ErrorCode.DATE_IS_INVALID);
    }
}
