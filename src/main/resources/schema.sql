CREATE TABLE IF NOT EXISTS concert (
  `concert_id` int AUTO_INCREMENT PRIMARY KEY,
  `name` varchar(255),
  `date` datetime
);

CREATE TABLE IF NOT EXISTS seat (
  `seat_id` int AUTO_INCREMENT PRIMARY KEY,
  `concert_id` int,
  `number` int,
  `cost` int,
  `status` varchar(255)
);

CREATE TABLE IF NOT EXISTS waiting_queue (
  `waiting_queue_id` int AUTO_INCREMENT PRIMARY KEY,
  `uuid` varchar(255),
  `status` varchar(255),
  `expired_at` datetime
);

CREATE TABLE IF NOT EXISTS reservation (
  `reservation_id` int AUTO_INCREMENT PRIMARY KEY,
  `user_id` int,
  `concert_id` int,
  `concert_name` varchar(255),
  `seat_id` int,
  `seat_number` int,
  `seat_cost` int,
  `status` varchar(255),
  `expired_at` datetime
);

CREATE TABLE IF NOT EXISTS payment (
  `payment_id` int AUTO_INCREMENT PRIMARY KEY,
  `reservation_id` int,
  `user_id` int,
  `point` int,
  `created_at` datetime
);

CREATE TABLE IF NOT EXISTS user (
  `user_id` int AUTO_INCREMENT PRIMARY KEY,
  `balance` int,
  `uuid` varchar(255)
);