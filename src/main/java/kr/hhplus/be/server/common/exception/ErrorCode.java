package kr.hhplus.be.server.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    PARAMETER_NOT_VALID(HttpStatus.BAD_REQUEST, "잘못된 인자입니다."),
    DATE_IS_INVALID(HttpStatus.BAD_REQUEST, "입력 가능한 날짜의 범위가 아닙니다."),
    POINT_IS_INVALID(HttpStatus.BAD_REQUEST, "입력 가능한 포인트의 범위가 아닙니다."),
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 토큰입니다."),
    ACTIVE_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "활성화된 토큰이 없습니다."),
    TOKEN_IS_INVALID(HttpStatus.FORBIDDEN, "활성화되지 않은 토큰입니다."),
    TOKEN_ALREADY_EXISTS(HttpStatus.FORBIDDEN, "토큰이 이미 존재합니다."),
    CONCERT_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 콘서트입니다."),
    AVAILABLE_SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "이용가능한 좌석이 없습니다."),
    SEAT_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 좌석입니다."),
    SEAT_IS_INVALID(HttpStatus.FORBIDDEN, "사용 가능한 좌석이 아닙니다."),
    RESERVATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 예약입니다."),
    RESERVATION_EXPIRED(HttpStatus.FORBIDDEN, "만료된 예약입니다."),
    RESERVATION_NOT_PAYMENT_REQUIRED(HttpStatus.FORBIDDEN, "결제가 필요한 예약이 아닙니다."),
    RESERVATION_NOT_MATCHED(HttpStatus.FORBIDDEN, "요청자의 예약 정보가 아닙니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 사용자입니다."),
    CHARGE_POINT_MAX(HttpStatus.FORBIDDEN, "충전 가능 금액을 초과하였습니다."),
    NOT_ENOUGH_BALANCE(HttpStatus.FORBIDDEN, "잔액이 부족합니다.");

    private final HttpStatus status;
    private final String message;
}
