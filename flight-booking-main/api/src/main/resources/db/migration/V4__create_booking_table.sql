CREATE TABLE booking (
    flight_id INT UNSIGNED NOT NULL,
    passenger_id INT UNSIGNED NOT NULL,
    ticket INT UNSIGNED NOT NULL,
    seat_row SMALLINT UNSIGNED NOT NULL,
    seat_column SMALLINT UNSIGNED NOT NULL,
    PRIMARY KEY (flight_id, passenger_id),
    FOREIGN KEY (flight_id) REFERENCES flight(id),
    FOREIGN KEY (passenger_id) REFERENCES passenger(id),
    CONSTRAINT ticket_unique UNIQUE (ticket),
    CONSTRAINT seat_unique UNIQUE (flight_id,seat_row, seat_column)
);