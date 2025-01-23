package kr.hhplus.be.server.api.token.dto;

public record GetTokenResponse(Long waitingNumber) {
    public static GetTokenResponse from(Long waitingNumber)
    {
        return new GetTokenResponse(waitingNumber);
    }
}
