package sba301.fe.edu.vn.besba.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DashboardResponse {
    private long totalMovies;
    private long totalUsers;
    private long totalBookings;
    private double totalRevenue;

    private List<ChartData> dailyRevenue;

    private List<MovieRevenue> movieRevenue;

    @Data
    @Builder
    public static class ChartData {
        private String date;
        private Double revenue;
    }

    @Data
    @Builder
    public static class MovieRevenue {
        private String movieName;
        private Double revenue;
    }
}