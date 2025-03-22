DROP TABLE IF EXISTS cart_products;
DROP TABLE IF EXISTS cart;

CREATE TABLE IF NOT EXISTS cart (
    shopping_cart_id UUID PRIMARY KEY,
    username VARCHAR,
    state VARCHAR
);

CREATE TABLE IF NOT EXISTS cart_products (
    cart_id UUID REFERENCES cart(shopping_cart_id),
    product_id UUID,
    quantity INT,
    PRIMARY KEY (cart_id, product_id)
);