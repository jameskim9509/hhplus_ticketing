package kr.hhplus.be.server.dto;

import kr.hhplus.be.server.domain.reservation.type.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse (Long id, String concertName, Long seatNumber, Long seatCost, ReservationStatus status, LocalDateTime expiredTime){
}
