package com.example.WebOrder.service;


import com.example.WebOrder.dto.ItemDto;
import com.example.WebOrder.entity.Item;
import com.example.WebOrder.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final LoginService loginService;

    public ItemService(ItemRepository itemRepository, LoginService loginService) {
        this.itemRepository = itemRepository;
        this.loginService = loginService;
    }
    

    public ItemDto getItemInfoById(Long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) throw new RuntimeException("아이템이 존재하지 않습니다.");

        return ItemDto.fromEntity(optionalItem.get());
    }

    public Long createItem(Long ownerId, ItemDto dto) {
        if (loginService.isCurrentUserAuthenticated(ownerId)) throw new RuntimeException("권한 없음");

        Item item = new Item();
        item.setName(dto.getName());
        item.setPrice(dto.getPrice());
        item.setItemImageUrl(dto.getItemImageUrl());
        item.setDescription(dto.getDescription());
        item.setOwnerId(ownerId);
        item.setReviews(new ArrayList<>());
        item.setAvgRate(0);
        item.setOrderedCount(0L);

        Item savedItem = itemRepository.save(item);

        return savedItem.getId();
    }

    @Transactional
    public void deleteItem(Long ownerId, Long itemId){
        if (loginService.isCurrentUserAuthenticated(ownerId)) throw new RuntimeException("권한 없음");
        itemRepository.deleteById(itemId);
    }

    public Long updateItem(Long ownerId, Long itemId,ItemDto dto) {
        if (loginService.isCurrentUserAuthenticated(ownerId)) throw new RuntimeException("권한 없음");

        Optional<Item> optionalItem = itemRepository.findById(itemId);

        if (optionalItem.isEmpty()) throw new RuntimeException("Item Doesn't Exist!");
        Item item = optionalItem.get();

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setItemImageUrl(dto.getItemImageUrl());
        item.setOwnerId(ownerId);

        itemRepository.save(item);

        return itemId;
    }
}
