BEGIN TRANSACTION;
BEGIN TRY

    INSERT INTO brand (name, description, address, opening_hours, closing_hours, created_at, updated_at)
    VALUES
        (N'Tiệm Bánh Ngọt SweetHome', N'Tiệm bánh ngọt chuyên các loại bánh Âu cao cấp', N'123 Đường Hoa Hồng, Quận 1, TP.HCM', '08:00', '21:00', SYSDATETIME(), SYSDATETIME());

    DECLARE @brandId BIGINT = SCOPE_IDENTITY();

    INSERT INTO product (brand_id, name, description, price, created_at, updated_at, created_by, updated_by, status)
    VALUES
        (@brandId, N'Bánh Tiramisu', N'Bánh ngọt Ý với lớp kem mascarpone và cacao', 120.00, SYSDATETIME(), SYSDATETIME(), '55555555-aaaa-bbbb-cccc-000000000001', '55555555-aaaa-bbbb-cccc-000000000001', 'ACTIVE'),
        (@brandId, N'Bánh Cheesecake Dâu', N'Cheesecake mềm mịn phủ sốt dâu tây', 95.50, SYSDATETIME(), SYSDATETIME(), '55555555-aaaa-bbbb-cccc-000000000001', '55555555-aaaa-bbbb-cccc-000000000001', 'ACTIVE'),
        (@brandId, N'Bánh Mousse Socola', N'Mousse socola béo ngậy tan chảy trong miệng', 110.00, SYSDATETIME(), SYSDATETIME(), '55555555-aaaa-bbbb-cccc-000000000001', '55555555-aaaa-bbbb-cccc-000000000001', 'ACTIVE'),
        (@brandId, N'Bánh Macaron Pháp', N'Macaron nhiều màu với nhân kem hạnh nhân', 150.00, SYSDATETIME(), SYSDATETIME(), '55555555-aaaa-bbbb-cccc-000000000001', '55555555-aaaa-bbbb-cccc-000000000001', 'ACTIVE'),
        (@brandId, N'Bánh Cupcake Vanilla', N'Cupcake vị vanilla phủ kem bơ ngọt ngào', 45.00, SYSDATETIME(), SYSDATETIME(), '55555555-aaaa-bbbb-cccc-000000000001', '55555555-aaaa-bbbb-cccc-000000000001', 'ACTIVE'),
        (@brandId, N'Bánh Brownie Socola', N'Brownie đậm vị socola, ăn kèm hạt óc chó', 70.00, SYSDATETIME(), SYSDATETIME(), '55555555-aaaa-bbbb-cccc-000000000001', '55555555-aaaa-bbbb-cccc-000000000001', 'ACTIVE'),
        (@brandId, N'Bánh Red Velvet', N'Bánh Red Velvet mềm mịn với lớp kem phô mai', 130.00, SYSDATETIME(), SYSDATETIME(), '55555555-aaaa-bbbb-cccc-000000000001', '55555555-aaaa-bbbb-cccc-000000000001', 'ACTIVE'),
        (@brandId, N'Bánh Choux Kem', N'Bánh su kem nhân vani thơm béo', 55.00, SYSDATETIME(), SYSDATETIME(), '55555555-aaaa-bbbb-cccc-000000000001', '55555555-aaaa-bbbb-cccc-000000000001', 'ACTIVE'),
        (@brandId, N'Bánh Opera', N'Bánh Opera nhiều lớp socola và cà phê', 140.00, SYSDATETIME(), SYSDATETIME(), '55555555-aaaa-bbbb-cccc-000000000001', '55555555-aaaa-bbbb-cccc-000000000001', 'ACTIVE');

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

    INSERT INTO users (id, user_info_id, brand_id, status, role, created_at, updated_at)
    VALUES
        ('44444444-aaaa-bbbb-cccc-000000000001', '33333333-aaaa-bbbb-cccc-000000000001', '1', 'ACTIVE', 'USER', SYSDATETIME(), SYSDATETIME()),
        ('44444444-aaaa-bbbb-cccc-000000000002', '33333333-aaaa-bbbb-cccc-000000000002', '1', 'DELETED', 'USER', SYSDATETIME(), SYSDATETIME()),
        ('44444444-aaaa-bbbb-cccc-000000000003', '33333333-aaaa-bbbb-cccc-000000000003', '1', 'ACTIVE', 'USER', SYSDATETIME(), SYSDATETIME()),
        ('44444444-aaaa-bbbb-cccc-000000000004', '33333333-aaaa-bbbb-cccc-000000000004', '1', 'SUSPENDED', 'USER', SYSDATETIME(), SYSDATETIME());

    INSERT INTO cart (id, user_id)
    VALUES
        ('77777777-aaaa-bbbb-cccc-000000000001', '44444444-aaaa-bbbb-cccc-000000000001'),
        ('77777777-aaaa-bbbb-cccc-000000000002', '44444444-aaaa-bbbb-cccc-000000000002'),
        ('77777777-aaaa-bbbb-cccc-000000000003', '44444444-aaaa-bbbb-cccc-000000000003');

    INSERT INTO cart_item (cart_id, product_id, name, price, quantity)
    VALUES
        ('77777777-aaaa-bbbb-cccc-000000000001', '1', N'Bánh Tiramisu',         120.00, 2),
        ('77777777-aaaa-bbbb-cccc-000000000001', '6', N'Bánh Brownie Socola',   70.00,  1),
        ('77777777-aaaa-bbbb-cccc-000000000001', '8', N'Bánh Choux Kem',        55.00,  3),
        ('77777777-aaaa-bbbb-cccc-000000000002', '2', N'Bánh Cheesecake Dâu',   95.50,  1),
        ('77777777-aaaa-bbbb-cccc-000000000002', '4', N'Bánh Macaron Pháp',     150.00, 2),
        ('77777777-aaaa-bbbb-cccc-000000000002', '5', N'Bánh Cupcake Vanilla',  45.00,  4),
        ('77777777-aaaa-bbbb-cccc-000000000003', '3', N'Bánh Mousse Socola',    110.00, 1),
        ('77777777-aaaa-bbbb-cccc-000000000003', '7', N'Bánh Red Velvet',       130.00, 2),
        ('77777777-aaaa-bbbb-cccc-000000000003', '9', N'Bánh Opera',            140.00, 1);

    INSERT INTO staff (id, user_info_id, brand_id, status, role, salary, department, created_at, updated_at)
    VALUES
        ('66666666-aaaa-bbbb-cccc-000000000001', '33333333-aaaa-bbbb-cccc-000000000006', '1', 'ACTIVE', 'CHEF', 15000000.00, 'BOH', SYSDATETIME(), SYSDATETIME()),
        ('66666666-aaaa-bbbb-cccc-000000000002', '33333333-aaaa-bbbb-cccc-000000000007', '1', 'ACTIVE', 'CHEF', 13000000.00, 'BOH', SYSDATETIME(), SYSDATETIME()),
        ('66666666-aaaa-bbbb-cccc-000000000003', '33333333-aaaa-bbbb-cccc-000000000008', '1', 'ACTIVE', 'WAITER', 8000000.00, 'FOH', SYSDATETIME(), SYSDATETIME()),
        ('66666666-aaaa-bbbb-cccc-000000000004', '33333333-aaaa-bbbb-cccc-000000000009', '1', 'ACTIVE', 'WAITER', 8000000.00, 'FOH', SYSDATETIME(), SYSDATETIME());

    INSERT INTO admins (id, user_info_id, brand_id, status, role, created_at, updated_at)
    VALUES
        ('55555555-aaaa-bbbb-cccc-000000000001', '33333333-aaaa-bbbb-cccc-000000000005', '1', 'ACTIVE', 'ADMIN', SYSDATETIME(), SYSDATETIME());

COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    ROLLBACK TRANSACTION;
    THROW;
END CATCH;