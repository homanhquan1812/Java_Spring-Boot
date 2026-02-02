/*
UNIQUEIDENTIFIER = UUID
NEWID() = gen_random_uuid()
NVARCHAR = VARCHAR, but supports Unicode for languages like Vietnamese
DATETIME = TIMESTAMP
GETDATE() = CURRENT_TIMESTAMP
VARCHAR(MAX) = TEXT
CONSTRAINT FK_<current_table>_<reference_table> FOREIGN KEY...
*/

-- BRAND
CREATE TABLE brand (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,  -- BIGINT cho Long
    name NVARCHAR(50) NOT NULL,
    description NVARCHAR(MAX) NOT NULL,
    address NVARCHAR(MAX) NOT NULL,
    opening_hours TIME NOT NULL,
    closing_hours TIME NOT NULL,
    deleted_at DATETIME NULL, -- Not yet deleted
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);

INSERT INTO brand (name, description, address, opening_hours, closing_hours)
VALUES
(N'Tiệm Bánh Ngọt SweetHome', N'Tiệm bánh ngọt chuyên các loại bánh Âu cao cấp', N'123 Đường Hoa Hồng, Quận 1, TP.HCM', '08:00', '21:00');

-- USER_INFO
CREATE TABLE user_info (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY DEFAULT NEWID(),
    full_name NVARCHAR(50) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    phone VARCHAR(10) UNIQUE NOT NULL,
    gender VARCHAR(10) NOT NULL,
    address NVARCHAR(MAX) NOT NULL
);

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

-- PRODUCT
CREATE TABLE product (
    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,  -- ⭐ BIGINT cho Long
    brand_id BIGINT NOT NULL,                       -- ⭐ BIGINT cho foreign key
    name NVARCHAR(50) NOT NULL,
    description NVARCHAR(MAX) NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    deleted_at DATETIME2 NULL, -- Not yet deleted
    created_at DATETIME2 NOT NULL,
    updated_at DATETIME2 NOT NULL,
    created_by UNIQUEIDENTIFIER NOT NULL,
    updated_by UNIQUEIDENTIFIER NOT NULL,
    deleted_by UNIQUEIDENTIFIER NULL,
    version BIGINT NOT NULL DEFAULT 0,
    status NVARCHAR(10) NOT NULL,
    CONSTRAINT FK_product_brand FOREIGN KEY (brand_id)
        REFERENCES brand(id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

INSERT INTO product (brand_id, name, description, price, created_at, updated_at, created_by, updated_by, status)
VALUES
('1', N'Bánh Tiramisu', N'Bánh ngọt Ý với lớp kem mascarpone và cacao', 120.00, SYSDATETIME(), SYSDATETIME(), '55555555-aaaa-bbbb-cccc-000000000001', '55555555-aaaa-bbbb-cccc-000000000001', 'ACTIVE'),
('1', N'Bánh Cheesecake Dâu', N'Cheesecake mềm mịn phủ sốt dâu tây', 95.50, SYSDATETIME(), SYSDATETIME(), '55555555-aaaa-bbbb-cccc-000000000001', '55555555-aaaa-bbbb-cccc-000000000001', 'ACTIVE'),
('1', N'Bánh Mousse Socola', N'Mousse socola béo ngậy tan chảy trong miệng', 110.00, SYSDATETIME(), SYSDATETIME(), '55555555-aaaa-bbbb-cccc-000000000001', '55555555-aaaa-bbbb-cccc-000000000001', 'ACTIVE'),
('1', N'Bánh Macaron Pháp', N'Macaron nhiều màu với nhân kem hạnh nhân', 150.00, SYSDATETIME(), SYSDATETIME(), '55555555-aaaa-bbbb-cccc-000000000001', '55555555-aaaa-bbbb-cccc-000000000001', 'ACTIVE'),
('1', N'Bánh Cupcake Vanilla', N'Cupcake vị vanilla phủ kem bơ ngọt ngào', 45.00, SYSDATETIME(), SYSDATETIME(), '55555555-aaaa-bbbb-cccc-000000000001', '55555555-aaaa-bbbb-cccc-000000000001', 'ACTIVE'),
('1', N'Bánh Brownie Socola', N'Brownie đậm vị socola, ăn kèm hạt óc chó', 70.00, SYSDATETIME(), SYSDATETIME(), '55555555-aaaa-bbbb-cccc-000000000001', '55555555-aaaa-bbbb-cccc-000000000001', 'ACTIVE'),
('1', N'Bánh Red Velvet', N'Bánh Red Velvet mềm mịn với lớp kem phô mai', 130.00, SYSDATETIME(), SYSDATETIME(), '55555555-aaaa-bbbb-cccc-000000000001', '55555555-aaaa-bbbb-cccc-000000000001', 'ACTIVE'),
('1', N'Bánh Choux Kem', N'Bánh su kem nhân vani thơm béo', 55.00, SYSDATETIME(), SYSDATETIME(), '55555555-aaaa-bbbb-cccc-000000000001', '55555555-aaaa-bbbb-cccc-000000000001', 'ACTIVE'),
('1', N'Bánh Opera', N'Bánh Opera nhiều lớp socola và cà phê', 140.00, SYSDATETIME(), SYSDATETIME(), '55555555-aaaa-bbbb-cccc-000000000001', '55555555-aaaa-bbbb-cccc-000000000001', 'ACTIVE');


-- USER
CREATE TABLE users (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY DEFAULT NEWID(),
    user_info_id UNIQUEIDENTIFIER NOT NULL,
    brand_id BIGINT NOT NULL,
    status VARCHAR(10) NOT NULL,
    role VARCHAR(10) NOT NULL,
    deleted_at DATETIME NULL, -- Not yet deleted
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_users_user_info FOREIGN KEY (user_info_id)
        REFERENCES user_info(id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_users_brand FOREIGN KEY (brand_id)
        REFERENCES brand(id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

INSERT INTO users (id, user_info_id, brand_id, status, role)
VALUES
('44444444-aaaa-bbbb-cccc-000000000001', '33333333-aaaa-bbbb-cccc-000000000001', '1', 'ACTIVE', 'USER'),
('44444444-aaaa-bbbb-cccc-000000000002', '33333333-aaaa-bbbb-cccc-000000000002', '1', 'DELETED', 'USER'),
('44444444-aaaa-bbbb-cccc-000000000003', '33333333-aaaa-bbbb-cccc-000000000003', '1', 'ACTIVE', 'USER'),
('44444444-aaaa-bbbb-cccc-000000000004', '33333333-aaaa-bbbb-cccc-000000000004', '1', 'SUSPENDED', 'USER');

-- CART
CREATE TABLE cart (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY DEFAULT NEWID(),
    user_id UNIQUEIDENTIFIER NOT NULL,
    CONSTRAINT FK_cart_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE ON UPDATE NO ACTION
);

INSERT INTO cart (id, user_id)
VALUES
('77777777-aaaa-bbbb-cccc-000000000001', '44444444-aaaa-bbbb-cccc-000000000001'),
('77777777-aaaa-bbbb-cccc-000000000002', '44444444-aaaa-bbbb-cccc-000000000002'),
('77777777-aaaa-bbbb-cccc-000000000003', '44444444-aaaa-bbbb-cccc-000000000003');

-- CART_ITEM
CREATE TABLE cart_item (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY DEFAULT NEWID(),
    cart_id UNIQUEIDENTIFIER NOT NULL,
    product_id BIGINT NOT NULL,
    name NVARCHAR(50) NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    CONSTRAINT FK_cart_item_cart FOREIGN KEY (cart_id)
        REFERENCES cart(id) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT FK_cart_item_product FOREIGN KEY (product_id)
        REFERENCES product(id) ON DELETE CASCADE ON UPDATE NO ACTION
);

-- CART 1: 777...0001
INSERT INTO cart_item (cart_id, product_id, name, price, quantity) VALUES
('77777777-aaaa-bbbb-cccc-000000000001', '1', N'Bánh Tiramisu',         120.00, 2),
('77777777-aaaa-bbbb-cccc-000000000001', '6', N'Bánh Brownie Socola',   70.00,  1),
('77777777-aaaa-bbbb-cccc-000000000001', '8', N'Bánh Choux Kem',        55.00,  3);

-- CART 2: 777...0002
INSERT INTO cart_item (cart_id, product_id, name, price, quantity) VALUES
('77777777-aaaa-bbbb-cccc-000000000002', '2', N'Bánh Cheesecake Dâu',   95.50,  1),
('77777777-aaaa-bbbb-cccc-000000000002', '4', N'Bánh Macaron Pháp',     150.00, 2),
('77777777-aaaa-bbbb-cccc-000000000002', '5', N'Bánh Cupcake Vanilla',  45.00,  4);

-- CART 3: 777...0003
INSERT INTO cart_item (cart_id, product_id, name, price, quantity) VALUES
('77777777-aaaa-bbbb-cccc-000000000003', '3', N'Bánh Mousse Socola',    110.00, 1),
('77777777-aaaa-bbbb-cccc-000000000003', '7', N'Bánh Red Velvet',       130.00, 2),
('77777777-aaaa-bbbb-cccc-000000000003', '9', N'Bánh Opera',            140.00, 1);

CREATE TABLE orders (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY DEFAULT NEWID(),
    user_id UNIQUEIDENTIFIER NOT NULL,
    total_price DECIMAL(18,2) NOT NULL,
    status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_method NVARCHAR(20),
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_order_user FOREIGN KEY (user_id)
        REFERENCES users(id)
);

CREATE TABLE order_item (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY DEFAULT NEWID(),
    order_id UNIQUEIDENTIFIER NOT NULL,
    product_id BIGINT NOT NULL,
    name NVARCHAR(50) NOT NULL,
    price DECIMAL(18,2) NOT NULL,
    quantity INT NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_order_item_orders FOREIGN KEY (order_id)
        REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT FK_order_item_product FOREIGN KEY (product_id)
        REFERENCES product(id)
);

-- STAFF (Chef & Waiter)
CREATE TABLE staff (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY DEFAULT NEWID(),
    user_info_id UNIQUEIDENTIFIER NOT NULL,
    brand_id BIGINT NOT NULL,
    status VARCHAR(10) NOT NULL,
    role VARCHAR(10) NOT NULL,
    salary DECIMAL(18,2) NOT NULL,
    department VARCHAR(3) NOT NULL,
    deleted_at DATETIME NULL, -- Not yet deleted
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_staff_user_info FOREIGN KEY (user_info_id)
        REFERENCES user_info(id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_staff_brand FOREIGN KEY (brand_id)
        REFERENCES brand(id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

INSERT INTO staff (id, user_info_id, brand_id, status, role, salary, department)
VALUES
-- Chef
('66666666-aaaa-bbbb-cccc-000000000001', '33333333-aaaa-bbbb-cccc-000000000006', '1', 'ACTIVE', 'CHEF', 15000000.00, 'BOH'),
('66666666-aaaa-bbbb-cccc-000000000002', '33333333-aaaa-bbbb-cccc-000000000007', '1', 'ACTIVE', 'CHEF', 13000000.00, 'BOH'),
-- Waiter
('66666666-aaaa-bbbb-cccc-000000000003', '33333333-aaaa-bbbb-cccc-000000000008', '1', 'ACTIVE', 'WAITER', 8000000.00, 'FOH'),
('66666666-aaaa-bbbb-cccc-000000000004', '33333333-aaaa-bbbb-cccc-000000000009', '1', 'ACTIVE', 'WAITER', 8000000.00, 'FOH');

-- ADMIN
CREATE TABLE admins (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY DEFAULT NEWID(),
    user_info_id UNIQUEIDENTIFIER NOT NULL,
    brand_id BIGINT NOT NULL,
    status VARCHAR(10) NOT NULL,
    role VARCHAR(10) NOT NULL,
    deleted_at DATETIME NULL, -- Not yet deleted
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_admins_user_info FOREIGN KEY (user_info_id)
        REFERENCES user_info(id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT FK_admins_brand FOREIGN KEY (brand_id)
        REFERENCES brand(id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

INSERT INTO admins (id, user_info_id, brand_id, status, role)
VALUES
('55555555-aaaa-bbbb-cccc-000000000001', '33333333-aaaa-bbbb-cccc-000000000005', '1', 'ACTIVE', 'ADMIN');

-- INDEX
-- 1 brand watches the list of all products

CREATE INDEX idx_product_brand_id ON product(brand_id);
-- Users lookup by user_info
CREATE INDEX idx_users_user_info_id ON users(user_info_id);
CREATE INDEX idx_users_brand_id ON users(brand_id);

-- Staff lookup
CREATE INDEX idx_staff_user_info_id ON staff(user_info_id);
CREATE INDEX idx_staff_brand_id ON staff(brand_id);
CREATE INDEX idx_staff_department ON staff(department);

-- Admins lookup
CREATE INDEX idx_admins_user_info_id ON admins(user_info_id);
CREATE INDEX idx_admins_brand_id ON admins(brand_id);

-- Cart lookup by user
CREATE INDEX idx_cart_user_id ON cart(user_id);

-- Cart items lookup
CREATE INDEX idx_cart_item_cart_id ON cart_item(cart_id);

-- Soft delete queries (tìm records chưa bị xóa)
CREATE INDEX idx_brand_deleted_at ON brand(deleted_at);
CREATE INDEX idx_users_deleted_at ON users(deleted_at);
CREATE INDEX idx_staff_deleted_at ON staff(deleted_at);
CREATE INDEX idx_admins_deleted_at ON admins(deleted_at);
CREATE INDEX idx_product_deleted_at ON product(deleted_at);