package com.example.WebOrder.service;

import com.example.WebOrder.dto.ItemDto;
import com.example.WebOrder.dto.OrderItemDto;
import com.example.WebOrder.dto.ReviewDto;
import com.example.WebOrder.entity.Item;
import com.example.WebOrder.entity.Order;
import com.example.WebOrder.entity.Review;
import com.example.WebOrder.exception.status4xx.NoEntityException;
import com.example.WebOrder.exception.status4xx.TypicalException;
import com.example.WebOrder.repository.ItemRepository;
import com.example.WebOrder.repository.OrderRepository;
import com.example.WebOrder.repository.ReviewRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

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
        if (optionalItem.isEmpty()) throw new NoEntityException("해당하는 메뉴가 존재하지 않습니다!");
        Item reviewedItem = optionalItem.get();

        Review review = new Review();
        review.setItem(reviewedItem);
        review.setRate(dto.getRate());
        review.setComment(dto.getComment());

        review = reviewRepository.save(review);


        reviewedItem.setAvgRate(reviewRepository.findAverageRateByItemId(dto.getItemId()));

        itemRepository.save(reviewedItem);

        return review.getId();
    }

    public List<ReviewDto> getReviewsOfItem(Long itemId, Pageable pageable){
        List<Review> reviews = reviewRepository.findByItemId(itemId, pageable).getContent();
        log.info(reviews.toString());
        return reviews.stream().map(ReviewDto::fromEntity).toList();
    }

    public Page<Review> getReviewPageOfItem(Long itemId, Pageable pageable){
        return reviewRepository.findByItemId(itemId, pageable);
    }


    // 주문할 때, 주문 내역에 해당하는 item id를 쿠키로 생성하는 코드.
    // 리뷰 서비스에 활용 됨.
    public Cookie getCookieOfOrderInfo(HttpServletRequest request, Long orderId){
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) throw new NoEntityException("쿠키에 해당하는 주문 내역이 존재하지 않습니다!");
        Order order = optionalOrder.get();


        // request에 이미 해당하는 cookie가 있다면 (이전에 주문한 내역이 있다면), cookie를 이어서 작성하도록 값을 복사한다.
        Cookie cookie = new Cookie("orderItemIds", "");
        for (Cookie requestCookie : request.getCookies()){
            if (requestCookie.getName().equals("orderItemIds")){
                cookie.setValue(requestCookie.getValue());
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
            if (optionalItem.isEmpty()) throw new NoEntityException("해당하는 메뉴가 존재하지 않습니다!");
            if (!optionalItem.get().getAdminId().equals(userId)) throw new TypicalException("주문한 테이블의 QR를 다시 찌어주세요!");

            itemDtoList.add(ItemDto.fromEntity(optionalItem.get()));
        }

        return itemDtoList;


    }


    public Integer getNumberOfPages(Long itemId) {
        return reviewRepository.findByItemId(itemId, Pageable.ofSize(10)).getTotalPages();
    }

    // 소수점 아래 1자리만 보이게 한다.
    public Double getAverageRateOfItem(Long itemId){
        return reviewRepository.findAverageRateByItemId(itemId);
    }

    public Boolean doesOrderRecordsExistsInCookie(String cookieString, Long itemId) {
        String[] splitCookieString = cookieString.split("-");
        List<Long> itemIdList = Arrays.stream(splitCookieString)
                .map(String::trim)  // 각 요소의 앞뒤 공백 제거
                .filter(s -> !s.isEmpty())  // 빈 문자열 제거
                .map(Long::parseLong)  // 문자열을 정수로 변환
                .distinct()  // 중복 제거
                .toList();

        return itemIdList.contains(itemId);
    }
}
