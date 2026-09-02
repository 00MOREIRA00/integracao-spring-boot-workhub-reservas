CREATE TABLE rooms (
    id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    capacity INTEGER NOT NULL,
    active BOOLEAN NOT NULL,

    CONSTRAINT pk_rooms PRIMARY KEY (id),
    CONSTRAINT uk_rooms_name UNIQUE (name),
    CONSTRAINT ck_rooms_capacity_positive CHECK (capacity > 0)
)