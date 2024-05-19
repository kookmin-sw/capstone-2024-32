package com.example.WebOrder.controller;

import com.example.WebOrder.dto.ItemDto;
import com.example.WebOrder.dto.ReviewDto;
import com.example.WebOrder.exception.status4xx.ForbiddenException;
import com.example.WebOrder.exception.status4xx.TypicalException;
import com.example.WebOrder.service.ItemService;
import com.example.WebOrder.service.ReviewService;
import com.example.WebOrder.service.UrlEncodeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private final ItemService itemService;
    public ReviewController(ReviewService reviewService, ItemService itemService) {
        this.reviewService = reviewService;
        this.itemService = itemService;
    }

    
    // fetch를 통해 item의 review를 가져오는 컨트롤러
    // requestParam으로 page가 있어야 한다.
    @ResponseBody
    @GetMapping("/review/get/{itemId}")
    public List<ReviewDto> getReviewOfItem( @PathVariable("itemId") Long itemId, @RequestParam(name="page", defaultValue = "1") Integer page){
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.Direction.DESC, "id");

        List<ReviewDto> reviewsOfItem = reviewService.getReviewsOfItem(itemId, pageable);

        return reviewsOfItem;
    }

    @ResponseBody
    @GetMapping("/review/page/{itemId}")
    public Integer getReviewPageNumberOfItem( @PathVariable("itemId") Long itemId, @RequestParam(name="page", defaultValue = "1") Integer page){
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.Direction.DESC, "id");

        Integer pageNumber =  reviewService.getReviewPageOfItem(itemId, pageable).getTotalPages();

        return pageNumber;
    }

    // 쿠키를 분석해서 주문한 메뉴의 리스트를 보여주는 페이지
    @GetMapping("/review/menu/{encodedUserId}")
    public String getReviewMenuPage(HttpServletRequest request, Model model, @PathVariable("encodedUserId") String encodedUserId){
        Long userId = Long.parseLong(UrlEncodeService.decodeBase64UrlSafe(encodedUserId));
        String orderItemsIdString = new String();

        for (Cookie cookie : request.getCookies()){
            if (cookie.getName().equals("orderItemIds")){
                orderItemsIdString = cookie.getValue();
            }
        }
        log.info("orderItemIds : " + orderItemsIdString);

        model.addAttribute("itemList", reviewService.getItemDtoListFromCookieString(userId, orderItemsIdString));
        return "review/reviewMenu";
    }

    // 리뷰 적는 form을 주는 페이지
    @GetMapping("/review/write/{userId}/{itemId}")
    public String getReviewWriteForm(HttpServletRequest request, @PathVariable("userId") Long userId, @PathVariable("itemId") Long itemId,@RequestParam(name="page", defaultValue = "1") Integer page, Model model){
        // 검증 페이지
        ItemDto itemDto = itemService.getItemInfoById(itemId);
        if (!userId.equals(itemDto.getAdminId())) throw new TypicalException("잘못된 요청입니다!");

        String orderItemsIdString = new String();
        for (Cookie cookie : request.getCookies()){
            if (cookie.getName().equals("orderItemIds")){
                orderItemsIdString = cookie.getValue();
            }
        }
        if (!reviewService.doesOrderRecordsExistsInCookie(orderItemsIdString, itemId)) throw new ForbiddenException("메뉴를 주문한 적이 없거나 2시간이 지나 기록이 사라졌습니다!");
        
        model.addAttribute("item", itemDto);
        Integer totalPages = reviewService.getNumberOfPages(itemId);
        if (totalPages == 0)
            model.addAttribute("totalPage", 1);
        else
            model.addAttribute("totalPage", totalPages);

        model.addAttribute("reviews", reviewService.getReviewsOfItem(itemId, PageRequest.of(page - 1, 10, Sort.Direction.DESC, "id")));
        return "review/reviewWrite";
    }

    

    // 리뷰 적는 form을 제출
    @PostMapping("/review/write/{userId}/{itemId}")
    public String writeReview(HttpServletRequest request, @PathVariable("userId") Long userId, @PathVariable("itemId") Long itemId, ReviewDto reviewDto){
        // 검증 페이지
        ItemDto itemDto = itemService.getItemInfoById(itemId);
        if (!userId.equals(itemDto.getAdminId())) throw new TypicalException("잘못된 요청입니다!");

        String orderItemsIdString = new String();
        for (Cookie cookie : request.getCookies()){
            if (cookie.getName().equals("orderItemIds")){
                orderItemsIdString = cookie.getValue();
            }
        }
        if (!reviewService.doesOrderRecordsExistsInCookie(orderItemsIdString, itemId)) throw new ForbiddenException("메뉴를 주문한 적이 없거나 2시간이 지나 기록이 사라졌습니다!");

        reviewService.createReview(reviewDto);
        return "redirect:/review/write/" + userId + "/" + itemId;
    }
}
