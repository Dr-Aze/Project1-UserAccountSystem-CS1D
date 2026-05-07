-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 07, 2026 at 04:05 AM
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

--
-- Indexes for dumped tables
--

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Unique user identifier', AUTO_INCREMENT=5;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
