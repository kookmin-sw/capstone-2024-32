package com.example.WebOrder.service;

import com.example.WebOrder.dto.ReviewDto;
import com.example.WebOrder.entity.Item;
import com.example.WebOrder.entity.Review;
import com.example.WebOrder.repository.ItemRepository;
import com.example.WebOrder.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.Dictionary;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ItemRepository itemRepository;
    private final ReviewRepository reviewRepository;

    public ReviewService(ItemRepository itemRepository, ReviewRepository reviewRepository) {
        this.itemRepository = itemRepository;
        this.reviewRepository = reviewRepository;
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


}
