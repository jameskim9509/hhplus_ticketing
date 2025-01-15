package kr.hhplus.be.server.common.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.components.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class UserValidateInterceptor implements HandlerInterceptor {
    private final UserReader userReader;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uuid = request.getHeader("X-Custom-Header");
        if (uuid == null) throw new ConcertException(ErrorCode.PARAMETER_NOT_VALID);
        User user = userReader.readByUuid(uuid);
        UserContext.setContext(user);

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
