package kr.hhplus.be.server.domain.seat;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.concert.Concert;
import kr.hhplus.be.server.domain.seat.type.SeatStatus;
import lombok.*;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Seat {
    @Id
    @GeneratedValue
    @Column(name = "seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private Concert concert;
    private Long concertId;

    private Long number;

    private Long cost;

    @Setter
    @Enumerated(EnumType.STRING)
    private SeatStatus status;
}
