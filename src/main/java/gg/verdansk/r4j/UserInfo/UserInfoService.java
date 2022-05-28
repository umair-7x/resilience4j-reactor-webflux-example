package gg.verdansk.r4j.UserInfo;

import gg.verdansk.r4j.PersonalInfo.PersonalInfoService;
import gg.verdansk.r4j.PersonalInfoFallback.PersonalInfoFallbackService;
import gg.verdansk.r4j.UserInfo.Model.UserInfo;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserInfoService {

    private final PersonalInfoService personalInfoService;
    private final PersonalInfoFallbackService personalInfoFallbackService;
    private final ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

    public UserInfoService(PersonalInfoService personalInfoService,
                           PersonalInfoFallbackService personalInfoFallbackService,
                           ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory) {
        this.personalInfoService = personalInfoService;
        this.personalInfoFallbackService = personalInfoFallbackService;
        this.reactiveCircuitBreakerFactory = reactiveCircuitBreakerFactory;
    }

    public Mono<UserInfo> getUserInfo(String userId) {
        Mono<String> name = personalInfoService.getName(userId);
        Mono<String> phone = personalInfoService.getPhone(userId);
        return collectUserInfo(name, phone, userId);
    }

    private Mono<UserInfo> getUserInfoFromFallback(String userId) {
        return Mono.from(personalInfoFallbackService.getUserData(userId))
                .flatMap(data -> {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUserName(data.getName());
                    userInfo.setUserPhone(data.getPhone());
                    userInfo.setUserId(data.getId());
                    return Mono.just(userInfo);
                });
    }

    private Mono<UserInfo> collectUserInfo(Mono<String> name, Mono<String> phone, String userId) {
        return Mono.zip(name, phone)
                .flatMap(data -> {
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUserName(data.getT1());
                    userInfo.setUserPhone(data.getT2());
                    userInfo.setUserId(userId);
                    return Mono.just(userInfo);
                })
                .transform(it -> {
                    ReactiveCircuitBreaker rcb = reactiveCircuitBreakerFactory.create("user-info-service");
                    return rcb.run(it, throwable -> getUserInfoFromFallback(userId));
                });
    }
}
