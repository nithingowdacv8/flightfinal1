CREATE TABLE flight (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    origin SMALLINT UNSIGNED NOT NULL,
    destination SMALLINT UNSIGNED NOT NULL,
    departure DATETIME NOT NULL,
    estimated_arrival DATETIME NOT NULL,
    actual_arrival DATETIME,
    seat_rows TINYINT UNSIGNED NOT NULL,
    seat_columns TINYINT UNSIGNED NOT NULL,
    FOREIGN KEY (origin) REFERENCES airport(id),
    FOREIGN KEY (destination) REFERENCES airport(id),
    CONSTRAINT seat_columns_check CHECK (seat_columns BETWEEN 1 AND 26)
);