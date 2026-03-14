# ATM System
- A console-based ATM simulation project built with Java and PostgreSQL.
- The objective of building logical thinking for banking system development

# What i learned
- DAO pattern for separating database logic
- JDBC PreparedStatement to prevent SQL injection
- SHA-256 password hashing for security
- Database transactions (commit/rollback) for safe transfers
- BigDecimal for precise financial calculations

# Features
- Secure PIN login with SHA-256 hashing
- Check account balance
- Deposit money
- Withdraw with balance validation
- Transfer between accounts (using DB transactions)
- Transaction history

# Technology usage
- Java 17
- PostgreSQL
- JDBC
- Maven
- Intelij IDE
- DataGrip

# Database
CREATE DATABASE atm_db;
CREATE USER atm_user WITH PASSWORD 'atm1234';
GRANT ALL PRIVILEGES ON DATABASE atm_db TO atm_user;
Test account 
1234567890123456  1234  5,000,000 
9999888877776666  5678  2,000,000 
