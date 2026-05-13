-- phpMyAdmin SQL Dump
-- Database: `user_management_db`

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL COMMENT 'Unique user identifier',
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `role` varchar(20) DEFAULT 'user',
  `total_login` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Dumping data for table `users` 
INSERT INTO `users` (`user_id`, `first_name`, `last_name`, `email`, `username`, `password`, `created_at`, `role`, `total_login`) VALUES
(1, 'Admin', 'Admin', 'admin', 'Admin', 'admin', '2026-05-05 14:42:15', 'admin', NULL),
(2, 'renz', 'palmes', 'renz@gmail.com', 'renz', '1432', '2026-05-05 14:42:15', 'user', NULL),
(4, 'hezron', 'del rosario', 'hezron@gmail.com', 'hezron', '1234', '2026-05-05 14:42:15', 'user', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `user_logs`
--

CREATE TABLE `user_logs` (
  `log_id` int(11) NOT NULL COMMENT 'Unique logs identifier',
  `user_id` int(11) NOT NULL COMMENT 'Unique user identifier',
  `time_in` timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'User Time In',
  `time_out` timestamp NULL DEFAULT NULL COMMENT 'User Time Out'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes and Constraints
--

ALTER TABLE `users` ADD PRIMARY KEY (`user_id`);
ALTER TABLE `user_logs` ADD PRIMARY KEY (`log_id`), ADD KEY `user_id` (`user_id`);

ALTER TABLE `users` MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
ALTER TABLE `user_logs` MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `user_logs` ADD CONSTRAINT `user_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

COMMIT;
