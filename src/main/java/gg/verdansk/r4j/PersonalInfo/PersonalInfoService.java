package gg.verdansk.r4j.PersonalInfo;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PersonalInfoService {

    public Mono<String> getName(String userId) {
        return Mono.just("John");
    }

    public Mono<String> getPhone(String userId) {
        return Mono.just("847").
                transformDeferred(it -> {
                    if (userId.equals("404")) {
                        return Mono.error(new RuntimeException("error getting phone"));
                    }
                    return Mono.just("847-1");
                });
    }
}
