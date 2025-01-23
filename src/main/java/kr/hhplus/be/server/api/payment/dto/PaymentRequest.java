package kr.hhplus.be.server.api.payment.dto;

import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;

public record PaymentRequest(Long reservationId) {
    public PaymentRequest(Long reservationId)
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
