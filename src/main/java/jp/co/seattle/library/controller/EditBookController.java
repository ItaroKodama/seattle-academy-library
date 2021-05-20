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
 * 編集コントローラー
 */
@Controller //APIの入り口
public class EditBookController {
    final static Logger logger = LoggerFactory.getLogger(EditBookController.class);

    @Autowired
    private BooksService booksService;

    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    private ValidationCheck validationCheck;

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

        model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
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
            @RequestParam("thumbnailUrl") String thumbnailUrl,
            @RequestParam("thumbnailName") String thumbnailName,
            @RequestParam("publish_date") String publishDate,
            @RequestParam("isbn") String isbn,
            @RequestParam("description") String description,
            @RequestParam("bookId") Integer bookId,
            Model model) {
        logger.info("Welcome delete! The client locale is {}.", locale);

        BookDetailsInfo bookInfo = new BookDetailsInfo();
        // パラメータで受け取った書籍情報をDtoに格納する。
        bookInfo.setTitle(title);
        bookInfo.setAuthor(author);
        bookInfo.setPublisher(publisher);
        bookInfo.setDescription(description);
        bookInfo.setBookId(bookId);
        
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
            return "editBook";
        }

        //サムネイル画像の変更がない場合の処理
        if (!thumbnailUrl.isEmpty()) {
            bookInfo.setThumbnailUrl(thumbnailUrl);
            bookInfo.setThumbnailName(thumbnailName);
        }
        // サムネイル画像をアップロード
        if (!thumbnailService.uploadThumbnail(file, bookInfo)) {
            model.addAttribute("bookDetailsInfo", bookInfo);
            return "editBook";
        }

        //古いサムネイルをminioから削除
        thumbnailService.deleteThumbnail(booksService.getBookInfo(bookId).getThumbnailName());

        // 書籍情報の編集
        booksService.updateBook(bookInfo);

        model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
        return "details";
    }
}
