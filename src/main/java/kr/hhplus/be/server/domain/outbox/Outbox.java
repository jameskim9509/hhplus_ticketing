package kr.hhplus.be.server.domain.outbox;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.outbox.type.OutboxStatus;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Entity
@Table(name = "outbox")
@NoArgsConstructor
@AllArgsConstructor
public class Outbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbox_id")
    private Long id;

    private Long reservationId;

    @Column(name = "key_name")
    private Long key;
    private String payload;

    private LocalDateTime createdAt;

    @Setter
    @Enumerated(EnumType.STRING)
    private OutboxStatus status;
}
