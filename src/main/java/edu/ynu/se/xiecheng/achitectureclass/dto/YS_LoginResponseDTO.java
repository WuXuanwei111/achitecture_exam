package edu.ynu.se.xiecheng.achitectureclass.dto;

import lombok.Data;

@Data
public class YS_LoginResponseDTO {
    private String result;
    private DataDTO data;
    private String page;
    private String code;
    private String msg;
    private String meta;
    
    @Data
    public static class DataDTO {
        private String accessToken;
        private Long expireTime;
    }
} 