package jp.co.seattle.library.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.stereotype.Controller;

@Controller
public class ValidationCheck {

    /**
     * 出版日とISBNのバリデーションチェック
     * @param publishDate チェックする出版日
     * @param isbn チェックするISBN
     * @return Listの１つ目に出版日のエラーメッセージ、２つ目にISBNのエラーメッセージ
     */
    public String[] validationCheck(String publishDate, String isbn) {
        String[] errorMsg = new String[2];

        if (!(publishDate.matches("[0-9]{8}"))) {
            errorMsg[0] = "出版日はYYYYMMDDの形式で入力してください";
        } else {
            try {
                DateFormat df = new SimpleDateFormat("yyyyMMdd");
                df.setLenient(false);
                df.parse(publishDate); // df.parseでParseExceptionがThrowされる                
            } catch (ParseException p) {
                errorMsg[0] = "出版日はYYYYMMDDの形式で入力してください";
                p.printStackTrace();
            }
        }
        if (!(isbn.matches("([0-9]{10}|[0-9]{13})?"))) {
            errorMsg[1] = "ISBNは10桁もしくは13桁の数字で入力してください";
        }

        return errorMsg;
    }
}
