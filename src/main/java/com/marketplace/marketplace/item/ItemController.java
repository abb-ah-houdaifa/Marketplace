package com.marketplace.marketplace.item;

import com.marketplace.marketplace.item.search.ItemSearchCriteria;
import com.marketplace.marketplace.item.search.ItemSpecificationBuilder;
import com.marketplace.marketplace.utils.modelAssembler.ItemModelAssembler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/items")
public class ItemController {

    private final ItemService itemService;
    private final ItemModelAssembler itemModelAssembler;

    @GetMapping(
            value = "/{itemId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getItemById(@PathVariable("itemId")Long itemId) {
        Item item = itemService.getItemById(itemId);
        return new ResponseEntity<>(itemModelAssembler.toModel(item), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<?> getItems(
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "15", required = false) int size,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> items = itemService.getAllItems(pageable);
        return new ResponseEntity<>(
                itemModelAssembler.toCollectionModel(
                        items,
                        request
                ),
                HttpStatus.OK
        );
    }


    @GetMapping("/search")
    public ResponseEntity<?> searchFor(
            @RequestParam("value") String value,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "15", required = false) int size,
            HttpServletRequest request
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> items = itemService.searchFor(value, pageable);
        return new ResponseEntity<>(
                itemModelAssembler.toCollectionModel(
                        items,
                        request
                ),
                HttpStatus.OK
        );
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchFor(
            @RequestBody List<ItemSearchCriteria> criteriaList,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "18", required = false) int size,
            HttpServletRequest request
    ) {
        ItemSpecificationBuilder specBuilder = new ItemSpecificationBuilder();
        // add each element from criteriaList to the specBuilder
        if (criteriaList != null) {
            criteriaList.forEach(specBuilder::with);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Item> items = itemService.findBySpecificationCriteria(
                specBuilder.build(),
                pageable
        );

        return new ResponseEntity<>(
                itemModelAssembler.toCollectionModel(
                        items,
                        request
                ),
                HttpStatus.OK
        );
    }

}
