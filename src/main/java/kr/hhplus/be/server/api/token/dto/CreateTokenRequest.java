package kr.hhplus.be.server.api.token.dto;

public record CreateTokenRequest(Long userId) {
    public CreateTokenRequest(Long userId)
    {
        validate(userId);
        this.userId = userId;
    }

    private void validate(Long userId)
    {
        if (userId == null) throw new RuntimeException("");
        if (userId < 0) throw new RuntimeException("");
    }
}
