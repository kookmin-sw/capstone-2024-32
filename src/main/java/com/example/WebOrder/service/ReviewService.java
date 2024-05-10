package com.example.WebOrder.service;

import com.example.WebOrder.dto.ItemDto;
import com.example.WebOrder.dto.OrderItemDto;
import com.example.WebOrder.dto.ReviewDto;
import com.example.WebOrder.entity.Item;
import com.example.WebOrder.entity.Order;
import com.example.WebOrder.entity.OrderItem;
import com.example.WebOrder.entity.Review;
import com.example.WebOrder.repository.ItemRepository;
import com.example.WebOrder.repository.OrderRepository;
import com.example.WebOrder.repository.ReviewRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewService {

    private final ItemRepository itemRepository;
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    public ReviewService(ItemRepository itemRepository, ReviewRepository reviewRepository, OrderRepository orderRepository) {
        this.itemRepository = itemRepository;
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
    }

    public Long createReview(ReviewDto dto){
        Optional<Item> optionalItem = itemRepository.findById(dto.getItemId());
        if (optionalItem.isEmpty()) throw new RuntimeException("엔티티없음");
        Item reviewedItem = optionalItem.get();

        Review review = new Review();
        review.setItem(reviewedItem);
        review.setRate(dto.getRate());
        review.setComment(dto.getComment());

        reviewedItem.getReviews().add(review);

        int avgOfRate = 0;
        for (Review incReview : reviewedItem.getReviews()){
            avgOfRate += incReview.getRate();
        }
        avgOfRate /= reviewedItem.getReviews().size();
        reviewedItem.setAvgRate(avgOfRate);


        Long returnValue = reviewRepository.save(review).getId();
        itemRepository.save(reviewedItem);

        return returnValue;
    }

    public List<ReviewDto> getReviewsOfItem(Long itemId, Pageable pageable){
        List<Review> reviews = reviewRepository.findByItemId(itemId, pageable).getContent();
        log.info(reviews.toString());
        return reviews.stream().map(ReviewDto::fromEntity).toList();
    }


    // 주문할 때, 주문 내역에 해당하는 item id를 쿠키로 생성하는 코드.
    // 리뷰 서비스에 활용 됨.
    public Cookie getCookieOfOrderInfo(HttpServletRequest request, Long orderId){
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) throw new RuntimeException("엔티티없음");
        Order order = optionalOrder.get();


        // request에 이미 해당하는 cookie가 있다면 (이전에 주문한 내역이 있다면), cookie를 이어서 작성하도록 값을 복사한다.
        Cookie cookie = new Cookie("orderItemIds", "");
        for (Cookie requestCookie : request.getCookies()){
            if (requestCookie.getName().equals("orderItemIds")){
                cookie.setValue(requestCookie.getValue());
                log.info("현재 가져온 쿠키 value : " + requestCookie.getName() + "/" +  requestCookie.getValue());
            }
        }


        StringBuilder sb = new StringBuilder();
        for (OrderItemDto itemDto : order.getOrderItems()){
            sb.append("-");
            sb.append(itemDto.getItemId().toString());
        }

        cookie.setValue(cookie.getValue() + sb.toString());
        cookie.setMaxAge(7200);
        cookie.setPath("/");

        return cookie;
    }

    // 리뷰를 작성하고자 리뷰 메뉴 페이지에 접근할 때, 주문 내역에 남아있는 메뉴들을 parsing해서 dto 리스트로 가져오는 메소드
    public List<ItemDto> getItemDtoListFromCookieString(Long userId, String cookieString){
        String[] splitCookieString = cookieString.split("-");
        List<Long> itemIdList = Arrays.stream(splitCookieString)
                .map(String::trim)  // 각 요소의 앞뒤 공백 제거
                .filter(s -> !s.isEmpty())  // 빈 문자열 제거
                .map(Long::parseLong)  // 문자열을 정수로 변환
                .distinct()  // 중복 제거
                .toList();  // 리스트로 수집

        List<ItemDto> itemDtoList = new ArrayList<>();

        for (Long itemId : itemIdList){
            Optional<Item> optionalItem = itemRepository.findById(itemId);
            if (optionalItem.isEmpty()) throw new RuntimeException("엔티티없음");

            itemDtoList.add(ItemDto.fromEntity(optionalItem.get()));
        }

        return itemDtoList;


    }


    public Integer getNumberOfPages(Long itemId) {
        return reviewRepository.findByItemId(itemId, Pageable.ofSize(10)).getTotalPages();
    }
}
