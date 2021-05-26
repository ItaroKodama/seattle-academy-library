package jp.co.seattle.library.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import jp.co.seattle.library.dto.BorrowingHistory;

@Configuration
public class BorrowingHistoryRowMapper implements RowMapper<BorrowingHistory> {

    @Override
    public BorrowingHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Query結果（ResultSet rs）を、オブジェクトに格納する実装
        BorrowingHistory borrowingHistory = new BorrowingHistory();

        borrowingHistory.setBorrowingDate(rs.getString("borrow_date"));
        borrowingHistory.setReturnDate(rs.getString("return_date"));
        borrowingHistory.setAccountName(rs.getString("account_name"));
        return borrowingHistory;
    }
}