package jp.co.seattle.library.controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
import jp.co.seattle.library.service.ValidationCheck;

/**
 * Handles requests for the application home page.
 */
@Controller //APIの入り口
public class AddBooksBulkController {
    final static Logger logger = LoggerFactory.getLogger(AddBooksBulkController.class);

    @Autowired
    private BooksService booksService;

    @Autowired
    private ValidationCheck validationCheck;

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

        List<String> errorMessage = new ArrayList<String>();
        //書籍情報をリストに追加
        List<String[]> bookList = readCsvFile(file, errorMessage);

        //エラーがある際は一括登録画面にエラーメッセージを表示
        if (errorMessage.size() != 0) {
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

        model.addAttribute("complete", "登録完了");
        return "addBooksBulk";
    }

    /**
     * csvファイルの読み込み
     * @param file 入力されたcsvファイル
     * @param errorMessage エラーメッセージ格納ようのリスト
     * @return csvファイルから読み込んだ書籍情報
     */
    private List<String[]> readCsvFile(MultipartFile file, List<String> errorMessage) {
        List<String[]> bookList = new ArrayList<String[]>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));) {
            int fileRow = 1; //csvファイルの行番号            
            String line;
            // 一行ずつ読み出してList<String[]>型のbookListに格納
            while ((line = br.readLine()) != null) {
                String tmpMessage = null; //エラーメッセージ用の一時変数   

                //各行のデータを,毎に区切りbookListに格納        
                String[] bookData = line.split(",", -1);
                bookList.add(bookData);

                //bookData[0]-[3]は必須、[4][5]は任意
                if (bookData[0].isEmpty() || bookData[1].isEmpty() || bookData[2].isEmpty()
                        || bookData[3].isEmpty()) {
                    tmpMessage += "必要な情報がありません。";
                }
                //出版日とISBNのバリデーションチェック(bookData[3]=出版日、bookData[4]=ISBN)
                String[] errorMsg = validationCheck.validationCheck(bookData[3], bookData[4]);
                if (errorMsg[0] != null || errorMsg[1] != null) {
                    tmpMessage += errorMsg[0] + errorMsg[1];
                }

                //エラーがある場合はerrorMessageにエラーメッセージを追加、ない場合はnull
                if (tmpMessage != null) {
                    errorMessage.add(fileRow + "行目：" + tmpMessage);
                }
                fileRow++;
            }
        } catch (FileNotFoundException e) {
            logger.error("ファイルが見つかりません", e);
        } catch (IOException e) {
            logger.error("エラーが発生しました", e);
        }
        return bookList;
    }
}
