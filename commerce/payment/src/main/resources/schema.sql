CREATE TABLE IF NOT EXISTS payment (
    payment_id UUID PRIMARY KEY,
    total_payment DOUBLE PRECISION,
    delivery_total DOUBLE PRECISION,
    fee_total DOUBLE PRECISION,
);