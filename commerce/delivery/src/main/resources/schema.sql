CREATE TABLE IF NOT EXISTS delivery (
    delivery_id UUID PRIMARY KEY,
    from_address_id UUID REFERENCES delivery_address(address_id),
    to_address_id UUID REFERENCES delivery_address(address_id),
    order_id UUID,
    delivery_state VARCHAR
);

CREATE TABLE IF NOT EXISTS delivery_address (
    address_id UUID DEFAULT gen_random_uuid() PRIMARY KEY
    country VARCHAR,
    city VARCHAR,
    street VARCHAR,
    house VARCHAR,
    flat VARCHAR
);