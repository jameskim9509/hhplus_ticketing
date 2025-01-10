package kr.hhplus.be.server.domain.concert;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.seat.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "concert")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Concert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_id")
    private Long id;

    private String name;

    @Temporal(TemporalType.DATE)
    private LocalDate date;

    @OneToMany(mappedBy = "concert", fetch = FetchType.LAZY)
    @Builder.Default
    List<Seat> seatList = new ArrayList<>();
}
