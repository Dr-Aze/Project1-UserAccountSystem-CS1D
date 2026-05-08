-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 08, 2026 at 06:18 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `user_management_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL COMMENT 'Unique user identifier',
  `first_name` varchar(50) DEFAULT NULL COMMENT 'User''s first name',
  `last_name` varchar(50) DEFAULT NULL COMMENT 'User''s last name',
  `email` varchar(100) DEFAULT NULL COMMENT 'User''s email address',
  `username` varchar(50) DEFAULT NULL COMMENT 'User''s chosen username',
  `password` varchar(255) DEFAULT NULL COMMENT 'Hashed password',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'Date and time of account\r\ncreation',
  `role` varchar(20) DEFAULT 'user',
  `total_login` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `first_name`, `last_name`, `email`, `username`, `password`, `created_at`, `role`, `total_login`) VALUES
(1, 'Admin', 'Admin', 'admin', 'admin', 'admin', '2026-05-05 14:42:15', 'admin', NULL),
(2, 'renz', 'palmes', 'renz@gmail.com', 'renz@gmail.com', '1432', '2026-05-05 14:42:15', 'user', NULL),
(4, 'hezron', 'del rosario', 'hezron@gmail.com', 'hezron@gmail.com', '1234', '2026-05-05 14:42:15', 'user', NULL);

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
-- Dumping data for table `user_logs`
--

INSERT INTO `user_logs` (`log_id`, `user_id`, `time_in`, `time_out`) VALUES
(1, 1, '2026-05-07 13:43:45', '2026-05-07 13:43:53'),
(2, 1, '2026-05-07 13:44:30', '2026-05-07 13:44:45'),
(3, 1, '2026-05-07 13:55:56', '2026-05-07 13:56:06'),
(4, 1, '2026-05-07 13:56:45', '2026-05-07 13:56:53'),
(5, 1, '2026-05-07 13:59:43', '2026-05-07 13:59:53'),
(6, 1, '2026-05-08 03:28:12', '2026-05-08 03:28:23'),
(7, 1, '2026-05-08 03:37:10', '2026-05-08 03:37:30'),
(8, 1, '2026-05-08 03:38:23', '2026-05-08 03:38:45'),
(9, 1, '2026-05-08 03:51:42', '2026-05-08 03:51:54'),
(10, 1, '2026-05-08 04:03:52', '2026-05-08 04:04:06'),
(11, 1, '2026-05-08 04:05:18', '2026-05-08 04:05:28'),
(12, 1, '2026-05-08 04:06:51', '2026-05-08 04:07:40'),
(13, 1, '2026-05-08 04:08:23', '2026-05-08 04:08:45'),
(14, 1, '2026-05-08 04:09:16', '2026-05-08 04:09:36'),
(15, 1, '2026-05-08 04:10:05', '2026-05-08 04:10:25'),
(16, 1, '2026-05-08 04:11:03', '2026-05-08 04:11:56'),
(17, 1, '2026-05-08 04:13:21', '2026-05-08 04:13:33'),
(18, 1, '2026-05-08 04:14:12', '2026-05-08 04:17:45'),
(19, 1, '2026-05-08 04:17:51', '2026-05-08 04:18:12');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`);

--
-- Indexes for table `user_logs`
--
ALTER TABLE `user_logs`
  ADD PRIMARY KEY (`log_id`),
  ADD KEY `user_id` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Unique user identifier', AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `user_logs`
--
ALTER TABLE `user_logs`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Unique logs identifier', AUTO_INCREMENT=20;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `user_logs`
--
ALTER TABLE `user_logs`
  ADD CONSTRAINT `user_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
