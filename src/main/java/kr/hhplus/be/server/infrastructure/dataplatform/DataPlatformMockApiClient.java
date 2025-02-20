package kr.hhplus.be.server.infrastructure.dataplatform;

import org.springframework.stereotype.Component;

@Component
public class DataPlatformMockApiClient {
    public void sendReservation(String reservationInfo)
    {
        // 무조건 성공
        System.out.println("예약 데이터 전송 성공");
//        throw new RuntimeException();
    }
}
