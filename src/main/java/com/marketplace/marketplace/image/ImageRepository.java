package com.marketplace.marketplace.image;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query(value = "SELECT nextval('images_id_seq')", nativeQuery = true)
    Long getNextImageId();

    @Transactional
    @Modifying
    @Query(
            value = "insert into images (image_id, image_path, type) values (?1, ?2, ?3)",
            nativeQuery = true
    )
    void save(Long imageId, String imagePath, String imageType);
}
