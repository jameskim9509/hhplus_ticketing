package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.api.reservation.dto.ReservationSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void success(ReservationSuccessEvent event)
    {
        applicationEventPublisher.publishEvent(event);
    }
}
