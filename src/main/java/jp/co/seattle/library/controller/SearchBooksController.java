package jp.co.seattle.library.controller;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.seattle.library.dto.BookInfo;
import jp.co.seattle.library.service.BooksService;

@Controller //APIの入り口
public class SearchBooksController {
    final static Logger logger = LoggerFactory.getLogger(AddBooksController.class);

    @Autowired
    private BooksService booksService;

    /**
     * 書籍の検索
     * @param locale
     * @param isTitleSearchPartial 部分一致ならtrue
     * @param titleSearchWord
     * @param isAuthorSearchPartial 部分一致ならtrue
     * @param authorSearchWord
     * @param isPublisherSearchPartial 部分一致ならtrue
     * @param publisherSearchWord
     * @param isPublishDateSearchPartial 部分一致ならtrue
     * @param publishDateSearchWord
     * @param model
     * @return
     */
    @Transactional
    @RequestMapping(value = "/searchBooks", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String searchBooks(Locale locale,
            @RequestParam("titleMatching") boolean isTitleSearchPartial,
            @RequestParam("searchTitle") String titleSearchWord,
            @RequestParam("authorMatching") boolean isAuthorSearchPartial,
            @RequestParam("searchAuthor") String authorSearchWord,
            @RequestParam("publisherMatching") boolean isPublisherSearchPartial,
            @RequestParam("searchPublisher") String publisherSearchWord,
            @RequestParam("publishDateMatching") boolean isPublishDateSearchPartial,
            @RequestParam("searchPublishDate") String publishDateSearchWord,
            Model model) {
        logger.info("Welcome insertBooks.java! The client locale is {}.", locale);

        List<BookInfo> matchBooks = booksService.searchBooks(
                isTitleSearchPartial, titleSearchWord,
                isAuthorSearchPartial, authorSearchWord,
                isPublisherSearchPartial, publisherSearchWord,
                isPublishDateSearchPartial, publishDateSearchWord);
        if (CollectionUtils.isEmpty(matchBooks)) {
            model.addAttribute("noBook", "検索した書籍データがありません");
        } else {
            model.addAttribute("bookList", matchBooks);
            model.addAttribute("search_word",
                    "書籍名：" + titleSearchWord + "&emsp;&emsp;著者名：" + authorSearchWord + "&emsp;&emsp;出版社："
                            + publisherSearchWord + "&emsp;&emsp;出版日：" + publishDateSearchWord
                            + "&emsp;&emsp;で検索中");
        }
        return "home";
    }
}
