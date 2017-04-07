package models;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Builder(toBuilder = true)
@Data
public class User {

    private String uuid;
    private String username;
    private String password;
}
