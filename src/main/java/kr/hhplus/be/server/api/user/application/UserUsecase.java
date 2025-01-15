package kr.hhplus.be.server.api.user.application;

import kr.hhplus.be.server.domain.user.User;

public interface UserUsecase {
    public User chargePoint(Long point);
    public User getBalance();
}
