package kr.hhplus.be.server.api.token.dto;

import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;

public record CreateTokenRequest(Long userId) {
    public CreateTokenRequest(Long userId)
    {
        validate(userId);
        this.userId = userId;
    }

    private void validate(Long userId)
    {
        if (userId == null)
            throw new ConcertException(ErrorCode.PARAMETER_NOT_VALID);
        if (userId < 1)
            throw new ConcertException(ErrorCode.PARAMETER_NOT_VALID);
    }
}
