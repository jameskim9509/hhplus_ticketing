package kr.hhplus.be.server.api.user.application;

import kr.hhplus.be.server.common.Interceptor.UserContext;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.components.UserModifier;
import kr.hhplus.be.server.domain.user.components.UserReader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UserApplicationUnitTest {
    @Mock
    private UserReader userReader;
    @Mock
    private UserModifier userModifier;

    @InjectMocks
    private UserApplication userApplication;

    @Test
    void chargePoint() {
        // given
        UserContext.setContext(
                User.builder()
                        .balance(0L)
                        .uuid(UUID.randomUUID().toString())
                        .build()
        );

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // when
        userApplication.chargePoint(10000L);

        Mockito.verify(userModifier).modifyUserWithoutVersion(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        //then
        Assertions.assertThat(capturedUser.getBalance()).isEqualTo(10000L);
    }
}