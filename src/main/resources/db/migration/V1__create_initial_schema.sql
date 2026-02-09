-- V1: Create initial schema for pension fund system

-- Participants table
CREATE TABLE participants (
    participant_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    registration_date DATE NOT NULL,
    employer_name VARCHAR(255) NOT NULL,
    current_salary DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_birth_date (birth_date),
    INDEX idx_registration_date (registration_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Contributions table
CREATE TABLE contributions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    participant_id VARCHAR(50) NOT NULL,
    month DATE NOT NULL,
    employee_contribution DECIMAL(15, 2) NOT NULL,
    employer_contribution DECIMAL(15, 2) NOT NULL,
    salary_base DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (participant_id) REFERENCES participants(participant_id) ON DELETE CASCADE,
    UNIQUE KEY uk_participant_month (participant_id, month),
    INDEX idx_participant_id (participant_id),
    INDEX idx_month (month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fund return rates table
CREATE TABLE fund_return_rates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    year INT NOT NULL UNIQUE,
    return_rate DECIMAL(10, 6) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_year (year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Pension rules table (single row configuration)
CREATE TABLE pension_rules (
    id INT PRIMARY KEY DEFAULT 1,
    normal_retirement_age INT NOT NULL DEFAULT 58,
    early_retirement_age INT NOT NULL DEFAULT 50,
    minimum_years_of_service INT NOT NULL DEFAULT 5,
    early_retirement_penalty_rate DECIMAL(5, 4) NOT NULL DEFAULT 0.05,
    monthly_benefit_divisor INT NOT NULL DEFAULT 180,
    effective_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (id = 1) -- Ensure only one row
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
