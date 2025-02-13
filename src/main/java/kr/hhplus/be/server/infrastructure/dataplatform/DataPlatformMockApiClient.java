package kr.hhplus.be.server.infrastructure.dataplatform;

import org.springframework.stereotype.Component;

@Component
public class DataPlatformMockApiClient {
    public void sendReservation()
    {
        // 무조건 실패
        System.out.println("예약 데이터 전송 실패");
        throw new RuntimeException();
    }
}
