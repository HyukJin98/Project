package edu.du.samplep.service;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank
    @Email
    private String email;
    @Size(min = 6)
    private String password;
    @NotEmpty
    private String name;

    public @NotBlank @Email String getEmail() {
        return email;
    }

    public @Size(min = 6) String getPassword() {
        return password;
    }

    public @NotEmpty String getName() {
        return name;
    }

    public void setEmail(@NotBlank @Email String email) {
        this.email = email;
    }

    public void setPassword(@Size(min = 6) String password) {
        this.password = password;
    }

    public void setName(@NotEmpty String name) {
        this.name = name;
    }
}
