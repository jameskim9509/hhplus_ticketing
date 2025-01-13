package kr.hhplus.be.server.api.payment.dto;

public record PaymentRequest(Long reservationId, String uuid) {
    public PaymentRequest(Long reservationId, String uuid)
    {
        validate(reservationId, uuid);
        this.reservationId = reservationId;
        this.uuid = uuid;
    }

    private void validate(Long reservationId, String uuid)
    {
        if(uuid == null || reservationId == null) throw new RuntimeException();
        if(reservationId < 1) throw new RuntimeException();
    }
}
