package com.avni.URLShortener.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class URLRequestDTO {

    @NotBlank(message = "URL cannot be blank")
    @URL(message = "URL is invalid")
    private String url;
}
