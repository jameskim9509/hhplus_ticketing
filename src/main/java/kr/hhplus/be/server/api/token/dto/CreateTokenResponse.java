package kr.hhplus.be.server.api.token.dto;

import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;

public record CreateTokenResponse(String uuid, WaitingQueueStatus status) {
    public static CreateTokenResponse from(String uuid)
    {
        return new CreateTokenResponse(uuid, WaitingQueueStatus.WAIT);
    }
}
