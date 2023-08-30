package io.alviss.recipe_api.recipe_api.config.cloudinary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
//@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinaryConfiguration;
//    private final Logger logger;

    public String uploadFile(MultipartFile file) {
        try {
            File uploadedFile = convertMultipartToFile(file);
            Map uploadResult;
            uploadResult = cloudinaryConfiguration.uploader().upload(uploadedFile, ObjectUtils.asMap(
                "folder", "recipe-app",
                "resource_type", "image"
            ));
            boolean isDeleted = uploadedFile.delete();
//            if (isDeleted) {
//                logger.info("File deleted!");
//            }
            return uploadResult.get("url").toString();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private File convertMultipartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();

        return convertedFile;
    }
}