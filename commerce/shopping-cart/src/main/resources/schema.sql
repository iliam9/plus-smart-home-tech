CREATE TABLE IF NOT EXISTS cart (
    id UUID PRIMARY KEY,
    username VARCHAR,
    state INT
);

CREATE TABLE IF NOT EXISTS product (
    cart_id UUID REFERENCES carts(id),
    product_id UUID,
    quantity INT,
    PRIMARY KEY (cart_id, product_id)
);