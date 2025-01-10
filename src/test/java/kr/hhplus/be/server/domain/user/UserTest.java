package kr.hhplus.be.server.domain.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void 충전_가능_금액_초과_에러()
    {
        // given
        User user = User.builder()
                .balance(1_000_000L)
                .build();

        // when, then
        Assertions.assertThatThrownBy(
                () -> user.chargePoint(1000L)
        ).isInstanceOf(RuntimeException.class)
        .hasMessage("충전 가능 금액을 초과하였습니다.");
    }

    @Test
    void 잔액_부족_에러()
    {
        // given
        User user = User.builder()
                .balance(0L)
                .build();

        // when, then
        Assertions.assertThatThrownBy(
                        () -> user.usePoint(1000L)
                ).isInstanceOf(RuntimeException.class)
                .hasMessage("잔액이 부족합니다.");
    }
}