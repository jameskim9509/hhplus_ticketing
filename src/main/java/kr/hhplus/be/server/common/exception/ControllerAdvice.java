package kr.hhplus.be.server.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
// Spring MVC 예외까지 처리
public class ControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConcertException.class)
    public ResponseEntity<ErrorResponse> handleConcertException(ConcertException exception)
    {
        ErrorCode errCode = exception.getErrorCode();
        log.error("{}", exception.getMessage());

        return ResponseEntity
                .status(errCode.getStatus())
                .body(
                        new ErrorResponse(errCode, errCode.getMessage())
                );
    }

    // ArgumentNotValid 오류에 대한 일관성있는 처리
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        ErrorCode e = ErrorCode.PARAMETER_NOT_VALID;

        log.error("{}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErrorResponse(e, e.getMessage())
        );
    }
}
