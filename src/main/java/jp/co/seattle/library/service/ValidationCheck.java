package jp.co.seattle.library.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;

import jp.co.seattle.library.dto.BookDetailsInfo;

@Controller
public class ValidationCheck {

    /**
     * 出版日とISBNのバリデーションチェック
     * @param publishDate チェックする出版日
     * @param isbn チェックするISBN
     * @return Listの１つ目に出版日のエラーメッセージ、２つ目にISBNのエラーメッセージ
     */
    public List<String> validationCheck(String publishDate, String isbn) {
        List<String> errorMsg = new ArrayList<String>();
        errorMsg.add(0, "");
        errorMsg.add(1, "");
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
        return errorMsg;
    }

    /**
     * 書籍情報の必須項目とバリデーションのチェック
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
        return errorMsg;
    }
}
