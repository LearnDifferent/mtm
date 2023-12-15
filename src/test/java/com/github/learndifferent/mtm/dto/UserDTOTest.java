package com.github.learndifferent.mtm.dto;

import com.github.learndifferent.mtm.constant.enums.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserDTOTest {

    @Test
    @DisplayName("Password should be encrypted")
    void passwordShouldBeEncrypted() {
        String password = "123456789";
        UserDTO user = UserDTO.ofNewUser(1L, "username", password, UserRole.USER);
        Assertions.assertNotEquals(password, user.getPassword());
    }
}