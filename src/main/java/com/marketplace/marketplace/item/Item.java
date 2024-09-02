package com.marketplace.marketplace.item;

import com.marketplace.marketplace.image.Image;
import com.marketplace.marketplace.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "items_seq")
    @SequenceGenerator(name = "items_seq", sequenceName = "items_id_seq", allocationSize = 1)
    private Long id;
    private String name;
    private String description;
    private Integer price;
    @Enumerated(EnumType.STRING)
    private ItemCondition condition;
    private Date publishedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Image> images;
}
