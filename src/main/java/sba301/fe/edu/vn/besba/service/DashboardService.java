package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sba301.fe.edu.vn.besba.dto.response.DashboardResponse;
import sba301.fe.edu.vn.besba.entity.Booking;
import sba301.fe.edu.vn.besba.repository.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public DashboardResponse getDashboardStats() {

        long totalMovies = movieRepository.count();
        long totalUsers = userRepository.count();
        long totalBookings = bookingRepository.countByStatusNot("CANCELLED");

        // Tính tổng doanh thu toàn hệ thống
        Double totalRevenue = bookingRepository.findAll().stream()
                .filter(b -> "PAID".equals(b.getStatus()) || "COMPLETED".equals(b.getStatus()))
                .mapToDouble(b -> b.getTotalAmount())
                .sum();

        List<DashboardResponse.ChartData> dailyRevenue = new ArrayList<>();
        LocalDateTime sevenDaysAgo = LocalDate.now().minusDays(6).atStartOfDay();

        List<Booking> recentBookings = bookingRepository.findAllByCreatedAtAfterAndStatusIn(
                sevenDaysAgo, List.of("PAID", "COMPLETED"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateLabel = date.format(formatter);

            Double dayAmount = recentBookings.stream()
                    .filter(b -> b.getCreatedAt().toLocalDate().equals(date))
                    .mapToDouble(b -> b.getTotalAmount())
                    .sum();

            dailyRevenue.add(DashboardResponse.ChartData.builder()
                    .date(dateLabel)
                    .revenue(dayAmount)
                    .build());
        }

        // (Top doanh thu phim)
        List<Object[]> movieData = bookingRepository.getRevenueByMovie();
        List<DashboardResponse.MovieRevenue> movieRevenues = movieData.stream()
                .map(obj -> DashboardResponse.MovieRevenue.builder()
                        .movieName((String) obj[0])
                        .revenue((Double) obj[1])
                        .build())
                .sorted(Comparator.comparing(DashboardResponse.MovieRevenue::getRevenue).reversed())
                .limit(5)
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .totalMovies(totalMovies)
                .totalUsers(totalUsers)
                .totalBookings(totalBookings)
                .totalRevenue(totalRevenue != null ? totalRevenue : 0.0)
                .dailyRevenue(dailyRevenue)
                .movieRevenue(movieRevenues)
                .build();
    }
}