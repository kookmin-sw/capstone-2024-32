package com.example.WebOrder.service;


import com.example.WebOrder.dto.ItemDto;
import com.example.WebOrder.dto.MenuStatisticsDto;
import com.example.WebOrder.entity.Category;
import com.example.WebOrder.entity.Item;
import com.example.WebOrder.entity.ItemStatus;
import com.example.WebOrder.exception.status4xx.ForbiddenException;
import com.example.WebOrder.exception.status4xx.NoEntityException;
import com.example.WebOrder.exception.status4xx.NotAuthenticatedException;
import com.example.WebOrder.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;
    private final LoginService loginService;
    private final S3UploadService s3UploadService;

    public ItemService(ItemRepository itemRepository, CategoryRepository categoryRepository, LoginService loginService, S3UploadService s3UploadService) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.loginService = loginService;
        this.s3UploadService = s3UploadService;
    }

    //아이템 정보 리스트를 사용자에게 전달.
    public List<ItemDto> getAllItemsOfUser(Long userId){
        List<Item> itemList = itemRepository.findAllByAdminIdAndStatus(userId, ItemStatus.ACTIVE);

        List<ItemDto> itemDtoList = itemList.stream().map(ItemDto::fromEntity).toList();
        return itemDtoList;
    }
    

    public ItemDto getItemInfoById(Long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) throw new NoEntityException("해당 메뉴가 존재하지 않습니다!");

        return ItemDto.fromEntity(optionalItem.get());
    }

    public Long createItem(Long adminId, ItemDto dto, String fileName) {
        //중복 검증문.
        //if (!loginService.isCurrentUserAuthenticated(adminId)) throw new ForbiddenException("해당 작업을 할 권한이 존재하지 않습니다!");

        Item item = new Item();
        item.setName(dto.getName());
        item.setPrice(dto.getPrice());
        item.setItemImageUrl(fileName);
        item.setDescription(dto.getDescription());
        item.setAdminId(adminId);
        item.setReviews(new ArrayList<>());
        item.setAvgRate(0.0);
        item.setOrderedCount(0L);
        log.info(String.valueOf(dto.getCategoryId()));
        Category category = categoryRepository.findById(dto.getCategoryId()).get();
        item.setCategory(category);
        item.setStatus(ItemStatus.ACTIVE);

        Item savedItem = itemRepository.save(item);

        return savedItem.getId();
    }

    @Transactional
    public void deleteItem(Long ownerId, Long itemId) throws IOException {

        Item item = itemRepository.findById(itemId).get();
        if (!item.getAdminId().equals(ownerId)) throw new ForbiddenException("해당 작업을 할 권한이 존재하지 않습니다!");
        item.setStatus(ItemStatus.INACTIVE);
        itemRepository.save(item);
    }

    public Long updateItem(Long adminId, Long itemId, ItemDto dto) {
        if (!loginService.isCurrentUserAuthenticated(adminId)) throw new ForbiddenException("해당 작업을 할 권한이 존재하지 않습니다!");


        Optional<Item> optionalItem = itemRepository.findById(itemId);

        if (optionalItem.isEmpty()) throw new NoEntityException("해당 메뉴가 존재하지 않습니다!");
        Item item = optionalItem.get();

        //검증하기
        if (!item.getAdminId().equals(adminId)) throw new ForbiddenException("해당 작업을 할 권한이 존재하지 않습니다.");

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        Category category = categoryRepository.findById(dto.getCategoryId()).get();
        item.setCategory(category);

        itemRepository.save(item);

        return itemId;
    }

    public Long updateItem(Long adminId, Long itemId, ItemDto dto, String fileName) {
        if (!loginService.isCurrentUserAuthenticated(adminId)) throw new ForbiddenException("해당 작업을 할 권한이 존재하지 않습니다!");

        Optional<Item> optionalItem = itemRepository.findById(itemId);

        if (optionalItem.isEmpty()) throw new NoEntityException("해당 메뉴가 존재하지 않습니다!");
        Item item = optionalItem.get();

        //검증하기
        if (!item.getAdminId().equals(adminId)) throw new ForbiddenException("해당 작업을 할 권한이 존재하지 않습니다.");

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setItemImageUrl(fileName);
        Category category = categoryRepository.findById(dto.getCategoryId()).get();
        item.setCategory(category);

        itemRepository.save(item);

        return itemId;
    }

    public MenuStatisticsDto getBestItemStat(Long userId) {
        Optional<Item> avgRateEntity = itemRepository.findTopByAdminIdOrderByAvgRateDesc(userId);
        Optional<Item> orderedEntity = itemRepository.findTopByAdminIdOrderByOrderedCountDesc(userId);

        MenuStatisticsDto dto = new MenuStatisticsDto();
        if (avgRateEntity.isEmpty()) {
            dto.setBestRateMenu("메뉴 없음");
            dto.setBestRateNum(0.0);
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
