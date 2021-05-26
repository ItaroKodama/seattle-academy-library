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

import jp.co.seattle.library.service.BooksService;

/**
 * Handles requests for the application home page.
 */
@Controller //APIの入り口
public class BorrowBookController {
    final static Logger logger = LoggerFactory.getLogger(AddBooksController.class);

    @Autowired
    private BooksService booksService;

    /**
     * 書籍の貸出
     * @param locale
     * @param bookId 貸出書籍ID
     * @param accountName 書籍を借りるユーザ名
     * @param model
     * @return 詳細画面に遷移
     */
    @Transactional
    @RequestMapping(value = "/rentBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String borrowBook(
            Locale locale,
            @RequestParam("bookId") Integer bookId,
            @RequestParam("accountName") String accountName,
            Model model) {
        logger.info("Welcome borrowBooks.java! The client locale is {}.", locale);

        booksService.borrowBook(bookId, accountName);

        model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
        return "details";
    }

    /**
     * 書籍の返却
     * @param locale
     * @param bookId 返却書籍ID
     * @param model
     * @return 詳細画面に遷移
     */
    @Transactional
    @RequestMapping(value = "/returnBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String returnBook(
            Locale locale,
            @RequestParam("bookId") Integer bookId,
            Model model) {
        logger.info("Welcome returnBooks.java! The client locale is {}.", locale);

        booksService.returnBook(bookId);

        model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
        return "details";
    }

    /**
     * 貸出履歴表示
     * @param locale
     * @param bookId 貸出履歴を表示したい書籍ID
     * @param model
     * @return 詳細画面に遷移
     */
    @Transactional
    @RequestMapping(value = "/borrowingHistory", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String showBorrowingHistory(Locale locale,
            @RequestParam("bookId") Integer bookId,
            Model model) {

        model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
        model.addAttribute("borrowingHistory", booksService.borrowingHistory(bookId));
        return "details";
    }

}
