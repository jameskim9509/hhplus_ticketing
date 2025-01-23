package kr.hhplus.be.server.api.user.dto;

import kr.hhplus.be.server.domain.user.User;

public record ChargePointResponse(Long userId, Long balance) {
    public static ChargePointResponse from(User user)
    {
        return new ChargePointResponse(user.getId(), user.getBalance());
    }
}
