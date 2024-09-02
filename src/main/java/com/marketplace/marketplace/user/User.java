package com.marketplace.marketplace.user;

import com.marketplace.marketplace.image.Image;
import com.marketplace.marketplace.item.Item;
import com.marketplace.marketplace.utils.encryption.AttributeEncryptor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(name = "users_seq", sequenceName = "users_id_seq", allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id")
    private Image image;

    private String name;
    @Column(unique = true)
    @Convert(converter = AttributeEncryptor.class)
    private String username;
    private String password;
    private String phoneNumber;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Item> publishedItems;

    private boolean isLocked;
    private boolean isEnabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
