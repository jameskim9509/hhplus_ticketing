package kr.hhplus.be.server.api.token.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.api.token.application.TokenUsecase;
import kr.hhplus.be.server.api.token.dto.CreateTokenRequest;
import kr.hhplus.be.server.api.token.dto.CreateTokenResponse;
import kr.hhplus.be.server.api.token.dto.GetTokenRequest;
import kr.hhplus.be.server.api.token.dto.GetTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TokenController {
    private final TokenUsecase tokenUsecase;

    @Operation(description = "토큰을 조회합니다.")
    @GetMapping("/tickets/tokens/")
    public GetTokenResponse getToken(
            @ModelAttribute GetTokenRequest getTokenRequest
    )
    {
        return GetTokenResponse.from(
                tokenUsecase.getToken(getTokenRequest.uuid())
        );
    }

    @Operation(description = "토큰을 생성합니다.")
    @PostMapping("/tickets/tokens/{userId}")
    public CreateTokenResponse createToken(
            @ModelAttribute CreateTokenRequest createTokenRequest
    )
    {
        return CreateTokenResponse.from(
                tokenUsecase.createToken(createTokenRequest.userId())
        );
    }
}
