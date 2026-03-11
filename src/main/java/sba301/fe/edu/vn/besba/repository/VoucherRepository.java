package sba301.fe.edu.vn.besba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sba301.fe.edu.vn.besba.entity.Voucher;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {

    Optional<Voucher> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT v FROM Voucher v WHERE v.status = :status AND v.expiryDate >= :now AND v.quantity > v.usedCount")
    List<Voucher> findActiveVouchers(@Param("status") Integer status, @Param("now") Date now);
}