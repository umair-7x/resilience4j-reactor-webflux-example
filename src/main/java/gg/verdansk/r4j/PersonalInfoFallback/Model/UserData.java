package gg.verdansk.r4j.PersonalInfoFallback.Model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserData {

    private String name;
    private String phone;
    private String id;
}
