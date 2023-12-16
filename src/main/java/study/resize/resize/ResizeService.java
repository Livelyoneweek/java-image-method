package study.resize.resize;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ResizeService {

//    public void saveFiles(HttpServletResponse res, MultipartFile file) {
//        log.info("ResizeService.saveFiles");
//        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
//        log.info("extension={}",extension);
//        log.info("ContentType={}",file.getContentType());
//        log.info("OriginalFilename}",file.getOriginalFilename());
//        log.info("Size={}",file.getSize());
//
//        try (OutputStream os = res.getOutputStream();
//             InputStream inputStream = file.getInputStream()) {
//
//            // 버퍼를 사용하여 파일을 청크 단위로 전송
//            byte[] buffer = new byte[1024 * 4]; // 4KB size buffer
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                os.write(buffer, 0, bytesRead);
//            }
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void saveFiles(HttpServletResponse res, MultipartFile file) {
        res.setContentType("image/jpeg"); // MIME 타입 설정
        log.info("ResizeService.saveFiles");

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        log.info("extension={}",extension);
        log.info("ContentType={}",file.getContentType());
        log.info("OriginalFilename}",file.getOriginalFilename());
        log.info("Size={}",file.getSize());

        // 지원되는 이미지 타입 목록
        List<String> supportedImageTypes = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/bmp");


        if (!supportedImageTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException("지원되지 않는 파일 형식입니다: " + file.getContentType());
        }


        try (InputStream inputStream = file.getInputStream();
             OutputStream outputStream = res.getOutputStream()) {

//            String[] formatNames = ImageIO.getWriterFormatNames();
//            log.info("지원 가능한 포맷: {}", Arrays.toString(formatNames));

            BufferedImage originalImage = ImageIO.read(inputStream);
            if (originalImage == null) {
                throw new IOException("이미지 로드 실패");
            }

            // 이미지 타입 및 해상도 절반으로
//            BufferedImage processedImage = resizeAndConvertImage(originalImage, originalImage.getWidth() / 2, originalImage.getHeight() / 2, BufferedImage.TYPE_INT_RGB);

            // 이미지 타입을 TYPE_INT_RGB로 변경 (JPEG 포맷에 적합하도록)
            BufferedImage processedImage = convertToType(originalImage, BufferedImage.TYPE_INT_RGB);

            // 조정된 이미지를 OutputStream으로 전송
            boolean result = ImageIO.write(processedImage, "jpg", outputStream);
            log.info("result={}",result);
            outputStream.flush(); // OutputStream을 플러시
        } catch (IOException e) {
            throw new RuntimeException("이미지 처리 실패", e);
        }
    }

    private BufferedImage resizeAndConvertImage(BufferedImage originalImage, int targetWidth, int targetHeight, int targetType) {
        log.info("resizeAndConvertImage 시작: 원본 이미지 타입={}, 너비={}, 높이={}", originalImage.getType(), originalImage.getWidth(), originalImage.getHeight());
        log.info("목표 이미지 크기 및 타입: 너비={}, 높이={}, 타입={}", targetWidth, targetHeight, targetType);

        BufferedImage processedImage = new BufferedImage(targetWidth, targetHeight, targetType);
        Graphics2D g = processedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();

        log.info("이미지 리사이즈 및 타입 변환 완료");
        return processedImage;
    }

    private BufferedImage convertToType(BufferedImage originalImage, int newType) {
        if (originalImage.getType() != newType) {
            BufferedImage newImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), newType);
            Graphics2D g = newImage.createGraphics();
            g.drawImage(originalImage, 0, 0, null);
            g.dispose();
            return newImage;
        }
        return originalImage;
    }

}
