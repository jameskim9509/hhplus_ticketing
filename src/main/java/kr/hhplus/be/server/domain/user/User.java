package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.waiting_queue.WaitingQueue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private Long balance;
    private String uuid;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<WaitingQueue> waitingQueueList = new ArrayList<>();

    public void chargePoint(Long chargePoint)
    {
        if (MAX_POINT < this.balance + chargePoint)
            throw new RuntimeException("충전 가능 금액을 초과하였습니다.");
        this.balance += chargePoint;
    }

    public void usePoint(Long usePoint)
    {
        if (this.balance < usePoint)
            throw new RuntimeException("잔액이 부족합니다.");
        this.balance -= usePoint;
    }
}
