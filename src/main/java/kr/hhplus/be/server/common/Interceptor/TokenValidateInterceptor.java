package kr.hhplus.be.server.common.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.api.token.application.TokenApplication;
import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.token.WaitingQueue;
import kr.hhplus.be.server.domain.token.components.WaitingQueueReader;
import kr.hhplus.be.server.domain.token.type.WaitingQueueStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.components.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class TokenValidateInterceptor implements HandlerInterceptor {
    private final UserReader userReader;
    private final TokenApplication tokenApplication;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uuid = request.getHeader("X-Custom-Header");
        if (uuid == null)
            throw new ConcertException(ErrorCode.PARAMETER_NOT_VALID);

        User user = userReader.readByUuid(uuid);
        UserContext.setContext(user);

        tokenApplication.validateToken(user);

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
