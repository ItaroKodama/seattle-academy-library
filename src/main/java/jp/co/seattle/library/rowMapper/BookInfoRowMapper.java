package jp.co.seattle.library.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import jp.co.seattle.library.dto.BookInfo;

@Configuration
public class BookInfoRowMapper implements RowMapper<BookInfo> {

    @Override
    public BookInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        // Query結果（ResultSet rs）を、オブジェクトに格納する実装
        BookInfo bookInfo = new BookInfo();

        // bookInfoの項目と、取得した結果(rs)のカラムをマッピングする
        bookInfo.setBookId(rs.getInt("a.id"));
        bookInfo.setTitle(rs.getString("a.title"));
        bookInfo.setAuthor(rs.getString("a.author"));
        bookInfo.setPublisher(rs.getString("a.publisher"));
        bookInfo.setPublishDate(rs.getString("a.publish_date"));
        bookInfo.setThumbnail(rs.getString("a.thumbnail_url"));
        bookInfo.setBorrowing(rs.getBoolean("b.is_borrowing"));
        return bookInfo;
    }

}