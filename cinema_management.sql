CREATE DATABASE IF NOT EXISTS cinema_management;
USE cinema_management;

-- 1. Roles
CREATE TABLE Roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
);

-- 2. Users
CREATE TABLE Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    role_id INT,
    reset_token VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES Roles(id)
);

-- 3. Movies
CREATE TABLE Movies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duration_minutes INT,
    release_date DATE,
    poster_url VARCHAR(255),
    status VARCHAR(20) -- UPCOMING, ONGOING, ENDED
);

-- 4. Rooms
CREATE TABLE Rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    total_seats INT
);

-- 5. Seats
CREATE TABLE Seats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_id INT,
    row_name VARCHAR(5),
    seat_number INT,
    seat_type VARCHAR(20), -- NORMAL, VIP, COUPLE
    FOREIGN KEY (room_id) REFERENCES Rooms(id)
);

-- 6. Showtimes
CREATE TABLE Showtimes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT,
    room_id INT,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    base_price DOUBLE,
    FOREIGN KEY (movie_id) REFERENCES Movies(id),
    FOREIGN KEY (room_id) REFERENCES Rooms(id)
);

-- 7. Seat_Status
CREATE TABLE Seat_Status (
    id INT AUTO_INCREMENT PRIMARY KEY,
    showtime_id INT,
    seat_id INT,
    status VARCHAR(20), -- AVAILABLE, RESERVED, BOOKED
    user_id INT NULL,
    FOREIGN KEY (showtime_id) REFERENCES Showtimes(id),
    FOREIGN KEY (seat_id) REFERENCES Seats(id),
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

-- 8. Vouchers
CREATE TABLE Vouchers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    discount_percent INT,
    max_discount_amount DOUBLE,
    expiry_date DATE
);

-- 9. Bookings
CREATE TABLE Bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    showtime_id INT,
    total_amount DOUBLE,
    status VARCHAR(20), -- PENDING, PAID, CANCELLED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (showtime_id) REFERENCES Showtimes(id)
);

-- 10. Tickets
CREATE TABLE Tickets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT,
    seat_id INT,
    ticket_price DOUBLE,
    qr_code VARCHAR(100) UNIQUE,
    check_in_status BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (booking_id) REFERENCES Bookings(id),
    FOREIGN KEY (seat_id) REFERENCES Seats(id)
);

-- 11. Voucher_Usage
CREATE TABLE Voucher_Usage (
    id INT AUTO_INCREMENT PRIMARY KEY,
    voucher_id INT,
    user_id INT,
    used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    booking_id INT,
    FOREIGN KEY (voucher_id) REFERENCES Vouchers(id),
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (booking_id) REFERENCES Bookings(id)
);

-- 12. Payments
CREATE TABLE Payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT,
    payment_method VARCHAR(50), -- VNPAY, MOMO
    transaction_id VARCHAR(100),
    amount DOUBLE,
    status VARCHAR(20), -- SUCCESS, FAILED
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES Bookings(id)
);

-- Thêm Roles
INSERT INTO Roles (role_name) VALUES ('ADMIN'), ('STAFF'), ('USER');

-- Thêm Users (Pass giả định)
INSERT INTO Users (username, password, full_name, email, role_id) VALUES 
('hiepbq', 'hash_pass_123', 'Bui Quang Hiep', 'hiep@fpt.edu.vn', 1),
('customer01', 'hash_pass_456', 'Nguyen Van A', 'vana@gmail.com', 3);

-- Thêm Movies
INSERT INTO Movies (title, duration_minutes, status) VALUES 
('Spider-Man: No Way Home', 148, 'ONGOING'),
('Dune: Part Two', 166, 'UPCOMING');

-- Thêm Room & Seats
INSERT INTO Rooms (name, total_seats) VALUES ('Cinema 01', 50);
INSERT INTO Seats (room_id, row_name, seat_number, seat_type) VALUES 
(1, 'A', 1, 'NORMAL'),
(1, 'A', 2, 'NORMAL'),
(1, 'H', 5, 'VIP');

-- Thêm Showtime
INSERT INTO Showtimes (movie_id, room_id, start_time, base_price) VALUES 
(1, 1, '2026-02-15 19:00:00', 80000);

-- Khởi tạo trạng thái ghế cho suất chiếu
INSERT INTO Seat_Status (showtime_id, seat_id, status) VALUES 
(1, 1, 'AVAILABLE'),
(1, 2, 'AVAILABLE'),
(1, 3, 'AVAILABLE');

-- Thêm Voucher
INSERT INTO Vouchers (code, discount_percent, max_discount_amount, expiry_date) VALUES 
('CHAO2026', 10, 20000, '2026-12-31');