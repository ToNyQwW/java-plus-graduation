package ru.practicum.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewUserRequest {

    @Email
    @NotBlank
    @Size(min = 6, max = 512)
    private String email;

    @Size(min = 2, max = 255)
    @NotBlank
    private String name;
}
