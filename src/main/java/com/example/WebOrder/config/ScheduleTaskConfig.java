package com.example.WebOrder.config;

import com.example.WebOrder.entity.User;
import com.example.WebOrder.repository.UserRepository;
import com.example.WebOrder.service.OrderPasswordService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@EnableScheduling
public class ScheduleTaskConfig {
    private final OrderPasswordService orderPasswordService;
    private final UserRepository userRepository;


    public ScheduleTaskConfig(OrderPasswordService orderPasswordService, UserRepository userRepository) {
        this.orderPasswordService = orderPasswordService;
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRate = 1800000) // 30분마다 실행
    public void updateEntranceCodes() {
        // 모든 사용자의 entrance code 업데이트
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            orderPasswordService.updateEntranceCode(user.getId());
        }
    }
}
