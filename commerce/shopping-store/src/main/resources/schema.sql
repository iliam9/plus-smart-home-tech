CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS product (
    product_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    image_src VARCHAR(255),
    quantity_state VARCHAR(50),
    product_state VARCHAR(50),
    rating DOUBLE PRECISION NOT NULL,
    product_category VARCHAR(50),
    price DOUBLE PRECISION NOT NULL
);