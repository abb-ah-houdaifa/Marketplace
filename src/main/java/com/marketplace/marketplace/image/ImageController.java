package com.marketplace.marketplace.image;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("api/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getImage(
            @PathVariable("id") Long imageId
    ){
        try{
            byte[] image = imageService.findImageById(imageId);
            return new ResponseEntity<>(image, HttpStatus.OK);
        } catch (IOException ex){
            return new ResponseEntity<>("Image could be fetched", HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
