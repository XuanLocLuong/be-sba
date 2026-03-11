package sba301.fe.edu.vn.besba.utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

@Component
public class EmailUtil {

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.password}")
    private String APP_PASSWORD;

    private static final String FROM_NAME = "Rạp Chiếu Phim NextGen Cinema";

    private Properties getMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        return props;
    }

    public boolean sendOTP(String toEmail, String otp, String name) {
        try {
            String subject = "Mã OTP Khôi Phục Mật Khẩu";
            String content = buildOTPContent(otp, name);
            return sendEmail(toEmail, subject, content);
        } catch (Exception e) {
            System.err.println("❌ Lỗi trong quá trình chuẩn bị OTP: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendEmail(String toEmail, String subject, String content) {
        try {
            Properties props = getMailProperties();
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, APP_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail, FROM_NAME, "UTF-8"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setSentDate(new Date());
            message.setContent(content, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("✅ Gửi email thành công đến: " + toEmail);
            return true;

        } catch (AuthenticationFailedException e) {
            // Lỗi này xảy ra do sai Email hoặc App Password
            System.err.println("❌ Lỗi xác thực tài khoản Gmail (Kiểm tra lại App Password): " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            // Lỗi này xảy ra khi sai cấu hình Port, mạng nội bộ chặn SMTP...
            System.err.println("❌ Lỗi kết nối đến máy chủ Mail (SMTP/Network): " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {

            System.err.println("❌ Lỗi định dạng tên người gửi: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {

            System.err.println("❌ Lỗi không xác định khi gửi email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String buildOTPContent(String otp, String name) {
        try {
            return """
                <!DOCTYPE html>
                <html><head><meta charset='UTF-8'>
                <style>
                body{font-family:sans-serif;padding:20px;background:#f4f4f4;}
                .box{max-width:600px;margin:auto;background:#fff;padding:20px 30px;border-radius:8px;border:1px solid #ddd;}
                h2{color:#333;}
                p{color:#555;line-height:1.5;}
                .otp-container{text-align:center;padding:15px;background:#e9ecef;border-radius:4px;margin:25px 0;}
                .otp{font-size:36px;font-weight:bold;color:#007bff;letter-spacing:5px;}
                hr{border:0;border-top:1px solid #eee;margin:20px 0;}
                b{color:#333;}
                </style></head><body>
                <div class='box'>
                <h2>Khôi Phục Mật Khẩu - %s</h2>
                <p>Xin chào <b>%s</b>,</p>
                <p>Bạn vừa yêu cầu đặt lại mật khẩu. Vui lòng sử dụng mã OTP dưới đây để xác thực:</p>
                <div class='otp-container'><div class='otp'>%s</div></div>
                <p>Vui lòng sử dụng mã này để đổi mật khẩu mới.</p>
                <hr><p style='font-size:12px;color:#999;'>Không chia sẻ mã này cho ai. Nếu không phải bạn yêu cầu, hãy bỏ qua email này.</p>
                <p>Trân trọng,<br>Đội Ngũ Hỗ Trợ %s</p>
                </div></body></html>
            """.formatted(FROM_NAME, name, otp, FROM_NAME);
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi render giao diện HTML cho OTP: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }
}