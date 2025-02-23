package kr.hhplus.be.server.api.user.dto;

import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;

public record ChargePointRequest(Long point) {
    public ChargePointRequest(Long point)
    {
        validate(point);
        this.point = point;
    }

    private void validate(Long point)
    {
        if (point == null)
            throw new ConcertException(ErrorCode.PARAMETER_NOT_VALID);
        if (point < 0)
            throw new ConcertException(ErrorCode.POINT_IS_INVALID);
    }
}
