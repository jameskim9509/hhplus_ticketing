package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.api.reservation.dto.ReservationSuccessEvent;
import kr.hhplus.be.server.infrastructure.dataplatform.DataPlatformMockApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {
    private final DataPlatformMockApiClient dataPlatformMockApiClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void reservationSuccessHandler(ReservationSuccessEvent event)
    {
        dataPlatformMockApiClient.sendReservation();
    }
}
