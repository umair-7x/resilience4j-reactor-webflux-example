package gg.verdansk.r4j.PhoneChecker;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PhoneCheckerService {

    public Mono<Boolean> isValidNumber(String phoneNumber) {
        if (phoneNumber.length() > 0) {
            return Mono.just(Boolean.TRUE);
        }
        return Mono.just(Boolean.FALSE);
    }
}
