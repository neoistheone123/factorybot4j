package factorygirl4j.domain;

import lombok.Data;

@Data
public class User {
    private String firstName, lastName, email;
    private boolean admin;
}