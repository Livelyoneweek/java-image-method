package study.resize.resize;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/api/file")
public class ResizeController {

    private final ResizeService resizeService;

    @PostMapping
    public void upload(@RequestPart(required = false) MultipartFile file,
                       HttpServletResponse res) {
        log.info("ResizeController.upload");
        resizeService.saveFiles(res,file);
    }
}
