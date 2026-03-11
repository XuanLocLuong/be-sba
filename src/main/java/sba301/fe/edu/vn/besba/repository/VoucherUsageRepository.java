package sba301.fe.edu.vn.besba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sba301.fe.edu.vn.besba.entity.VoucherUsage;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, Integer> {
    
    void deleteByBookingId(Integer bookingId);

    List<VoucherUsage> findByVoucherIdOrderByUsedAtDesc(Integer voucherId);

    Optional<VoucherUsage> findByBookingId(Integer bookingId);
}