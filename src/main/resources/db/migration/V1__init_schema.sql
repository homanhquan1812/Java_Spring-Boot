BEGIN TRANSACTION;
BEGIN TRY

    CREATE TABLE brand (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        name NVARCHAR(50) NOT NULL,
        description NVARCHAR(MAX) NOT NULL,
        address NVARCHAR(MAX) NOT NULL,
        opening_hours TIME NOT NULL,
        closing_hours TIME NOT NULL,
        created_at DATETIME NOT NULL,
        updated_at DATETIME NOT NULL,
        version BIGINT NOT NULL DEFAULT 0
    );

    CREATE TABLE product (
        id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
        brand_id BIGINT NOT NULL,
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

    CREATE TABLE users (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY DEFAULT NEWID(),
        user_info_id UNIQUEIDENTIFIER NOT NULL,
        brand_id BIGINT NOT NULL,
        status VARCHAR(10) NOT NULL,
        role VARCHAR(10) NOT NULL,
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        version BIGINT NOT NULL DEFAULT 0,
        CONSTRAINT FK_users_user_info FOREIGN KEY (user_info_id)
            REFERENCES user_info(id) ON DELETE NO ACTION ON UPDATE NO ACTION,
        CONSTRAINT FK_users_brand FOREIGN KEY (brand_id)
            REFERENCES brand(id) ON DELETE NO ACTION ON UPDATE NO ACTION
    );

    CREATE TABLE cart (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY DEFAULT NEWID(),
        user_id UNIQUEIDENTIFIER NOT NULL,
        CONSTRAINT FK_cart_user FOREIGN KEY (user_id)
            REFERENCES users(id) ON DELETE CASCADE ON UPDATE NO ACTION
    );

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

    CREATE TABLE orders (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY DEFAULT NEWID(),
        user_id UNIQUEIDENTIFIER NOT NULL,
        total_price DECIMAL(18,2) NOT NULL,
        status NVARCHAR(20) NOT NULL DEFAULT 'PENDING',
        payment_method NVARCHAR(20),
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        version BIGINT NOT NULL DEFAULT 0,
        CONSTRAINT FK_order_user FOREIGN KEY (user_id)
            REFERENCES users(id) ON DELETE NO ACTION ON UPDATE NO ACTION
    );

    CREATE TABLE order_item (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY DEFAULT NEWID(),
        order_id UNIQUEIDENTIFIER NOT NULL,
        product_id BIGINT NOT NULL,
        name NVARCHAR(50) NOT NULL,
        price DECIMAL(18,2) NOT NULL,
        quantity INT NOT NULL,
        CONSTRAINT FK_order_item_orders FOREIGN KEY (order_id)
            REFERENCES orders(id) ON DELETE NO ACTION ON UPDATE NO ACTION,
        CONSTRAINT FK_order_item_product FOREIGN KEY (product_id)
            REFERENCES product(id) ON DELETE NO ACTION ON UPDATE NO ACTION
    );

    CREATE TABLE staff (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY DEFAULT NEWID(),
        user_info_id UNIQUEIDENTIFIER NOT NULL,
        brand_id BIGINT NOT NULL,
        status VARCHAR(10) NOT NULL,
        role VARCHAR(10) NOT NULL,
        salary DECIMAL(18,2) NOT NULL,
        department VARCHAR(3) NOT NULL,
        deleted_at DATETIME2 NULL, -- Not yet deleted
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        CONSTRAINT FK_staff_user_info FOREIGN KEY (user_info_id)
            REFERENCES user_info(id) ON DELETE NO ACTION ON UPDATE NO ACTION,
        CONSTRAINT FK_staff_brand FOREIGN KEY (brand_id)
            REFERENCES brand(id) ON DELETE NO ACTION ON UPDATE NO ACTION
    );

    CREATE TABLE admins (
        id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY DEFAULT NEWID(),
        user_info_id UNIQUEIDENTIFIER NOT NULL,
        brand_id BIGINT NOT NULL,
        status VARCHAR(10) NOT NULL,
        role VARCHAR(10) NOT NULL,
        deleted_at DATETIME2 NULL, -- Not yet deleted
        created_at DATETIME2 NOT NULL,
        updated_at DATETIME2 NOT NULL,
        CONSTRAINT FK_admins_user_info FOREIGN KEY (user_info_id)
            REFERENCES user_info(id) ON DELETE NO ACTION ON UPDATE NO ACTION,
        CONSTRAINT FK_admins_brand FOREIGN KEY (brand_id)
            REFERENCES brand(id) ON DELETE NO ACTION ON UPDATE NO ACTION
    );

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
    CREATE INDEX idx_product_deleted_at ON product(deleted_at);

COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    ROLLBACK TRANSACTION;
    THROW;
END CATCH;