package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.ConcertException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.token.WaitingQueue;
import lombok.*;

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
    @Setter
    private String uuid;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private List<WaitingQueue> waitingQueueList = new ArrayList<>();

    @Version
    private int version;

    public void chargePoint(Long chargePoint)
    {
        if (MAX_POINT < this.balance + chargePoint)
            throw new ConcertException(ErrorCode.CHARGE_POINT_MAX);
        this.balance += chargePoint;
    }

    public void usePoint(Long usePoint)
    {
        if (this.balance < usePoint)
            throw new ConcertException(ErrorCode.NOT_ENOUGH_BALANCE);
        this.balance -= usePoint;
    }
}
