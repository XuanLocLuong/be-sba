package sba301.fe.edu.vn.besba.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "seats")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "row_name")
    private String rowName;

    @Column(name = "seat_number")
    private Integer seatNumber;

    @Column(name = "seat_type")
    private String seatType; // NORMAL, VIP, COUPLE

    @OneToMany(mappedBy = "seat")
    private List<SeatStatus> seatStatuses;

    @OneToMany(mappedBy = "seat")
    private List<Ticket> tickets;

    @Column(name = "additional_price")
    @Builder.Default
    private Double additionalPrice = 0.0;
}
