package kr.hhplus.be.server.dto;

public class UuidContext {
    public static final ThreadLocal<String> uuidContext = new ThreadLocal<>();

    public static String getContext()
    {
        return uuidContext.get();
    }

    public static void setContext(String context)
    {
        uuidContext.set(context);
    }
}
