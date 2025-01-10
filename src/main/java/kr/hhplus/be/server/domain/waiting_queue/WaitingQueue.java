package kr.hhplus.be.server.domain.waiting_queue;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.waiting_queue.type.WaitingQueueStatus;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="waiting_queue")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitingQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waiting_queue_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uuid")
    private User user;
//    @Column(insertable = false, updatable = false)
//    private String uuid;

    @Setter
    @Enumerated(EnumType.STRING)
    private WaitingQueueStatus status;
    @Setter
    private LocalDateTime expiredAt;

    public void expire(LocalDateTime now)
    {
        this.expiredAt = now;
        this.status = WaitingQueueStatus.EXPIRED;
    }

    public void setUser(User user)
    {
        this.user = user;
        user.getWaitingQueueList().add(this);
    }
}
