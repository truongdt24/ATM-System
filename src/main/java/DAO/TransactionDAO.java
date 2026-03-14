package DAO;

import DB.DatabaseConnection;
import model.Transaction;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    // lưu lịch sử giao dịch
    public void save(int accountId, String type, BigDecimal amount) throws SQLException{
        String sql = "INSERT INTO transaction (account_id, type, amount) VALUES (?, ?, ?)";
        try(PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setString(2,type);
            ps.setBigDecimal(3, amount);
            ps.executeUpdate();

        }
    }

    // lấy toàn bộ lịch sử giao dịch của tk
    public List<Transaction> getHistory(int accountId) throws SQLException{
        String sql = "SELECT * FROM transaction WHERE account_id = ? ORDER BY created_at DESC";
        List<Transaction> list = new ArrayList<>();

        try(PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql)){
            ps.setInt(1,accountId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                list.add(new Transaction(
                        rs.getInt("id"),
                        rs.getInt("account_id"),
                        rs.getString("type"),
                        rs.getBigDecimal("amount"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
        }
        return list;
    }
}
