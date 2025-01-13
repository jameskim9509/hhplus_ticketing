package kr.hhplus.be.server.api.token.dto;

import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;

public record GetTokenRequest(String uuid) {
    public GetTokenRequest(String uuid)
    {
        validate(uuid);
        this.uuid = uuid;
    }

    private void validate(String uuid)
    {
        if(uuid == null)
            throw new ConcertException(ErrorCode.PARAMETER_NOT_VALID);
    }
}
