package kr.hhplus.be.server.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.api.user.application.UserUsecase;
import kr.hhplus.be.server.api.user.dto.ChargePointRequest;
import kr.hhplus.be.server.api.user.dto.ChargePointResponse;
import kr.hhplus.be.server.api.user.dto.GetBalanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/concerts")
public class UserController {
    private final UserUsecase userUsecase;

    @Operation(description = "포인트를 충전합니다.")
    @PatchMapping("/balance/{userId}")
    public ChargePointResponse chargePoint(
            @ModelAttribute ChargePointRequest chargePointRequest
            )
    {
        return ChargePointResponse.from(
                userUsecase.chargePoint(
                        chargePointRequest.point()
                )
        );
    }

    @Operation(description = "잔액을 조회합니다.")
    @GetMapping("/balance")
    public Object getBalance()
    {
        return GetBalanceResponse.from(
                userUsecase.getBalance()
        );
    }
}
