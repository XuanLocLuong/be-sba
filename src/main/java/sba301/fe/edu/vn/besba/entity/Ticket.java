package sba301.fe.edu.vn.besba.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(name = "ticket_price")
    private Double ticketPrice;

    @Column(name = "qr_code", unique = true)
    private String qrCode;

    @Column(name = "check_in_status")
    private Boolean checkInStatus = false;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "is_cancelled")
    private Boolean isCancelled = false;
}
