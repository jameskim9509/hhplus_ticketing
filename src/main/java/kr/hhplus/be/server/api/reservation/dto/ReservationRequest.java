package kr.hhplus.be.server.api.reservation.dto;

import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;

public record ReservationRequest(Long reservationId, String uuid) {
    public ReservationRequest(Long reservationId, String uuid)
    {
        validate(reservationId, uuid);
        this.reservationId = reservationId;
        this.uuid = uuid;
    }

    private void validate(Long reservationId, String uuid)
    {
        if(reservationId == null || uuid == null)
            throw new ConcertException(ErrorCode.PARAMETER_NOT_VALID);
        if(reservationId < 1)
            throw new ConcertException(ErrorCode.PARAMETER_NOT_VALID);
    }
}
