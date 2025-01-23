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
        userReader.readByIdWithPessimisticLock(user.getId());
        user.chargePoint(point);
        // 비관적 락의 경우, update 쿼리시에 version 검증이 나가는 것을 방지
        userModifier.modifyUserWithoutVersion(user);
        return user;
    }

    @Transactional
    @Override
    public User getBalance()
    {
        return UserContext.getContext();
    }
}
