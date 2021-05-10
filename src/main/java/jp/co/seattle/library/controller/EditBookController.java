package jp.co.seattle.library.controller;


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
        String[] errorMsg = validationCheck.validationCheck(publishDate, isbn);
        boolean flag = false;
        if (errorMsg[0] != null) {
            model.addAttribute("notDateError", errorMsg[0]);
            flag = true;
        } else {
            bookInfo.setPublish_date(publishDate);
        }
        if (errorMsg[1] != null) {
            model.addAttribute("notISBNError", errorMsg[1]);
            flag = true;
        } else {
            bookInfo.setIsbn(isbn);
        }
        if (flag) {
            model.addAttribute("bookDetailsInfo", bookInfo);
            return "editBook";
        }

        // サムネイル画像をアップロード
        if (!thumbnailService.uploadThumbnail(file, bookInfo)) {
            model.addAttribute("bookDetailsInfo", bookInfo);
            return "editBook";
        } else {
            bookInfo.setThumbnailUrl(booksService.getBookInfo(bookId).getThumbnailUrl());
            bookInfo.setThumbnailName(booksService.getBookInfo(bookId).getThumbnailName());
        }

        // 書籍情報の編集
        booksService.updateBook(bookInfo);

        model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
        return "details";
    }
}
