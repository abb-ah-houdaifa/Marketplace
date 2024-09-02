package com.marketplace.marketplace.utils.modelAssembler;

import com.marketplace.marketplace.item.Item;
import com.marketplace.marketplace.item.ItemController;
import com.marketplace.marketplace.item.ItemResponse;
import com.marketplace.marketplace.utils.mappers.Mapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemModelAssembler implements RepresentationModelAssembler<Item, ItemResponse> {
    private final Mapper mapper;

    @Override
    public ItemResponse toModel(Item item) {
        ItemResponse itemResponse = mapper.mapItemToResponse(item);
        Link selfLink = linkTo(methodOn(ItemController.class).getItemById(item.getId())).withSelfRel();
        itemResponse.add(selfLink);

        return itemResponse;
    }

    public CollectionModel<ItemResponse> toCollectionModel(
            Page<Item> entities,
            HttpServletRequest request
    ) {
        List<ItemResponse> items = new ArrayList<>();
        for (Item item : entities) {
            ItemResponse itemResponse = toModel(item);
            items.add(itemResponse);
        }
        List<Link> links = formatPreviousAndNextLinks(
                request,
                entities.getPageable(),
                entities.getTotalPages()
        );

        return PagedModel.of(items, links);
    }

    private List<Link> formatPreviousAndNextLinks(
            HttpServletRequest request,
            Pageable pageable,
            int totalPages
    ) {
        String baseUrl = request.getRequestURL().toString();

        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();

        String prev = (page == 0) ? "" : String.format("%s?page=%s&size=%s",baseUrl, page - 1, size);
        String next = (totalPages == 0 || page == totalPages - 1) ? "" : String.format("%s?page=%s&size=%s",baseUrl, page + 1, size);

        List<Link> links = new ArrayList<>();

        if(!prev.isEmpty())
            links.add(Link.of(prev, "previous"));
        if(!next.isEmpty())
            links.add(Link.of(next, "next"));


        return links;
    }
}
