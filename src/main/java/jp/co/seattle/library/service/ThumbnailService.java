package jp.co.seattle.library.service;

import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import jp.co.seattle.library.config.MinioConfig;
import jp.co.seattle.library.dto.BookDetailsInfo;

/**
 * サムネイルサービス
 * 
 * サムネイルに関してS3とのやりとりの処理を実装する
 */
@Controller
public class ThumbnailService {
    final static Logger logger = LoggerFactory.getLogger(ThumbnailService.class);

    private static final String S3_OBJECT_THUMBNAILS = "thumbnails/";
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioConfig minioConfig;

    /**
     * サムネイル画像をアップロードする
     * @param thumbnailName サムネイルファイル名
     * @param file サムネイルファイル
     * @return アップロードファイル名
     * @throws Exception
     */
    public String uploadThumbnail(String thumbnailName, MultipartFile file)
            throws Exception {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String extension = thumbnailName.substring(thumbnailName.lastIndexOf("."));

        //ファイル名をタイムスタンプの値にリネームする
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String timestampStr = (sdf.format(timestamp));
        String fileName = timestampStr + extension;

        // S3にサムネイル画像をアップロード
        InputStream inputStream = file.getInputStream();
        ObjectWriteResponse owr = minioClient.putObject(
                PutObjectArgs.builder().bucket(minioConfig.getMinioInfo("s3.bucket-name"))
                        .object(S3_OBJECT_THUMBNAILS + fileName)
                        .stream(
                                inputStream, -1, 10485760)
                        .build());

        return fileName;

    }

    /**
     * URL取得
     * @param fileName サムネイルファイル名
     * @return ファイルのURL
     * @throws Exception
     */
    public String getURL(String fileName) throws Exception {
        String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(minioConfig.getMinioInfo("s3.bucket-name"))
                        .object(S3_OBJECT_THUMBNAILS + fileName)
                        .build());

        return url;

    }

    /**
     * サムネイル画像を登録
     * @param file アップロードするサムネイルファイル
     * @param bookInfo
     * @return 正常終了ならtrue,異常終了ならfalse
     */
    public boolean uploadThumbnail(MultipartFile file, BookDetailsInfo bookInfo) {
        String thumbnail = file.getOriginalFilename();

        if (!file.isEmpty()) {
            try {
                // サムネイル画像をアップロード
                String fileName = uploadThumbnail(thumbnail, file);
                // URLを取得
                String thumbnailUrl = getURL(fileName);

                bookInfo.setThumbnailName(fileName);
                bookInfo.setThumbnailUrl(thumbnailUrl);

            } catch (Exception e) {
                // 異常終了時の処理
                logger.error("サムネイルアップロードでエラー発生", e);
                return false;
            }
        }
        return true;
    }

}
