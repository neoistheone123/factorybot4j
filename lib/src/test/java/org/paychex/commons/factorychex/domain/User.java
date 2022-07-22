package org.paychex.commons.factorychex.domain;

import lombok.Data;

@Data
public class User {
    private String firstName, lastName, email;
    private boolean admin;
}