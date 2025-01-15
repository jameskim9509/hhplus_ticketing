package kr.hhplus.be.server.common.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.components.UserReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserValidateInterceptor implements HandlerInterceptor {
    private final UserReader userReader;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uuid = request.getHeader("X-Custom-Header");
        if (uuid == null) throw new ConcertException(ErrorCode.PARAMETER_NOT_VALID);
        User user = userReader.readByUuid(uuid);
        UserContext.setContext(user);

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        log.info("{} : {}", request.getRemoteAddr(),handlerMethod.getMethod());

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
