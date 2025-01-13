package kr.hhplus.be.server.api.reservation.dto;

public record ReservationRequest(Long reservationId, String uuid) {
    public ReservationRequest(Long reservationId, String uuid)
    {
        validate(reservationId, uuid);
        this.reservationId = reservationId;
        this.uuid = uuid;
    }

    private void validate(Long reservationId, String uuid)
    {
        if(reservationId == null || uuid == null) throw new RuntimeException();
        if(reservationId < 1) throw new RuntimeException();
    }
}
