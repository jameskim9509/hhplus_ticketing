package kr.hhplus.be.server.api.user.application;

import kr.hhplus.be.server.domain.user.User;

public interface UserUsecase {
    public User chargePoint(Long point, String uuid);
    public User getBalance(String uuid);
}
