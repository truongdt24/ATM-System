package DAO;

import DB.DatabaseConnection;
import DB.HashUtil;
import model.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO {
    public Account findByCardNumber(String cardNumber) throws SQLException{
        String sql = "SELECT * FROM account WHERE card_number = ?";
        try(PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql)){
            ps.setString(1, cardNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return new Account(
                        rs.getInt("id"),
                        rs.getString("card_number"),
                        rs.getString("holder_name"),
                        rs.getBigDecimal("balance")
                );
            }
        }
        return null;
    }

    public void updateBalance(int accountId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        try(PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql)){
            ps.setBigDecimal(1, newBalance);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
    }


    //login
    public Account login(String cardNumber, String pin) throws SQLException{
        String pinHash = HashUtil.hashPin(pin);
        String sql = "SELECT * FROM account WHERE card_number = ? AND pin_hash = ? AND is_active = true";

        try(PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql)){
            ps.setString(1, cardNumber);
            ps.setString(2, pinHash);

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return new Account(
                        rs.getInt("id"),
                        rs.getString("card_number"),
                        rs.getString("holder_name"),
                        rs.getBigDecimal("balance")
                );
            }
        }
        return null;
    }
    //thay đổi giá trị khi tài khoản A chuyển tiền vào tài khoản B
    public BigDecimal getBalance(int accountId) throws SQLException {
        String sql = "SELECT balance FROM account WHERE id = ?";

        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql)){
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                return rs.getBigDecimal("balance");
            }
        }
        return BigDecimal.ZERO;
    }
    // nạp tiền
    public void deposit(int accountId, BigDecimal amount)throws SQLException{
        String sql = "UPDATE account SET balance = balance + ? WHERE id = ?";

        try(PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql)){
            ps.setBigDecimal(1, amount);
            ps.setInt(2,accountId);
            ps.executeUpdate();
        }
    }
    // rút tiền
    public boolean withdraw(int accountId, BigDecimal amount)throws SQLException{
        BigDecimal currentBalance = getBalance(accountId);

        if(currentBalance.compareTo(amount) <= 0){
            return false;
        }
        String sql = "UPDATE account SET balance = balance - ? WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql)){
            ps.setBigDecimal(1, amount);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
        return true;

    }

    public boolean transfer(int fromId, String toCardNumber, BigDecimal amount) throws SQLException{
        // kiểm tra
        BigDecimal currentBalance = getBalance(fromId);
        if (currentBalance.compareTo(amount) < 0){
            return false;
        }

        // kiểm tra người nhận
        Account receiver = findByCardNumber(toCardNumber);
        if (receiver == null){
            return false;
        }

        Connection conn = DatabaseConnection.get();
        try {
            // tạm tắt auto commit
            conn.setAutoCommit(false);
            // trừ tiền người gửi
            String deduct = "UPDATE account SET balance = balance - ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deduct)){
                ps.setBigDecimal(1, amount);
                ps.setInt(2, fromId);
                ps.executeUpdate();

            }
            // cộng tiền
            String add = "UPDATE account SET balance = balance + ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(add)){
                ps.setBigDecimal(1, amount);
                ps.setInt(2, receiver.getId());
                ps.executeUpdate();
            }

            // cả 2 chạy thành công thì lưu lại
            conn.commit();
            TransactionDAO transactionDAO = new TransactionDAO();
            transactionDAO.save(fromId, "Chuyển đi / Transfer out: -", amount);
            transactionDAO.save(receiver.getId(), " Chuyển vào / Transfer in: +", amount);
            return true;



        }catch (SQLException e){
            conn.rollback();
            throw e;
        }finally {
            // mở lại auto commit
            conn.setAutoCommit(true);
        }
    }

}
