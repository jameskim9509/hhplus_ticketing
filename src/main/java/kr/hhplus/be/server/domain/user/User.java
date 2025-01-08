package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.waiting_queue.WaitingQueue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static kr.hhplus.be.server.domain.user.components.UserModifier.MAX_POINT;

@Entity
@Table(name = "user")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private Long balance;
    private String uuid;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<WaitingQueue> waitingQueueList;

    public void chargePoint(Long chargePoint)
    {
        if (MAX_POINT < this.balance + chargePoint) {}
        this.balance += chargePoint;
    }

    public void usePoint(Long usePoint)
    {
        if (this.balance < usePoint) {}
        this.balance -= usePoint;
    }
}
