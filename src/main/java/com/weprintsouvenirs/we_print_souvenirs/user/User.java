package com.weprintsouvenirs.we_print_souvenirs.user;

public record User(
        Long id,
        String username,
        String password,
        String email,
        String phone,
        String telegram
) {
}
