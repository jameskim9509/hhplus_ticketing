package kr.hhplus.be.server.api.user.dto;

import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;

public record ChargePointRequest(String uuid, Long point) {
    public ChargePointRequest(String uuid, Long point)
    {
        validate(uuid, point);
        this.uuid = uuid;
        this.point = point;
    }

    private void validate(String uuid, Long point)
    {
        if (uuid == null || point == null)
            throw new ConcertException(ErrorCode.PARAMETER_NOT_VALID);
        if (point < 0)
            throw new ConcertException(ErrorCode.POINT_IS_INVALID);
    }
}
