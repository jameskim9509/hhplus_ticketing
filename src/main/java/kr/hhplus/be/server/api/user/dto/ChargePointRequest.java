package kr.hhplus.be.server.api.user.dto;

public record ChargePointRequest(String uuid, Long point) {
    public ChargePointRequest(String uuid, Long point)
    {
        validate(uuid, point);
        this.uuid = uuid;
        this.point = point;
    }

    private void validate(String uuid, Long point)
    {
        if (uuid == null || point == null) throw new RuntimeException();
        if (point < 0) throw new RuntimeException();
    }
}
