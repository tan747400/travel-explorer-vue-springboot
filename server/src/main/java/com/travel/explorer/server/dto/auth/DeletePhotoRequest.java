package com.travel.explorer.server.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeletePhotoRequest {

    @NotBlank(message = "imageUrl is required")
    private String imageUrl;
}