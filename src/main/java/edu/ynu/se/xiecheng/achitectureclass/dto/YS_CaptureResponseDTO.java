package edu.ynu.se.xiecheng.achitectureclass.dto;

import lombok.Data;

@Data
public class YS_CaptureResponseDTO {
    private String msg;
    private String code;
    private CaptureData data;

    @Data
    public static class CaptureData {
        private String picUrl;
    }
} 