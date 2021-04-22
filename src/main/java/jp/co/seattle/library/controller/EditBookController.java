package jp.co.seattle.library.controller;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.ThumbnailService;

/**
 * 編集コントローラー
 */
@Controller //APIの入り口
public class EditBookController {
    final static Logger logger = LoggerFactory.getLogger(EditBookController.class);

    @Autowired
    private BooksService booksService;

    @Autowired
    private ThumbnailService thumbnailService;

    /**
     * 詳細画面で編集ボタンを押下した時に書籍編集画面に遷移する
     * 
     * @param locale ローケル情報
     * @param bookId 書籍
     * @param model モデル情報
     * @return 遷移画面名
     */
    @Transactional
    @RequestMapping(value = "/editBook", method = RequestMethod.POST)
    public String editBook(Locale locale,
            @RequestParam("bookId") Integer bookId,
            Model model) {
        logger.info("Welcome delete! The client locale is {}.", locale);

        model.addAttribute("bookInfo", booksService.getBookInfo(bookId));
        return "editBook";
    }

    /**
     * 対象書籍を更新する
     *
     * @param locale ロケール情報
     * @param bookId 書籍ID
     * @param model モデル情報
     * @return 遷移先画面名
     */
    @Transactional
    @RequestMapping(value = "/updateBook", method = RequestMethod.POST)
    public String updateBook(Locale locale,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("publisher") String publisher,
            @RequestParam("thumbnail") MultipartFile file,
            @RequestParam("publish_date") String publishDate,
            @RequestParam("isbn") String isbn,
            @RequestParam("description") String description,
            @RequestParam("bookId") Integer bookId,
            Model model) {
        logger.info("Welcome delete! The client locale is {}.", locale);

        BookDetailsInfo bookInfo = new BookDetailsInfo();
        
        //出版日とISBNのバリデーションチェック
        boolean flag = false;
        if (!(publishDate.matches("[0-9]{8}"))) {
            model.addAttribute("notDateError", "出版日はYYYYMMDDの形式で入力してください");
            flag = true;
        } else {
            try {
                DateFormat df = new SimpleDateFormat("yyyyMMdd");
                df.setLenient(false);
                df.parse(publishDate); // df.parseでParseExceptionがThrowされる                
            } catch (ParseException p) {
                model.addAttribute("notDateError", "出版日はYYYYMMDDの形式で入力してください");
                flag = true;
                p.printStackTrace();
            }
        }
        if (!(isbn.matches("([0-9]{10}|[0-9]{13})?"))) {
            model.addAttribute("notISBNError", "ISBNは10桁もしくは13桁の数字で入力してください");
            flag = true;
        }
        if (flag) {
            return "editBook";
        }
        
        // パラメータで受け取った書籍情報をDtoに格納する。
        bookInfo.setTitle(title);
        bookInfo.setAuthor(author);
        bookInfo.setPublisher(publisher);
        bookInfo.setPublish_date(publishDate);
        bookInfo.setIsbn(isbn);
        bookInfo.setDescription(description);
        bookInfo.setBookId(bookId);

        // クライアントのファイルシステムにある元のファイル名を設定する
        String thumbnail = file.getOriginalFilename();

        if (!file.isEmpty()) {
            try {
                // サムネイル画像をアップロード
                String fileName = thumbnailService.uploadThumbnail(thumbnail, file);
                // URLを取得
                String thumbnailUrl = thumbnailService.getURL(fileName);

                bookInfo.setThumbnailName(fileName);
                bookInfo.setThumbnailUrl(thumbnailUrl);

            } catch (Exception e) {

                // 異常終了時の処理
                logger.error("サムネイルアップロードでエラー発生", e);
                model.addAttribute("bookInfo", bookInfo);
                return "editBook";
            }
        }

        // 書籍情報の編集
        booksService.updateBook(bookInfo);

        model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
        return "details";

    }

}
