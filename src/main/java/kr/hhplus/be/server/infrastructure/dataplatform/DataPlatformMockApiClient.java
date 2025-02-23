package kr.hhplus.be.server.infrastructure.dataplatform;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataPlatformMockApiClient {
    public void sendReservation(String reservationInfo)
    {
        // 무조건 성공
        log.info("{}", "예약 데이터 전송 성공");
//        throw new RuntimeException();
    }
}
