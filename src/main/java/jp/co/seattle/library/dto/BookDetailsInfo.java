package jp.co.seattle.library.dto;

import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 書籍詳細情報格納DTO
 *
 */
@Configuration
@Data
public class BookDetailsInfo {

    private int bookId;

    private String title;

    private String author;

    private String publisher;

    private String publish_date;

    private String isbn;

    private String description;

    private String thumbnailUrl;

    private String thumbnailName;

    private boolean isBorrowing;

    private String borrowingUserName;


    public BookDetailsInfo() {

    }

    public BookDetailsInfo(int bookId, String title, String author, String publisher, String publish_date,
            String isbn, String description, String thumbnailUrl, String thumbnailName, boolean isBorrowing) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publish_date = publish_date;
        this.isbn = isbn;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.thumbnailName = thumbnailName;
        this.isBorrowing = isBorrowing;
    }

}