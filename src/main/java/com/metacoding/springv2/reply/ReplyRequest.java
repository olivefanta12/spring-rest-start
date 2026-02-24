package com.metacoding.springv2.reply;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class ReplyRequest {

    @Data
    public static class SaveDTO {
        @NotBlank
        private String comment;
    }
}
