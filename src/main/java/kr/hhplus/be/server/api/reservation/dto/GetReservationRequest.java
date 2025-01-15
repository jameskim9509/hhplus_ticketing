package kr.hhplus.be.server.api.reservation.dto;

import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;

public record GetReservationRequest(Long reservationId) {
    public GetReservationRequest(Long reservationId)
    {
        validate(reservationId);
        this.reservationId = reservationId;
    }

    private void validate(Long reservationId)
    {
        if(reservationId == null)
            throw new ConcertException(ErrorCode.PARAMETER_NOT_VALID);
        if(reservationId < 1)
            throw new ConcertException(ErrorCode.PARAMETER_NOT_VALID);
    }
}
