package kr.hhplus.be.server.common.exception;

public record ErrorResponse(ErrorCode code, String message) {
}
