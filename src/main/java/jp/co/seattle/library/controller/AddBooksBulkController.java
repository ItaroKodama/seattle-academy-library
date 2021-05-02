package jp.co.seattle.library.controller;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

/**
 * Handles requests for the application home page.
 */
@Controller //APIの入り口
public class AddBooksBulkController {
    final static Logger logger = LoggerFactory.getLogger(AddBooksBulkController.class);

    @Autowired
    private BooksService booksService;

    /**
     * 一括登録画面遷移
     * @param model
     * @return
     */
    @RequestMapping(value = "/addBooksBulk", method = RequestMethod.GET) //value＝actionで指定したパラメータ
    //RequestParamでname属性を取得
    public String insertBooksBulk(Model model) {
        return "addBooksBulk";
    }

    /**
     * 一括登録
     * @param locale ロケール情報
     * @param file csvファイル
     * @param model モデル
     * @return 一括登録画面
     */
    @Transactional
    @RequestMapping(value = "/insertBooksBulk", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String insertBooksBulk(Locale locale,
            @RequestParam("csvFile") MultipartFile file,
            Model model) {
        logger.info("Welcome insertBooksBulk.java! The client locale is {}.", locale);

        if (file.isEmpty()) {
            model.addAttribute("errorMessage", "ファイルが選択されていません。");
            return "addBooksBulk";
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));) {

            List<String[]> bookList = new ArrayList<String[]>();
            List<String> errorMessage = new ArrayList<String>();
            int fileRow = 0; //csvファイルの行番号            
            boolean flag = false; //errorの判定用フラグ
            String line;

            // 一行ずつ読み出してList<String>型のbookListに格納
            while ((line = br.readLine()) != null) {
                int i = 0;
                String[] bookData = new String[6];
                String tmpMessage = ""; //エラーメッセージ用の一時変数

                fileRow++;

                //各行のデータを,毎に区切りbookListに格納
                for (String tmpData : line.split(",")) {
                    bookData[i] = tmpData;
                    i++;
                }
                bookList.add(fileRow - 1, bookData);


                //bookData[0]-[3]は必須、[4][5]は任意
                if (bookData[0].isEmpty() || bookData[1].isEmpty() || bookData[2].isEmpty() || bookData[3].isEmpty()) {
                    tmpMessage += "必要な情報がありません。";
                    flag = true;
                }
                //出版日とISBNのバリデーションチェック
                //bookData[3]=出版日、bookData[4]=ISBN 
                if (bookData[3] != null) {
                    if (!(bookData[3].matches("[0-9]{8}"))) {
                        tmpMessage += "出版日はYYYYMMDDの形式で入力してください。";
                        flag = true;
                    } else {
                        try {
                            DateFormat df = new SimpleDateFormat("yyyyMMdd");
                            df.setLenient(false);
                            df.parse(bookData[3]);
                        } catch (ParseException p) {
                            tmpMessage += "出版日はYYYYMMDDの形式で入力してください。";
                            flag = true;
                            p.printStackTrace();
                        }
                    }
                }
                if (bookData[4] != null && !(bookData[4].isEmpty())
                        && !(bookData[4].matches("([0-9]{10}|[0-9]{13})?"))) {
                    tmpMessage += "ISBNは10桁もしくは13桁の数字で入力してください。";
                    flag = true;
                }

                //エラーがある場合はerrorMessageにエラーメッセージを追加、ない場合はnull
                if (tmpMessage != "") {
                    errorMessage.add(fileRow - 1, fileRow + "行目：" + tmpMessage);
                } else {
                    errorMessage.add(fileRow - 1, "null");
                }

            }

            if (flag) {
                model.addAttribute("errorMessage", errorMessage);
                return "addBooksBulk";
            }

            // 書籍情報を新規登録する
            for (int i = 0; i < bookList.size(); i++) {
                BookDetailsInfo bookInfo = new BookDetailsInfo();
                bookInfo.setTitle(bookList.get(i)[0]);
                bookInfo.setAuthor(bookList.get(i)[1]);
                bookInfo.setPublisher(bookList.get(i)[2]);
                bookInfo.setPublish_date(bookList.get(i)[3]);
                bookInfo.setIsbn(bookList.get(i)[4]);
                bookInfo.setDescription(bookList.get(i)[5]);

                booksService.registBook(bookInfo);
            }
        } catch (FileNotFoundException e) {
            System.out.println("ファイルが存在しません");
        } catch (IOException e) {
            System.out.println("error");
        }

        model.addAttribute("complete", "登録完了");
        return "addBooksBulk";
    }
}
