package com.marketplace.marketplace.image;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "images_seq")
    @SequenceGenerator(name = "images_seq", sequenceName = "images_id_seq", allocationSize = 1)
    private Long imageId;
    private String imagePath;
    @Enumerated(EnumType.STRING)
    private ImageType type;
}
