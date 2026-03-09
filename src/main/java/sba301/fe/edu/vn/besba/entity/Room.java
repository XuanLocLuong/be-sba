package sba301.fe.edu.vn.besba.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "rooms")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(name = "total_seats")
    @Min(value = 1, message = "Tổng số ghế phải tối thiểu là 1")
    private Integer totalSeats;

    @OneToMany(mappedBy = "room" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats;

    @OneToMany(mappedBy = "room")
    private List<Showtime> showtimes;

    @Builder.Default
    private String status = "ACTIVE";
}
