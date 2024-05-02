package com.example.WebOrder.service;


import com.example.WebOrder.dto.ItemDto;
import com.example.WebOrder.dto.MenuStatisticsDto;
import com.example.WebOrder.entity.Item;
import com.example.WebOrder.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final LoginService loginService;
    private final S3UploadService s3UploadService;

    public ItemService(ItemRepository itemRepository, LoginService loginService, S3UploadService s3UploadService) {
        this.itemRepository = itemRepository;
        this.loginService = loginService;
        this.s3UploadService = s3UploadService;
    }

    //아이템 정보 리스트를 사용자에게 전달.
    public List<ItemDto> getAllItemsOfUser(Long userId){
        List<Item> itemList = itemRepository.findAllByOwnerId(userId);

        List<ItemDto> itemDtoList = itemList.stream().map(ItemDto::fromEntity).toList();
        return itemDtoList;
    }
    

    public ItemDto getItemInfoById(Long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) throw new RuntimeException("아이템이 존재하지 않습니다.");

        return ItemDto.fromEntity(optionalItem.get());
    }

    public Long createItem(Long ownerId, ItemDto dto) {
        if (!loginService.isCurrentUserAuthenticated(ownerId)) throw new RuntimeException("권한 없음");

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

    public Long createItem(Long ownerId, ItemDto dto, String fileName) {
        if (!loginService.isCurrentUserAuthenticated(ownerId)) throw new RuntimeException("권한 없음");

        Item item = new Item();
        item.setName(dto.getName());
        item.setPrice(dto.getPrice());
        item.setItemImageUrl(fileName);
        item.setDescription(dto.getDescription());
        item.setOwnerId(ownerId);
        item.setReviews(new ArrayList<>());
        item.setAvgRate(0);
        item.setOrderedCount(0L);

        Item savedItem = itemRepository.save(item);

        return savedItem.getId();
    }

    @Transactional
    public void deleteItem(Long ownerId, Long itemId) throws IOException {
        if (!loginService.isCurrentUserAuthenticated(ownerId)) throw new RuntimeException("권한 없음");
        String image = itemRepository.findById(itemId).get().getItemImageUrl();
        if (image != null) {
            s3UploadService.delete(image);
        }
        itemRepository.deleteById(itemId);
    }

    public Long updateItem(Long ownerId, Long itemId,ItemDto dto) {
        if (!loginService.isCurrentUserAuthenticated(ownerId)) throw new RuntimeException("권한 없음");

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

    public MenuStatisticsDto getBestItemStat(Long userId) {
        Optional<Item> avgRateEntity = itemRepository.findTopByOwnerIdOrderByAvgRateDesc(userId);
        Optional<Item> orderedEntity = itemRepository.findTopByOwnerIdOrderByOrderedCountDesc(userId);

        MenuStatisticsDto dto = new MenuStatisticsDto();
        if (avgRateEntity.isEmpty()) {
            dto.setBestRateMenu("메뉴 없음");
            dto.setBestRateNum(0);
        }
        else {
            dto.setBestRateMenu(avgRateEntity.get().getName());
            dto.setBestRateNum(avgRateEntity.get().getAvgRate());
        }

        if (orderedEntity.isEmpty()){
            dto.setMostOrderedMenu("메뉴 없음");
            dto.setMostOrderedNum(0L);
        }
        else {
            dto.setMostOrderedMenu(orderedEntity.get().getName());
            dto.setMostOrderedNum(orderedEntity.get().getOrderedCount());
        }

        return dto;
    }
}
