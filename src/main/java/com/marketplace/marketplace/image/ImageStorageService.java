package com.marketplace.marketplace.image;

import com.marketplace.marketplace.exception.InvalidOperationException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
public class ImageStorageService {
    @Value("${spring.application.image-folder}")
    private Path imagesDir;

    // create the items and images folders after running the app
    @PostConstruct
    public void initDirectory() {
        File itemDir = new File(imagesDir.resolve("item-image").toString());
        File profileDir = new File(imagesDir.resolve("profile-image").toString());

        if (itemDir.mkdir() && profileDir.mkdir())
            log.info("Directories created successfully");
        else
            log.info("Directories already exists");
    }

    public String saveImageInFolder(
            MultipartFile image,
            long imageId,
            ImageType type
    ) throws IOException {

        if (image.isEmpty()){
            log.error("Image file [{}] is empty", image.getOriginalFilename());
            throw new IllegalArgumentException("Image file is empty");
        }

        Set<String> validExtensions = Set.of(".png", ".jpg", ".jpeg");
        String filename = image.getOriginalFilename();
        boolean isValid = validExtensions.stream().anyMatch(Objects.requireNonNull(filename)::endsWith);

        if (!isValid){
            log.error("The provided file is not an image file");
            throw new InvalidOperationException("The provided file is not an image file");
        }

        try {
            String folder = (type == ImageType.ITEM)? "item-image" : "profile-image";
            Path imagePath = imagesDir.resolve(String.format("%s/%s",folder, imageId) + ".png");
            Files.copy(image.getInputStream() , imagesDir.resolve(imagePath));
            return imagePath.toAbsolutePath().toString();
        } catch (IOException ex){
            log.error("Error while uploading the image, error: {}", ex.getMessage());
            throw new IOException("Error while saving image file", ex);
        }
    }

    public void deleteImage(String imagePath) throws IOException {
        try{
            Path path = Paths.get(imagePath);
            if(Files.deleteIfExists(path))
                log.info("Image file {} deleted successfully",path);
            else
                log.warn("Image file {} not found", path);
        } catch (IOException ex){
            log.error("Error while deleting image file");
            throw ex;
        }
    }
}
