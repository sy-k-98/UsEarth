package com.isfive.usearth.aop;

import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.isfive.usearth.domain.common.FileImage;
import com.isfive.usearth.domain.common.FileImageService;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class FilesDeleteAspect {

    private final FileImageService fileImageService;

    @AfterThrowing(
            pointcut = "@annotation(com.isfive.usearth.annotation.FilesDelete)",
            throwing = "e"
    )
    public void handleException(JoinPoint joinPoint, RuntimeException e) {

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof List<?> && !((List<?>) arg).isEmpty() && ((List<?>) arg).get(0) instanceof FileImage) {
                List<FileImage> fileImages = (List<FileImage>) arg;
                fileImages.forEach(fileImage -> fileImageService.deleteFile(fileImage.getStoredName()));
                break;
            }
        }
        throw e;
    }
}
