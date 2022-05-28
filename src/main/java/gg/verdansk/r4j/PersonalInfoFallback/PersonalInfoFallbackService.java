package gg.verdansk.r4j.PersonalInfoFallback;

import gg.verdansk.r4j.PersonalInfoFallback.Model.UserData;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PersonalInfoFallbackService {

    public Mono<UserData> getUserData(String userId) {
        return Mono.just(UserData.builder()
                .name("Umair")
                .phone("312")
                .id(userId)
                .build());
    }
}
