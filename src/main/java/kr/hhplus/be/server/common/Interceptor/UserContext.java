package kr.hhplus.be.server.common.Interceptor;

import kr.hhplus.be.server.domain.user.User;

public class UserContext {
    public static final ThreadLocal<User> userContext = new ThreadLocal<>();

    public static User getContext()
    {
        return userContext.get();
    }

    public static void setContext(User user)
    {
        userContext.set(user);
    }
}
