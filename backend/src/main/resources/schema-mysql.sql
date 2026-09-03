-- MySQL Schema for JML Access Reconciliation Engine

CREATE DATABASE IF NOT EXISTS jml_reconciliation;
USE jml_reconciliation;

CREATE TABLE IF NOT EXISTS employees (
    employee_id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    current_role VARCHAR(50) NOT NULL,
    previous_role VARCHAR(50),
    employment_status VARCHAR(20) NOT NULL,
    join_date DATETIME NOT NULL,
    last_role_change_date DATETIME,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS role_policies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    application_name VARCHAR(50) NOT NULL,
    expected_permission VARCHAR(20) NOT NULL,
    is_required BOOLEAN DEFAULT TRUE,
    UNIQUE KEY uk_role_app (role_name, application_name)
);

CREATE TABLE IF NOT EXISTS directory_groups (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    group_name VARCHAR(100) NOT NULL,
    application_name VARCHAR(50) NOT NULL,
    access_level VARCHAR(20) NOT NULL,
    last_updated DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS entitlements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    application_name VARCHAR(50) NOT NULL,
    permission_level VARCHAR(20) NOT NULL,
    granted_date DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    last_updated DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS approval_records (
    approval_id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    application_name VARCHAR(50) NOT NULL,
    permission_level VARCHAR(20) NOT NULL,
    requested_by VARCHAR(50) NOT NULL,
    approved_by VARCHAR(50),
    approval_date DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    reason TEXT
);

CREATE TABLE IF NOT EXISTS reconciliation_issues (
    issue_id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    employee_name VARCHAR(100) NOT NULL,
    current_role VARCHAR(50) NOT NULL,
    issue_type VARCHAR(50) NOT NULL,
    application_name VARCHAR(50) NOT NULL,
    expected_access VARCHAR(20),
    actual_access VARCHAR(20),
    risk_level VARCHAR(20) NOT NULL,
    confidence_score DOUBLE NOT NULL,
    engine_type VARCHAR(20) NOT NULL, -- PROTOTYPE or BASELINE
    recommended_action VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,
    detected_at DATETIME NOT NULL,
    remediated_at DATETIME,
    remediation_time_minutes INT,
    target_time_minutes INT,
    met_target BOOLEAN
);

CREATE TABLE IF NOT EXISTS remediation_actions (
    remediation_id VARCHAR(50) PRIMARY KEY,
    issue_id VARCHAR(50) NOT NULL,
    user_id VARCHAR(50) NOT NULL,
    application_name VARCHAR(50) NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    previous_state VARCHAR(100) NOT NULL,
    proposed_state VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL,
    requested_by VARCHAR(50) NOT NULL,
    approved_by VARCHAR(50),
    reviewer_comment TEXT,
    created_at DATETIME NOT NULL,
    executed_at DATETIME,
    rolled_back_at DATETIME,
    rollback_reason TEXT
);

CREATE TABLE IF NOT EXISTS audit_events (
    audit_id VARCHAR(50) PRIMARY KEY,
    timestamp DATETIME NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    user_id VARCHAR(50) NOT NULL,
    actor VARCHAR(50) NOT NULL,
    action VARCHAR(100) NOT NULL,
    previous_state TEXT,
    new_state TEXT,
    reason TEXT,
    related_issue_id VARCHAR(50),
    related_approval_id VARCHAR(50),
    data_sources_used VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS data_source_health (
    source_name VARCHAR(50) PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    last_updated DATETIME NOT NULL,
    freshness VARCHAR(20) NOT NULL,
    records_count INT DEFAULT 0
);
