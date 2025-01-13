package kr.hhplus.be.server.api.token.dto;

import kr.hhplus.be.server.common.ConcertException;
import kr.hhplus.be.server.common.ErrorCode;
import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;

public record CreateTokenResponse(String uuid, WaitingQueueStatus status) {
    public static CreateTokenResponse from(WaitingQueue token)
    {
        return new CreateTokenResponse(token.getUser().getUuid(), token.getStatus());
    }
}
