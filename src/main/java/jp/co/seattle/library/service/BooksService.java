package jp.co.seattle.library.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.dto.BookInfo;
import jp.co.seattle.library.rowMapper.BookDetailsInfoRowMapper;
import jp.co.seattle.library.rowMapper.BookInfoRowMapper;

/**
 * 書籍サービス
 * 
 *  booksテーブルに関する処理を実装する
 */
@Service
public class BooksService {
    final static Logger logger = LoggerFactory.getLogger(BooksService.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 書籍リストを取得する
     *
     * @return 書籍リスト
     */
    public List<BookInfo> getBookList() {

        // TODO 取得したい情報を取得するようにSQLを修正
        List<BookInfo> gotBookList = jdbcTemplate.query(
                "select id,title,author,publisher,publish_date,thumbnail_url from books order by TITLE asc",
                new BookInfoRowMapper());

        return gotBookList;
    }

    /**
     * 書籍IDに紐づく書籍詳細情報を取得する
     *
     * @param bookId 書籍ID
     * @return 書籍情報
     */
    public BookDetailsInfo getBookInfo(int bookId) {

        // JSPに渡すデータを設定する
        String sql = "SELECT * FROM books where id ="
                + bookId;

        BookDetailsInfo bookDetailsInfo = jdbcTemplate.queryForObject(sql, new BookDetailsInfoRowMapper());

        bookDetailsInfo.setBorrowing(isBorrowing(bookId));

        return bookDetailsInfo;
    }



    /**
     * 書籍を登録する
     *
     * @param bookInfo 書籍情報
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
        String sql = "delete from books where ID = " + bookId;
        jdbcTemplate.update(sql);
    }

    /**
     * 追加した書籍のIDを取得
     * @return booksテーブルのIDの最大値
     */
    public int getBookId(){
        String sql = "SELECT MAX(ID) FROM books";

        return jdbcTemplate.queryForObject(sql, Integer.class);
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
    public List<BookInfo> searchBooks(boolean isTitleSearchPartial, String titleSearchWord, boolean isAuthorSearchPartial,
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
     * 書籍が貸し出し中の場合true
     * @param bookId
     * @return 
     */
    public boolean isBorrowing(int bookId) {
        String sql = "select book_id from borrowing where book_id = " + bookId;
        try {
            jdbcTemplate.queryForObject(sql, Integer.class);
            return true;
        } catch (IncorrectResultSizeDataAccessException e) {
            return false;
        }
    }

    /**
     * 書籍を貸し出しテーブルに追加
     * @param bookId 貸し出し書籍のID
     */
    public void borrowBook(int bookId) {
        String sql = "insert into borrowing (book_id) values (" + bookId + ")";
        jdbcTemplate.update(sql);
    }

    /**
     * 書籍を貸し出しテーブルから削除
     * @param bookId 返却書籍のID
     */
    public void returnBook(int bookId) {
        jdbcTemplate.update("delete from borrowing where book_id =" + bookId);
    }
}
