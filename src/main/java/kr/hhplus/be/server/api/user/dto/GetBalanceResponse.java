package kr.hhplus.be.server.api.user.dto;

import kr.hhplus.be.server.domain.user.User;

public record GetBalanceResponse(Long userId, Long balance) {
    public static GetBalanceResponse from(User user)
    {
        return new GetBalanceResponse(user.getId(), user.getBalance());
    }
}
