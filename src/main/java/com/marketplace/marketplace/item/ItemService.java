package com.marketplace.marketplace.item;

import com.marketplace.marketplace.exception.InvalidOperationException;
import com.marketplace.marketplace.exception.ItemNotFoundException;
import com.marketplace.marketplace.image.Image;
import com.marketplace.marketplace.image.ImageService;
import com.marketplace.marketplace.user.User;
import com.marketplace.marketplace.user.userListings.ListingRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final ImageService imageService;

    // returns listings randomly from the database
    public Page<Item> getRandomListingsByUser(User user,Pageable pageable) {
        int limit = pageable.getPageSize();
        int totalItems = (int)itemRepository.count();
        List<Item> items = itemRepository.findRandomItemsByUser(user.getId(), limit);
        return new PageImpl<>(items, pageable, totalItems);
    }

    public Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with ID : %s not found", itemId)));
    }

    public Item saveItem(
            User user,
            List<MultipartFile> itemImageFiles,
            ListingRequest request
    ) {

        Item item = Item.builder()
                .name(request.getItemName())
                .description(request.getItemDescription())
                .price(request.getItemPrice())
                .publishedAt(new Date())
                .user(user)
                .condition(request.getItemCondition())
                .images(new ArrayList<>())
                .build();

        Item savedItem = itemRepository.save(item);
        log.info("Item with Id {} saved successfully", savedItem.getId());

        try{
            List<Image> itemImagesList = mapImageFileToImage(itemImageFiles);
            savedItem.setImages(itemImagesList);
        } catch (IOException ex){
            log.info("Image of the item with ID {} could not be saved in the database", savedItem.getId());
        }

        return savedItem;
    }

    private List<Image> mapImageFileToImage(List<MultipartFile> imageFilesList) throws IOException{
        List<Image> itemImages = new ArrayList<>();
        for (MultipartFile itemImageFile : imageFilesList) {
            Image itemImage = imageService.saveItemImage(itemImageFile);
            itemImages.add(itemImage);
        }

        return itemImages;
    }

    public Item addImagesToItem(
            Item item,
            List<MultipartFile> newItemImageFiles
    ) throws IOException {
        try{
            List<Image> previousItemImages = item.getImages();
            List<Image> newItemImages = mapImageFileToImage(newItemImageFiles);

            previousItemImages.addAll(newItemImages);
            item.setImages(previousItemImages);
            log.info("Image was added to item with ID : {}", item.getId());
            return item;
        } catch (IOException ex){
            log.error("Image could not be added to the item with ID : {}", item.getId());
            throw ex;
        }
    }

    public Item editListingDetails(Item item, ListingRequest newListingDetails) {
        item.setCondition(newListingDetails.getItemCondition());
        item.setName(newListingDetails.getItemName());
        item.setDescription(newListingDetails.getItemDescription());
        item.setPrice(newListingDetails.getItemPrice());

        return itemRepository.save(item);
    }

    public void deleteListing(Item item) throws IOException {
        List<Image> imagesList = item.getImages();

        try {
            for (Image image : imagesList){
                imageService.deleteImage(image.getImageId());
            }
        } catch (IOException ex) {
            log.error("Error while deleting item with Id : {} images", item.getId());
            throw ex;
        }

        itemRepository.delete(item);
        log.info("Item with Id : {} was deleted successfully", item.getId());
    }

    private Image assertThatImageBelongsToCorrectItem(List<Image> images, Long itemImageId) {
        for (Image image : images){
            if(image.getImageId().equals(itemImageId)){
                return image;
            }
        }
        return null;
    }

    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(
                        String.format("Item with with ID : %s Not Found", itemId))
                );
    }

    public Page<Item> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    // find by a search filter
    public Page<Item> findBySpecificationCriteria(
            Specification<Item> specification,
            Pageable pageable
    ) {
        return itemRepository.findAll(specification, pageable);
    }

    public Page<Item> searchFor(String value, Pageable pageable) {
        return itemRepository.findByDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(value, value, pageable);
    }

    public Page<Item> getUserListings(
            User user,
            Pageable pageable
    ) {
        return itemRepository.findAllByUser(user, pageable);
    }
}
