package kr.hhplus.be.server.api.reservation.application;

import kr.hhplus.be.server.domain.outbox.Outbox;
import kr.hhplus.be.server.domain.outbox.components.OutboxCommander;
import kr.hhplus.be.server.domain.outbox.components.OutboxReader;
import kr.hhplus.be.server.domain.outbox.type.OutboxStatus;
import kr.hhplus.be.server.infrastructure.dataplatform.DataPlatformMockApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReservationConsumer {
    private final DataPlatformMockApiClient dataPlatformMockApiClient;
    private final OutboxCommander outboxCommander;
    private final OutboxReader outboxReader;

    @KafkaListener(topics = {"reserveSeat"}, groupId = "reservation")
    public void listen(String reservationInfo)
    {
        try {
            dataPlatformMockApiClient.sendReservation(reservationInfo);
            String[] splits = reservationInfo.split("-");
            // outbox 상태 변경 (published)
            Outbox outbox = outboxReader.readByReservationId(Long.valueOf(splits[0]));
            outbox.setStatus(OutboxStatus.PUBLISHED);
            outboxCommander.writeOutbox(outbox);
        } catch (RuntimeException re)
        {
            log.error("{}", re.getMessage());
        }
    }
}
