package kr.hhplus.be.server.common.config;

import kr.hhplus.be.server.common.Interceptor.TokenValidateInterceptor;
import kr.hhplus.be.server.common.Interceptor.UserValidateInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final TokenValidateInterceptor tokenValidateInterceptor;
    private final UserValidateInterceptor userValidateInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenValidateInterceptor)
                .excludePathPatterns("/concerts/balance/**")
                .excludePathPatterns("/concerts/reservation/*");

        registry.addInterceptor(userValidateInterceptor)
                .addPathPatterns("/concerts/balance/**")
                .addPathPatterns("/concerts/reservation/*"); // 예약 조회
    }
}
