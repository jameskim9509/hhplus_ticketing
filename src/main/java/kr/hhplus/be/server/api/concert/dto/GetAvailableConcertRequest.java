package kr.hhplus.be.server.api.concert.dto;

import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;

import java.time.LocalDate;

public record GetAvailableConcertRequest(LocalDate startDate, LocalDate endDate) {
    public GetAvailableConcertRequest(LocalDate startDate, LocalDate endDate)
    {
        validate(startDate, endDate);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    private void validate(LocalDate startDate, LocalDate endDate)
    {
        if (startDate == null || endDate == null)
            throw new ConcertException(ErrorCode.PARAMETER_NOT_VALID);
        if (startDate.isBefore(LocalDate.now()) || endDate.isBefore(startDate))
            throw new ConcertException(ErrorCode.DATE_IS_INVALID);
    }
}
