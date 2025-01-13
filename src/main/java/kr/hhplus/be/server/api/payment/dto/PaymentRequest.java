package kr.hhplus.be.server.api.payment.dto;

import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;

public record PaymentRequest(Long reservationId, String uuid) {
    public PaymentRequest(Long reservationId, String uuid)
    {
        validate(reservationId, uuid);
        this.reservationId = reservationId;
        this.uuid = uuid;
    }

    private void validate(Long reservationId, String uuid)
    {
        if(uuid == null || reservationId == null)
            throw new ConcertException(ErrorCode.PARAMETER_NOT_VALID);
        if(reservationId < 1)
            throw new ConcertException(ErrorCode.PARAMETER_NOT_VALID);
    }
}
