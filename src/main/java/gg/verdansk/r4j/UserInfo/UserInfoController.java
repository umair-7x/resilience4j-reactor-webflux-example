package gg.verdansk.r4j.UserInfo;

import gg.verdansk.r4j.UserInfo.Model.UserInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class UserInfoController {

    private UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @GetMapping("/userinfo")
    public Mono<UserInfo> userInfo(@RequestParam(value = "id", defaultValue = "1") String id) {
        return userInfoService.getUserInfo(id);
    }
}
