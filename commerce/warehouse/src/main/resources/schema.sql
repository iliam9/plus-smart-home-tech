DROP TABLE IF EXISTS warehouse_product;

CREATE TABLE IF NOT EXISTS warehouse_product (
    productId UUID PRIMARY KEY,
    weight DOUBLE,
    width DOUBLE,
    height DOUBLE,
    depth DOUBLE,
    fragile BOOLEAN,
    quantity INT
);