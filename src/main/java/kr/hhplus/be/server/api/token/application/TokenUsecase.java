package kr.hhplus.be.server.api.token.application;

import kr.hhplus.be.server.api.token.dto.CreateTokenResponse;
import kr.hhplus.be.server.api.token.dto.GetTokenResponse;
import kr.hhplus.be.server.domain.token.WaitingQueue;

public interface TokenUsecase {
    public String createToken(Long userId);
    public Long getToken();
}
