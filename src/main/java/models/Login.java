package models;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Builder(toBuilder = true)
@Data
public class Login {

    private String username;
    private String password;

}
