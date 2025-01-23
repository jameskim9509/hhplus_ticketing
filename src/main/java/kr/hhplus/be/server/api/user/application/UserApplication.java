package kr.hhplus.be.server.api.user.application;

import kr.hhplus.be.server.common.Interceptor.UserContext;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.components.UserModifier;
import kr.hhplus.be.server.domain.user.components.UserReader;
import kr.hhplus.be.server.api.user.dto.ChargePointResponse;
import kr.hhplus.be.server.api.user.dto.GetBalanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserApplication implements UserUsecase {
    private final UserReader userReader;
    private final UserModifier userModifier;

    @Transactional
    @Override
    public User chargePoint(Long point)
    {
        User user = UserContext.getContext();
        userReader.readByIdWithLock(user.getId());
        user.chargePoint(point);
        return userModifier.modifyUser(user);
    }

    @Transactional
    @Override
    public User getBalance()
    {
        return UserContext.getContext();
    }
}
