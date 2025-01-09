package edu.ynu.se.xiecheng.achitectureclass.service;

import edu.ynu.se.xiecheng.achitectureclass.dto.YS_LoginResponseDTO;
import edu.ynu.se.xiecheng.achitectureclass.dto.YS_CaptureResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class YS_ServiceTest {

    @Autowired
    private YS_Service ys_Service;

    private static final String TEST_ACCOUNT_ID = "1ca7f0ab24ba41b291346347ec30140e";
    private static final String TEST_DEVICE_SERIAL = "AB9831171";
    private static final String TEST_CHANNEL_NO = "1";

    @Test
    public void testLogin() {
        // 测试登录功能
        YS_LoginResponseDTO response = ys_Service.login(TEST_ACCOUNT_ID);
        
        // 验证响应不为空
        assertNotNull(response);
        // 验证返回的token不为空
        assertNotNull(response.getData());
        assertNotNull(response.getData().getAccessToken());
        // 验证token的有效期
        assertTrue(response.getData().getExpireTime() > System.currentTimeMillis());
        
        System.out.println("登录成功，获取到的token: " + response.getData().getAccessToken());
    }

    @Test
    public void testCapture() {
        // 先登录获取token
        YS_LoginResponseDTO loginResponse = ys_Service.login(TEST_ACCOUNT_ID);
        String token = loginResponse.getData().getAccessToken();
        
        // 测试抓拍功能
        YS_CaptureResponseDTO response = ys_Service.capture(token, TEST_DEVICE_SERIAL, TEST_CHANNEL_NO, 1);
        
        // 验证响应不为空
        assertNotNull(response);
        // 验证返回码为200
        assertEquals("200", response.getCode());
        // 验证返回的图片URL不为空
        assertNotNull(response.getData());
        assertNotNull(response.getData().getPicUrl());
        
        System.out.println("抓拍成功，图片URL: " + response.getData().getPicUrl());
    }

    @Test
    public void testCaptureWithDifferentQualities() {
        // 先登录获取token
        YS_LoginResponseDTO loginResponse = ys_Service.login(TEST_ACCOUNT_ID);
        String token = loginResponse.getData().getAccessToken();
        
        // 测试不同清晰度的抓拍
        Integer[] qualities = {0, 1, 2, 3, 4};
        for (Integer quality : qualities) {
            YS_CaptureResponseDTO response = ys_Service.capture(token, TEST_DEVICE_SERIAL, TEST_CHANNEL_NO, quality);
            
            // 验证每个清晰度都能成功抓拍
            assertNotNull(response);
            assertEquals("200", response.getCode());
            assertNotNull(response.getData());
            assertNotNull(response.getData().getPicUrl());
            
            System.out.println("清晰度" + quality + "抓拍成功，图片URL: " + response.getData().getPicUrl());
        }
    }

    @Test
    public void testInvalidDeviceSerial() {
        // 先登录获取token
        YS_LoginResponseDTO loginResponse = ys_Service.login(TEST_ACCOUNT_ID);
        String token = loginResponse.getData().getAccessToken();
        
        // 测试无效的设备序列号
        YS_CaptureResponseDTO response = ys_Service.capture(token, "invalid_serial", TEST_CHANNEL_NO, 1);
        
        // 验证错误响应
        assertNotNull(response);
        assertEquals("20014", response.getCode());  // 设备序列号错误的错误码
        assertEquals("设备序列不正确", response.getMsg());
        assertNull(response.getData());
    }
} 