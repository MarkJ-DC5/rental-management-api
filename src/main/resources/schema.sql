--DROP DATABASE IF EXISTS rental_management;
--CREATE DATABASE rental_management;
--USE rental_management;

DROP TABLE IF EXISTS payment_transactions;
DROP TABLE IF EXISTS tenant;
DROP TABLE IF EXISTS room;
DROP TABLE IF EXISTS building;

CREATE TABLE building (
	building_id INT AUTO_INCREMENT PRIMARY KEY,
	building_name VARCHAR(100) NOT NULL,
	street VARCHAR(255) NOT NULL,
	barangay VARCHAR(100) NOT NULL,
	city VARCHAR(100) NOT NULL,
	province VARCHAR(100) NOT NULL
);

CREATE TABLE room (
	room_id INT AUTO_INCREMENT PRIMARY KEY,
	building_id INT NOT NULL,
	room_name VARCHAR(100) NOT NULL,
	room_type ENUM('RESIDENTIAL', 'COMMERCIAL', 'OTHERS') NOT NULL,
	rent INT NOT NULL,
	FOREIGN KEY (building_id) REFERENCES building(building_id)
);

CREATE TABLE tenant (
	tenant_id INT AUTO_INCREMENT PRIMARY KEY,
	room_id INT NOT NULL,
	is_primary BOOL NOT NULL,
	login_id VARCHAR(100),
	password VARCHAR(100),
	first_name VARCHAR(100) NOT NULL,
	last_name VARCHAR(100) NOT NULL,
	middle_name VARCHAR(100),
	birth_date DATE NOT NULL,
	gender ENUM ('M', 'F') NOT NULL,
	contact_number VARCHAR(100) NOT NULL,
	date_moved_in DATE NOT NULL,
	date_moved_out DATE,
	FOREIGN KEY (room_id) REFERENCES room(room_id)
);

CREATE TABLE payment_transactions (
	transaction_id INT AUTO_INCREMENT PRIMARY KEY,
	room_id INT NOT NULL,
	tenant_id INT NOT NULL,
	transaction_type ENUM('RENT', 'UTILS', 'DOWNPAYMENT', 'OTHERS') NOT NULL,
	amount INT NOT NULL,
	for_month_of DATE NOT NULL,
	transaction_date DATE NOT NULL,
	notes VARCHAR(255),
	created_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 	FOREIGN KEY (room_id) REFERENCES room(room_id),
	FOREIGN KEY (tenant_id) REFERENCES tenant(tenant_id)
);

CREATE TABLE IF NOT EXISTS user (
	user_id INT AUTO_INCREMENT PRIMARY KEY,
	username VARCHAR(50) NOT NULL UNIQUE,
	password_hash VARCHAR(255) NOT NULL,
	role ENUM ('ADMIN', 'PROP_MNGR') NOT NULL,
	first_name VARCHAR(100) NOT NULL,
	last_name VARCHAR(100) NOT NULL,
    created_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
