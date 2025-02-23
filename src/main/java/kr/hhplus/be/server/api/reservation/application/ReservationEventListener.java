package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.api.reservation.dto.ReservationSuccessEvent;
import kr.hhplus.be.server.domain.outbox.Outbox;
import kr.hhplus.be.server.domain.outbox.components.OutboxCommander;
import kr.hhplus.be.server.domain.outbox.components.OutboxReader;
import kr.hhplus.be.server.domain.outbox.type.OutboxStatus;
import kr.hhplus.be.server.infrastructure.dataplatform.DataPlatformMockApiClient;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.lang.reflect.Array;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {
    private final NewTopic reservationTopic;
    private final KafkaTemplate<Long, String> kafkaTemplate;
    private final OutboxReader outboxReader;
    private final OutboxCommander outboxCommander;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutbox(ReservationSuccessEvent event)
    {
        String[] splits = event.reservationInfo().split("-");
        // outbox 상태 저장 (init)
        outboxCommander.writeOutbox(
                Outbox.builder()
                        .reservationId(Long.valueOf(splits[0]))
                        .key(event.userId())
                        .payload(event.reservationInfo())
                        .status(OutboxStatus.INIT)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void reservationSuccessHandler(ReservationSuccessEvent event)
    {
        kafkaTemplate.send(
                reservationTopic.name(), event.userId(), event.reservationInfo()
        );
    }

    // 책임 분리 필요
    @Scheduled(cron = "0 */5 * * * *")
    public void republish()
    {
        outboxReader.readAllByStatus(OutboxStatus.INIT).forEach(o -> {
                    if (o.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5)))
                    {
                        kafkaTemplate.send(reservationTopic.name(), o.getKey(), o.getPayload());
                    }
                }
        );
    }
}
