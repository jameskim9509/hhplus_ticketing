package kr.hhplus.be.server.api.reservation.dto;

import kr.hhplus.be.server.domain.reservation.Reservation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public record ReservationSuccessEvent(Long userId, String reservationInfo) {
}
