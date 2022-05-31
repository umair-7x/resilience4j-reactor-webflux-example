package gg.verdansk.r4j.UserInfo;

import gg.verdansk.r4j.PersonalInfo.PersonalInfoService;
import gg.verdansk.r4j.PersonalInfoFallback.Model.UserData;
import gg.verdansk.r4j.PersonalInfoFallback.PersonalInfoFallbackService;
import gg.verdansk.r4j.PhoneChecker.PhoneCheckerService;
import gg.verdansk.r4j.UserInfo.Model.UserInfo;
import io.github.resilience4j.circuitbreaker.internal.InMemoryCircuitBreakerRegistry;

import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreaker;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {UserInfoService.class})
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootApplication
class UserInfoServiceTest {
    @MockBean
    private PersonalInfoFallbackService personalInfoFallbackService;

    @MockBean
    private PersonalInfoService personalInfoService;

    @MockBean
    private PhoneCheckerService phoneCheckerService;

    @Autowired
    private ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

    @Autowired
    private UserInfoService userInfoService;

    @Test
    void testGetUserInfoWithNoErrors() {
        UserInfo expectedResponse = UserInfo.builder()
                .userName("test-name")
                .userPhone("test-phone")
                .userId("42")
                .build();

        when(this.personalInfoService.getName(any())).thenReturn(Mono.just("test-name"));
        when(this.personalInfoService.getPhone(any())).thenReturn(Mono.just("test-phone"));
        when(this.phoneCheckerService.isValidNumber(any())).thenReturn(Mono.just(Boolean.TRUE));

        Mono<UserInfo> actualResponse = this.userInfoService.getUserInfo("42");

        Assertions.assertEquals(expectedResponse, actualResponse.block());

        verify(personalInfoService, times(1)).getName(any());
        verify(personalInfoService, times(1)).getPhone(any());
        verify(phoneCheckerService, times(1)).isValidNumber(any());
        verifyNoInteractions(personalInfoFallbackService);
    }

    @Test
    void testGetUserInfoFallback_whenGetPhoneFails() {

        UserInfo expectedResponse = UserInfo.builder()
                .userName("fallback-name")
                .userPhone("fallback-phone")
                .build();
        when(this.personalInfoService.getName(any())).thenReturn(Mono.just("test-name"));
        when(this.personalInfoService.getPhone(any())).thenReturn(Mono.error(new RuntimeException("error-getting-phone-number")));

        UserData userData = UserData.builder()
                .name("fallback-name")
                .phone("fallback-phone")
                .build();

        when(this.personalInfoFallbackService.getUserData(any())).thenReturn(Mono.just(userData));

        Mono<UserInfo> actualResponse = this.userInfoService.getUserInfo("41");
        Assertions.assertEquals(expectedResponse, actualResponse.block());

        verify(personalInfoService, times(1)).getName(any());
        verify(personalInfoService, times(1)).getPhone(any());
        verify(personalInfoFallbackService,times(1)).getUserData(any());
    }

    @Test
    void testGetUserInfoFallback_whenGetNameFails() {

        UserInfo expectedResponse = UserInfo.builder()
                .userName("fallback-name")
                .userPhone("fallback-phone")
                .build();
        when(this.personalInfoService.getName(any())).thenReturn(Mono.error(new RuntimeException("error-getting-name")));
        when(this.personalInfoService.getPhone(any())).thenReturn(Mono.just("333"));

        UserData userData = UserData.builder()
                .name("fallback-name")
                .phone("fallback-phone")
                .build();

        when(this.personalInfoFallbackService.getUserData(any())).thenReturn(Mono.just(userData));

        Mono<UserInfo> actualResponse = this.userInfoService.getUserInfo("41");
        Assertions.assertEquals(expectedResponse, actualResponse.block());

        verify(personalInfoService, times(1)).getName(any());
        verify(personalInfoService, times(1)).getPhone(any());
        verify(personalInfoFallbackService,times(1)).getUserData(any());
    }

    @Test
    void testGetUserInfoFallback_whenPhoneCheckerFails() {

        UserInfo expectedResponse = UserInfo.builder()
                .userName("fallback-name")
                .userPhone("fallback-phone")
                .build();
        when(this.personalInfoService.getName(any())).thenReturn(Mono.just("test-name"));
        when(this.personalInfoService.getPhone(any())).thenReturn(Mono.just("test-phone"));
        when(this.phoneCheckerService.isValidNumber(any())).thenReturn(Mono.error(new RuntimeException("error-validating-phone-number")));

        UserData userData = UserData.builder()
                .name("fallback-name")
                .phone("fallback-phone")
                .build();

        when(this.personalInfoFallbackService.getUserData(any())).thenReturn(Mono.just(userData));

        Mono<UserInfo> actualResponse = this.userInfoService.getUserInfo("41");
        Assertions.assertEquals(expectedResponse, actualResponse.block());

        verify(personalInfoService, times(1)).getName(any());
        verify(personalInfoService, times(1)).getPhone(any());
        verify(phoneCheckerService, times(1)).isValidNumber(any());
        verify(personalInfoFallbackService,times(1)).getUserData(any());
    }
}

