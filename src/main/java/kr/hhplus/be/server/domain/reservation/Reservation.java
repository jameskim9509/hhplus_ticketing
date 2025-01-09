package kr.hhplus.be.server.domain.reservation;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.reservation.type.ReservationStatus;
import kr.hhplus.be.server.domain.seat.Seat;
import kr.hhplus.be.server.domain.user.User;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Entity
@Table(name = "reservation")
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
//    @Column(name = "user_id", insertable = false, updatable = false)
//    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private Concert concert;
//    @Column(name = "concert_id", insertable = false, updatable = false)
//    private Long concertId;
    private String concertName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;
    // seat_id 컬럼을 조회용으로 중복 설정
//    @Column(name = "seat_id", insertable = false, updatable = false)
//    private Long seatId;
    private Long seatNumber;
    private Long seatCost;

    @Setter
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private LocalDateTime expiredAt;

    public void setConcert(Concert concert)
    {
        this.concert = concert;
//        this.concertId = concert.getId();
        this.concertName = concert.getName();
    }

    public void setSeat(Seat seat)
    {
        this.seat = seat;
//        this.seatId = seat.getId();
        this.seatCost = seat.getCost();
        this.seatNumber = seat.getNumber();
    }

    public void confirm(LocalDateTime expiredAt)
    {
        this.status = ReservationStatus.RESERVED;
        this.expiredAt = expiredAt;
    }
}
