package jp.co.seattle.library.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.dto.BookInfo;
import jp.co.seattle.library.dto.BorrowingHistory;
import jp.co.seattle.library.rowMapper.BookDetailsInfoRowMapper;
import jp.co.seattle.library.rowMapper.BookInfoRowMapper;
import jp.co.seattle.library.rowMapper.BorrowingHistoryRowMapper;

/**
 * 書籍サービス
 *  booksテーブルに関する処理を実装する
 */
@Service
public class BooksService {
    final static Logger logger = LoggerFactory.getLogger(BooksService.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 書籍リストを取得
     * @return 書籍リスト
     */
    public List<BookInfo> getBookList() {
        // 取得したい情報を取得するようにSQLを修正
        List<BookInfo> gotBookList = jdbcTemplate.query(
                "select id,title,author,publisher,publish_date,thumbnail_url from books order by TITLE asc",
                new BookInfoRowMapper());

        return gotBookList;
    }

    /**
     * 書籍IDに紐づく書籍詳細情報を取得する
     * @param bookId 書籍ID
     * @return 書籍情報
     */
    public BookDetailsInfo getBookInfo(int bookId) {
        // JSPに渡すデータを設定
        String sql = "SELECT * FROM books where id = " + bookId;
        BookDetailsInfo bookDetailsInfo = jdbcTemplate.queryForObject(sql, new BookDetailsInfoRowMapper());
        // 書籍の貸出状況を設定
        if (isBorrowing(bookId)) {
            bookDetailsInfo.setBorrowing(true);
            bookDetailsInfo.setBorrowingUserName(whoBorrowing(bookId));
        } else {
            bookDetailsInfo.setBorrowing(false);
        }
        return bookDetailsInfo;
    }

    /**
     * 書籍を登録
     * @param bookInfo 登録する書籍の情報
     */
    public void registBook(BookDetailsInfo bookInfo) {
        String sql = "INSERT INTO books (title, author,publisher,publish_date,isbn,description,thumbnail_name,thumbnail_url,reg_date,upd_date) VALUES ('"
                + bookInfo.getTitle() + "','" + bookInfo.getAuthor() + "','" + bookInfo.getPublisher() + "','"
                + bookInfo.getPublish_date() + "','"
                + bookInfo.getIsbn() + "','"
                + bookInfo.getDescription() + "','"
                + bookInfo.getThumbnailName() + "','"
                + bookInfo.getThumbnailUrl() + "',"
                + "sysdate(),"
                + "sysdate())";

        jdbcTemplate.update(sql);
    }

    /**
     * 書籍を削除
     * @param bookID 削除対象の書籍ID
     */
    public void deleteBook(int bookId) {
        jdbcTemplate.update("delete from borrowing where book_id = " + bookId);
        jdbcTemplate.update("delete from books where ID = " + bookId);
    }

    /**
     * 追加した書籍のIDを取得
     * @return booksテーブルのIDの最大値
     */
    public int getBookId() {
        return jdbcTemplate.queryForObject("SELECT MAX(ID) FROM books", Integer.class);
    }

    /**
     * 書籍情報の更新
     * @param bookInfo 更新したい書籍の情報
     */
    public void updateBook(BookDetailsInfo bookInfo) {
        String sql = "update books set title = '"
                + bookInfo.getTitle() + "', author = '"
                + bookInfo.getAuthor() + "', publisher = '"
                + bookInfo.getPublisher() + "', publish_date = '"
                + bookInfo.getPublish_date() + "', isbn = '"
                + bookInfo.getIsbn() + "', description = '"
                + bookInfo.getDescription() + "', thumbnail_url = '"
                + bookInfo.getThumbnailUrl() + "', thumbnail_name = '"
                + bookInfo.getThumbnailName() + "', upd_date = sysdate() where id = " + bookInfo.getBookId();

        jdbcTemplate.update(sql);
    }

    /**
    * 書籍を部分一致または完全一致で検索
    * @param isTitleSearchPartial 部分一致ならtrue
    * @param titleSearchWord
    * @param isAuthorSearchPartial 部分一致ならtrue
    * @param authorSearchWord
    * @param isPublisherSearchPartial 部分一致ならtrue
    * @param publisherSearchWord
    * @param isPublishDateSearchPartial 部分一致ならtrue
    * @param publishDateSearchWord
    * @return 検索で取得した書籍情報
    */
    public List<BookInfo> searchBooks(boolean isTitleSearchPartial, String titleSearchWord,
            boolean isAuthorSearchPartial,
            String authorSearchWord, boolean isPublisherSearchPartial, String publisherSearchWord,
            boolean isPublishDateSearchPartial, String publishDateSearchWord) {

        //部分一致の場合は'='をlikeにし、検索ワードの前後を'%'で囲む
        String isTitlePartial = "";
        String isTitleLike = "=";
        String isAuthorPartial = "";
        String isAuthorLike = "=";
        String isPublisherPartial = "";
        String isPublisherLike = "=";
        String isPublishDatePartial = "";
        String isPublishDateLike = "=";
        if (isTitleSearchPartial || titleSearchWord.isEmpty()) {
            isTitlePartial = "%";
            isTitleLike = "like";
        }
        if (isAuthorSearchPartial || authorSearchWord.isEmpty()) {
            isAuthorPartial = "%";
            isAuthorLike = "like";
        }
        if (isPublisherSearchPartial || publisherSearchWord.isEmpty()) {
            isPublisherPartial = "%";
            isPublisherLike = "like";
        }
        if (isPublishDateSearchPartial || publishDateSearchWord.isEmpty()) {
            isPublishDatePartial = "%";
            isPublishDateLike = "like";
        }

        String sql = "select id,title,author,publisher,publish_date,thumbnail_url from books where "
                + "title " + isTitleLike + " '"
                + isTitlePartial + titleSearchWord + isTitlePartial
                + "' and author " + isAuthorLike + " '"
                + isAuthorPartial + authorSearchWord + isAuthorPartial
                + "' and publisher " + isPublisherLike + " '"
                + isPublisherPartial + publisherSearchWord + isPublisherPartial
                + "' and publish_date " + isPublishDateLike + " '"
                + isPublishDatePartial + publishDateSearchWord + isPublishDatePartial
                + "' order by TITLE asc";

        return jdbcTemplate.query(sql, new BookInfoRowMapper());
    }

    /**
     * 書籍の貸出状況取得
     * @param bookId 書籍ID
     * @return 貸出中ならtrue
     */
    public boolean isBorrowing(int bookId) {
        try {
            //                    ストアドファンクション
            //                    CREATE DEFINER=`root`@`%` FUNCTION `isBorrowing`(bookId bigint(20)) RETURNS bit(1)
            //                            BEGIN
            //                                select max(id) from borrowing where book_id = bookId into @tmp;
            //                                select is_borrowing from borrowing where id = @tmp into @result;
            //                                RETURN(@result);
            //                            END;
            Integer isBorrowing = jdbcTemplate.queryForObject("select isBorrowing(" + bookId + ")", Integer.class);
            if (isBorrowing == null || isBorrowing == 0) {
                return false;
            }
            return true;
        } catch (IncorrectResultSizeDataAccessException e) {
            return false;
        }
    }

    /**
     * 書籍を貸出テーブルに追加
     * @param bookId 貸出書籍のID
     * @param accountName 書籍を借りたユーザ名
     */
    public void borrowBook(int bookId, String accountName) {
        String borrowDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
        String sql = "insert into borrowing (book_id, borrow_date, account_name) "
                + "values (" + bookId + ", '" + borrowDate + "', '" + accountName + "')";

        jdbcTemplate.update(sql);
    }

    /**
     * 書籍を貸出テーブルから論理削除
     * @param bookId 返却書籍のID
     */
    public void returnBook(int bookId) {
        String returnDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
        //                    ストアドプロシージャ
        //                    CREATE DEFINER=`root`@`%` PROCEDURE `returnBook`(in bookId bigint(20), returnDate varchar(20))
        //                            begin
        //                                select max(id) from borrowing where book_id = bookId into @tmp;
        //                                update borrowing set return_date = returnDate, is_borrowing = 0 where id = @tmp;
        //                            end;
        jdbcTemplate.update("call returnBook(" + bookId + ",'" + returnDate + "')");
    }

    /**
     * 書籍の貸出履歴の取得
     * @param bookId 貸出履歴を取得したい書籍のID
     * @return 貸出日時と返却日時、借りたユーザ名のリスト
     */
    public List<BorrowingHistory> borrowingHistory(int bookId) {
        String sql = "select borrow_date, return_date, account_name from borrowing where book_id = " + bookId;

        return jdbcTemplate.query(sql, new BorrowingHistoryRowMapper());

    }

    /**
     * 書籍を借りているユーザ名を取得
     * @param bookId 貸出中の書籍
     * @return 書籍を借りているユーザ名
     */
    public String whoBorrowing(int bookId) {
        String sql = "select account_name from borrowing where is_borrowing = 1 and book_id = " + bookId;
        return jdbcTemplate.queryForObject(sql, String.class);
    }
}
