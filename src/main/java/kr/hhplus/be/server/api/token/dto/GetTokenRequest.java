package kr.hhplus.be.server.api.token.dto;

public record GetTokenRequest(String uuid) {
    public GetTokenRequest(String uuid)
    {
        validate(uuid);
        this.uuid = uuid;
    }

    private void validate(String uuid)
    {
        if(uuid == null) throw new RuntimeException("");
    }
}
