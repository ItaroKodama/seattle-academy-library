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

    @Transactional
    @RequestMapping(value = "/rentBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String borrowBook(
            Locale locale,
            @RequestParam("bookId") Integer bookId,
            Model model) {
        logger.info("Welcome borrowBooks.java! The client locale is {}.", locale);

        if (booksService.isBorrowing(bookId)) {
            model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
            model.addAttribute("borrowingMessage", "この書籍は貸し出し中です。");
            return "details";
        }

        booksService.borrowBook(bookId);

        model.addAttribute("bookDetailsInfo", booksService.getBookInfo(bookId));
        return "details";
    }

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

}
