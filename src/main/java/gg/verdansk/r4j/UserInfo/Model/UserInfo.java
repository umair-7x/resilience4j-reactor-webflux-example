package gg.verdansk.r4j.UserInfo.Model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInfo {

    private String userName;
    private String userPhone;
    private String userId;
}
