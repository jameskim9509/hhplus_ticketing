package kr.hhplus.be.server.common.exception;

import lombok.Getter;

@Getter
public class ConcertException extends RuntimeException{
    private final String message;
    private final ErrorCode errorCode;

    public ConcertException(ErrorCode e)
    {
        super(e.getMessage());
        this.errorCode = e;
        this.message = e.getMessage();
    }
}
