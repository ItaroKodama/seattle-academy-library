package jp.co.seattle.library.controller;

import java.util.List;
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
import jp.co.seattle.library.service.ValidationCheck;

/**
 * Handles requests for the application home page.
 */
@Controller //APIの入り口
public class AddBooksController {
    final static Logger logger = LoggerFactory.getLogger(AddBooksController.class);

    @Autowired
    private BooksService booksService;

    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    private ValidationCheck validationCheck;

    @RequestMapping(value = "/addBook", method = RequestMethod.GET) //value＝actionで指定したパラメータ
    //RequestParamでname属性を取得
    public String login(Model model) {
        return "addBook";
    }

    /**
     * 書籍情報を登録する
     * @param locale ロケール情報
     * @param title 書籍名
     * @param author 著者名
     * @param publisher 出版社
     * @param file サムネイルファイル
     * @param publishDate 出版日
     * @param isbn ISBN
     * @param description 説明文
     * @param model モデル
     * @return 遷移先画面
     */
    @Transactional
    @RequestMapping(value = "/insertBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String insertBook(Locale locale,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("publisher") String publisher,
            @RequestParam("thumbnail") MultipartFile file,
            @RequestParam("publish_date") String publishDate,
            @RequestParam("isbn") String isbn,
            @RequestParam("description") String description,
            Model model) {
        logger.info("Welcome insertBooks.java! The client locale is {}.", locale);

        // パラメータで受け取った書籍情報をDtoに格納する。   
        BookDetailsInfo bookInfo = new BookDetailsInfo();
      
        //出版日とISBNのバリデーションチェック
        List<String> errorMsg = validationCheck.validationCheck(publishDate, isbn, title, author, publisher,
                description);
        if (!errorMsg.get(0).isEmpty()) {
            model.addAttribute("notDateError", errorMsg.get(0));
        } else {
            bookInfo.setPublish_date(publishDate);
        }
        if (!errorMsg.get(1).isEmpty()) {
            model.addAttribute("notISBNError", errorMsg.get(1));
        } else {
            bookInfo.setIsbn(isbn);
        }
        if (!errorMsg.get(2).isEmpty()) {
            model.addAttribute("titleError", errorMsg.get(2));
        } else {
            bookInfo.setTitle(title);
        }
        if (!errorMsg.get(3).isEmpty()) {
            model.addAttribute("authorError", errorMsg.get(3));
        } else {
            bookInfo.setAuthor(author);
        }
        if (!errorMsg.get(4).isEmpty()) {
            model.addAttribute("publisherError", errorMsg.get(4));
        } else {
            bookInfo.setPublisher(publisher);
        }
        if (!errorMsg.get(5).isEmpty()) {
            model.addAttribute("descriptionError", errorMsg.get(5));
        } else {
            bookInfo.setDescription(description);
        }
        if (!errorMsg.get(0).isEmpty() || !errorMsg.get(1).isEmpty() || !errorMsg.get(2).isEmpty()
                || !errorMsg.get(3).isEmpty() || !errorMsg.get(4).isEmpty() || !errorMsg.get(5).isEmpty()) {
            model.addAttribute("bookDetailsInfo", bookInfo);
            return "addBook";
        }

        // サムネイル画像をアップロード
        if (!thumbnailService.uploadThumbnail(file, bookInfo)) {
            model.addAttribute("bookDetailsInfo", bookInfo);
            return "addBook";
        }

        // 書籍情報を新規登録する
        booksService.registBook(bookInfo);

        //  詳細画面に遷移する
        model.addAttribute("bookDetailsInfo", booksService.getBookInfo(booksService.getBookId()));
        return "details";
    }
}
