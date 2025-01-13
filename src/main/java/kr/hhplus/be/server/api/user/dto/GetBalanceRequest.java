package kr.hhplus.be.server.api.user.dto;

public record GetBalanceRequest(String uuid) {
    public GetBalanceRequest(String uuid)
    {
        validate(uuid);
        this.uuid = uuid;
    }

    private void validate(String uuid)
    {
        if(uuid == null) throw new RuntimeException();
    }
}
