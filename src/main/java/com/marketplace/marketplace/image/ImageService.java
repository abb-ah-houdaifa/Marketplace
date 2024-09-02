package com.marketplace.marketplace.image;

import com.marketplace.marketplace.exception.ImageNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ImageService {
    private final ImageRepository imageRepository;
    private final ImageStorageService imageStorageService;


    private Image saveImage(MultipartFile image, ImageType type) throws IOException {
        long imageId = imageRepository.getNextImageId();
        String imagePath = imageStorageService.saveImageInFolder(image, imageId, type);

        imageRepository.save(
                imageId,
                imagePath,
                type.toString()
        );

        Image savedImage = this.fetchImage(imageId);
        log.info("Image saved successfully in the database");
        return savedImage;
    }

    private Image fetchImage(Long imageId){
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException(String.format("Image with ID : %s Not Found", imageId)));
    }

    public Image saveItemImage(MultipartFile image) throws IOException {
        return saveImage(image, ImageType.ITEM);
    }

    public Image saveProfileImage(MultipartFile image) throws IOException {
        return saveImage(image, ImageType.PROFILE);
    }

    public void deleteImage(Long imageId) throws IOException {
        Image imageEntity = this.fetchImage(imageId);

        String imagePath = imageEntity.getImagePath();
        imageStorageService.deleteImage(imagePath);
        imageRepository.deleteById(imageId);
        log.info("Image with ID: {} deleted successfully from database and file system", imageId);
    }

    public void editProfileImage(Image previousProfileImage, MultipartFile profileImage) throws IOException {
        imageStorageService.deleteImage(previousProfileImage.getImagePath()); // delete the previous image from the profile directory
        String newPath = imageStorageService.saveImageInFolder(
                profileImage,
                previousProfileImage.getImageId(),
                ImageType.PROFILE
        );// save the new profile image in the same path
        log.info("The profile image was changed successfully in {}", newPath);
    }

    public byte[] findImageById(Long imageId) throws IOException {
        Image image = this.fetchImage(imageId);

        Path imagePath = Paths.get(image.getImagePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(imagePath));

        return resource.getByteArray();
    }
}
