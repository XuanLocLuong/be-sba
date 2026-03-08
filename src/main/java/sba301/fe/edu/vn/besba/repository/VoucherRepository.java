package sba301.fe.edu.vn.besba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sba301.fe.edu.vn.besba.dto.VoucherResponse;
import sba301.fe.edu.vn.besba.entity.Voucher;

import java.util.Date;
import java.util.List;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    @Query("SELECT v FROM Voucher v WHERE v.status = :status AND v.expiryDate >= :now AND v.quantity > v.usedCount")
    List<Voucher> findActiveVouchers(@Param("status") Integer status, @Param("now") Date now);
}
