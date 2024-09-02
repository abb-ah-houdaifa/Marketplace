package com.marketplace.marketplace.item;

import com.marketplace.marketplace.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    @Query(
            value = """
                        SELECT *
                        FROM items i
                        WHERE user_id = ?1
                        ORDER BY RANDOM()
                        LIMIT ?2
                    """,
            nativeQuery = true
    )
    List<Item> findRandomItemsByUser(Long userId, int limit);

    Page<Item> findAllByUser(User user, Pageable pageable);

    Page<Item> findByDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(String value1, String value2, Pageable page);

}
