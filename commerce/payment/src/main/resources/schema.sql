DROP TABLE IF EXISTS payment;

CREATE TABLE IF NOT EXISTS payment (
    payment_id UUID PRIMARY KEY,
    total_payment DOUBLE PRECISION NOT NULL,
    delivery_total DOUBLE PRECISION NOT NULL,
    fee_total DOUBLE PRECISION NOT NULL,
    payment_state VARCHAR NOT NULL
);