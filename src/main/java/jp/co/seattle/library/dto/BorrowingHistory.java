package jp.co.seattle.library.dto;

import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 書籍の貸出履歴情報格納DTO
 */
@Configuration
@Data
public class BorrowingHistory {
    private String borrowingDate;

    private String returnDate;

}

