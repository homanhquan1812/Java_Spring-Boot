INSERT INTO user_info (id, full_name, username, password, email, phone, gender, address)
VALUES
    -- USER (password123)
    ('33333333-aaaa-bbbb-cccc-000000000001', N'Nguyễn Văn An', 'nguyenvanan', '$2a$12$kZkrvFjTjb72WX0cM/Iz4uRZEKHaQRFWC7gzwZwURsgFAzNiPJ.4G', 'vanan@gmail.com', '0901234567', 'MALE', N'456 Đường Lê Lợi, Quận 1, TP.HCM'),
    ('33333333-aaaa-bbbb-cccc-000000000002', N'Trần Thị Bình', 'tranthib', '$2a$12$QPLA9lGzwU3PWDZ3V0ysresN69jJVY8bq7TEjFvfFexcrGM3JlTTu', 'thibinh@gmail.com', '0901234568', 'FEMALE', N'789 Đường Nguyễn Huệ, Quận 1, TP.HCM'),
    ('33333333-aaaa-bbbb-cccc-000000000003', N'Lê Minh Châu', 'leminhchau', '$2a$12$bNxhe1apac8qsWONguGceeubTcaflfav2WwI8Hynbb8.ziQON2vya', 'minhchau@gmail.com', '0901234569', 'FEMALE', N'321 Đường Pasteur, Quận 3, TP.HCM'),
    ('33333333-aaaa-bbbb-cccc-000000000004', N'Phạm Quốc Dũng', 'phamquocdung', '$2a$12$uHhtQ3U5L4shgAAU55GpbOt1m5VDxZSO8P2u.cnnj.eP1ImvuoyhO', 'quocdung@gmail.com', '0901234570', 'MALE', N'654 Đường Cách Mạng Tháng 8, Quận 10, TP.HCM'),
    -- ADMIN (admin123)
    ('33333333-aaaa-bbbb-cccc-000000000005', N'Võ Thị Hoa', 'vothihoa', '$2a$12$KaFcv2MYGS2eh7Ol1Pz8sO.aOkci2qGlhXhGZt1bu2o7jwozGKMAW', 'admin.hoa@sweethome.com', '0909876543', 'FEMALE', N'123 Đường Hoa Hồng, Quận 1, TP.HCM'),
    -- Nhân viên bếp (staff123)
    ('33333333-aaaa-bbbb-cccc-000000000006', N'Hoàng Văn Khánh', 'hoangvankhanh', '$2a$12$rSxQ1k3vrg85ASnypmo2COnKoDfdQsPU0mPil3rigixqw1WRZ5FAm', 'chef.khanh@sweethome.com', '0908765432', 'MALE', N'111 Đường Trần Hưng Đạo, Quận 5, TP.HCM'),
    ('33333333-aaaa-bbbb-cccc-000000000007', N'Đỗ Thị Lan', 'dothilan', '$2a$12$Vdw7IQ4tlORg8YcO1eiaZOYmjeO/AZyzdePoQgRn2HCaEXE/8SBMK', 'chef.lan@sweethome.com', '0908765433', 'FEMALE', N'222 Đường Lý Thường Kiệt, Quận 10, TP.HCM'),
    -- Nhân viên phục vụ (staff123)
    ('33333333-aaaa-bbbb-cccc-000000000008', N'Bùi Minh Nam', 'buiminhnam', '$2a$12$wcwQ4yEAfRRj/ZCh9UFcpeBiOOMe1eoQtT.vwBkDcoXWCJLh04f9S', 'waiter.nam@sweethome.com', '0907654321', 'MALE', N'333 Đường Hai Bà Trưng, Quận 3, TP.HCM'),
    ('33333333-aaaa-bbbb-cccc-000000000009', N'Lý Thị Oanh', 'lythioanh', '$2a$12$b41lA85g0RJmQYuPzy6TP.Io7.FA8XUKNj3vY60ROX/ouczug2ALC', 'waiter.oanh@sweethome.com', '0907654322', 'FEMALE', N'444 Đường Điện Biên Phủ, Quận Bình Thạnh, TP.HCM');