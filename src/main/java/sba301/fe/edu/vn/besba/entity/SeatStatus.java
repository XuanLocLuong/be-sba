package sba301.fe.edu.vn.besba.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seat_status")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SeatStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(nullable = false)
    private String status; // AVAILABLE, RESERVED, BOOKED

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
}
