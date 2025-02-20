package kr.hhplus.be.server.infrastructure.core.outbox;

import kr.hhplus.be.server.domain.outbox.Outbox;
import kr.hhplus.be.server.domain.outbox.type.OutboxStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class OutboxReaderRepositoryImplTest {
    @Autowired
    private OutboxReaderRepositoryImpl outboxReaderRepository;
    @Autowired
    private OutboxJpaRepository outboxJpaRepository;

    @BeforeEach
    void addData()
    {
        outboxJpaRepository.save(
                Outbox.builder().reservationId(100L).status(OutboxStatus.INIT).build()
        );
        outboxJpaRepository.save(
                Outbox.builder().reservationId(101L).status(OutboxStatus.INIT).build()
        );
        outboxJpaRepository.save(
            Outbox.builder().reservationId(102L).status(OutboxStatus.INIT).build()
        );
        outboxJpaRepository.save(
            Outbox.builder().reservationId(103L).status(OutboxStatus.PUBLISHED).build()
        );
        outboxJpaRepository.save(
            Outbox.builder().reservationId(104L).status(OutboxStatus.PUBLISHED).build()
        );
    }

    @AfterEach
    void deleteData()
    {
        outboxJpaRepository.deleteAll();
    }

    @Test
    void readAllByStatus() {
//        // when
        List<Outbox> outboxList =
                outboxReaderRepository.readAllByStatus(OutboxStatus.INIT);

        // then
        Assertions.assertThat(outboxList.size()).isEqualTo(3L);
    }

    @Test
    void readByReservationId() {
        // when
        Outbox outbox =
                outboxReaderRepository.readByReservationId(104L).get();

        // then
        Assertions.assertThat(outbox.getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
    }
}