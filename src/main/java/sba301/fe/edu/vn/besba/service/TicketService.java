package sba301.fe.edu.vn.besba.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sba301.fe.edu.vn.besba.entity.Ticket;
import sba301.fe.edu.vn.besba.exception.CustomException;
import sba301.fe.edu.vn.besba.repository.TicketRepository;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;

    @Transactional
    public String checkInTicket(String qrCode) {
        Ticket ticket = ticketRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new CustomException(404, "Vé không tồn tại hoặc mã QR sai", HttpStatus.NOT_FOUND));

        if (Boolean.TRUE.equals(ticket.getCheckInStatus())) {
            throw new CustomException(400, "Vé này đã được sử dụng (Checked-in) trước đó!", HttpStatus.BAD_REQUEST);
        }

        ticket.setCheckInStatus(true);
        ticketRepository.save(ticket);
        return "Check-in thành công cho ghế: " + ticket.getSeat().getRowName() + ticket.getSeat().getSeatNumber();
    }
}