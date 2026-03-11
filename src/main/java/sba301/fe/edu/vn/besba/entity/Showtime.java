package sba301.fe.edu.vn.besba.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "showtimes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Showtime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "base_price")
    private Double basePrice;

    @OneToMany(mappedBy = "showtime")
    private List<SeatStatus> seatStatuses;

    @OneToMany(mappedBy = "showtime")
    private List<Booking> bookings;

    @Column(length = 20)
    @Builder.Default
    private String status = "SCHEDULED"; // SCHEDULED, NOW_SHOWING, ENDED, CANCELLED
}
