package jp.co.seattle.library.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;

import jp.co.seattle.library.dto.BookDetailsInfo;

/**
 * @author user
 * 書籍情報のバリデーションチェック
 */
@Controller
public class ValidationCheck {

    /**
     * 書籍登録、編集時におけるバリデーションチェック
     * @param publishDate チェックする出版日
     * @param isbn チェックするISBN
     * @param title チェックする書籍名
     * @param author チェックする著者名
     * @param publisher チェックする出版社名
     * @param description チェックする説明文
     * @return エラーメッセージのリスト（出版日、ISBN、書籍名、著者名、出版社名、説明文の順）
     */
    public List<String> validationCheck(String publishDate, String isbn, String title, String author, String publisher,
            String description) {
        List<String> errorMsg = new ArrayList<String>();
        errorMsg.add(0, "");
        errorMsg.add(1, "");
        errorMsg.add(2, "");
        errorMsg.add(3, "");
        errorMsg.add(4, "");
        errorMsg.add(5, "");

        if (!(publishDate.matches("[0-9]{8}"))) {
            errorMsg.add(0, "出版日はYYYYMMDDの形式で入力してください");
        } else {
            try {
                DateFormat df = new SimpleDateFormat("yyyyMMdd");
                df.setLenient(false);
                df.parse(publishDate); // df.parseでParseExceptionがThrowされる                
            } catch (ParseException p) {
                errorMsg.add(0, "出版日はYYYYMMDDの形式で入力してください");
                p.printStackTrace();
            }
        }
        if (!(isbn.matches("([0-9]{10}|[0-9]{13})?"))) {
            errorMsg.add(1, "ISBNは10桁もしくは13桁の数字で入力してください");
        }

        if (255 < title.length()) {
            errorMsg.add(2, "書籍名は255文字以内で入力してください");
        }
        if (255 < author.length()) {
            errorMsg.add(3, "著者名は255文字以内で入力してください");
        }
        if (255 < publisher.length()) {
            errorMsg.add(4, "出版社は255文字以内で入力してください");
        }
        if (255 < description.length()) {
            errorMsg.add(5, "説明文は255文字以内で入力してください");
        }

        return errorMsg;
    }

    /**
     * 書籍一括登録時における書籍情報の必須項目とバリデーションのチェック
     * @param bookInfo チェックする書籍情報
     * @return エラーメッセージ
     */
    public String validationCheck(BookDetailsInfo bookInfo) {
        String errorMsg = "";
        if (bookInfo.getTitle().isEmpty() || bookInfo.getAuthor().isEmpty() || bookInfo.getPublisher().isEmpty()
                || bookInfo.getPublish_date().isEmpty()) {
            errorMsg += "必要な情報がありません。";
        }

        if (!(bookInfo.getPublish_date().matches("[0-9]{8}"))) {
            errorMsg += "出版日はYYYYMMDDの形式で入力してください。";
        } else {
            try {
                DateFormat df = new SimpleDateFormat("yyyyMMdd");
                df.setLenient(false);
                df.parse(bookInfo.getPublish_date()); // df.parseでParseExceptionがThrowされる                
            } catch (ParseException p) {
                errorMsg += "出版日はYYYYMMDDの形式で入力してください。";
                p.printStackTrace();
            }
        }
        if (!(bookInfo.getIsbn().matches("([0-9]{10}|[0-9]{13})?"))) {
            errorMsg += "ISBNは10桁もしくは13桁の数字で入力してください。";
        }
        if (255 < bookInfo.getTitle().length() || 255 < bookInfo.getAuthor().length()
                || 255 < bookInfo.getPublisher().length() || 255 < bookInfo.getDescription().length()) {
            errorMsg += "書籍名、著者名、出版社、説明文は255文字以内で記述してください。";
        }
        return errorMsg;
    }
}
