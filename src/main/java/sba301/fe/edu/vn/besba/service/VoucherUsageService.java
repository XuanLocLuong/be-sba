package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sba301.fe.edu.vn.besba.dto.response.VoucherUsageResponse;
import sba301.fe.edu.vn.besba.repository.VoucherUsageRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherUsageService {
    private final VoucherUsageRepository usageRepository;

    public List<VoucherUsageResponse> getAllUsages() {
        return usageRepository.findAll().stream()
                .map(VoucherUsageResponse::fromEntity)
                .toList();
    }

    public List<VoucherUsageResponse> getUsagesByVoucherId(Integer voucherId) {
        return usageRepository.findByVoucherIdOrderByUsedAtDesc(voucherId).stream()
                .map(VoucherUsageResponse::fromEntity)
                .toList();
    }
}