package edu.ynu.se.xiecheng.achitectureclass.service;

import edu.ynu.se.xiecheng.achitectureclass.dto.YS_LoginResponseDTO;
import edu.ynu.se.xiecheng.achitectureclass.dto.YS_CaptureResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.beans.factory.annotation.Autowired;
import edu.ynu.se.xiecheng.achitectureclass.dao.Equipment_CaptureDao;
import edu.ynu.se.xiecheng.achitectureclass.entity.Equipment_Capture;
import java.time.LocalDateTime;
import edu.ynu.se.xiecheng.achitectureclass.entity.Equipment;
import edu.ynu.se.xiecheng.achitectureclass.dao.EquipmentDao;

@Slf4j
@Service
public class YS_Service {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String YS_LOGIN_URL = "http://42.193.14.241:7000/ysapi/subAccount/getToken";
    private static final String YS_CAPTURE_URL = "https://open.ys7.com/api/lapp/device/capture";

    @Autowired
    private Equipment_CaptureDao equipment_CaptureDao;

    @Autowired
    private EquipmentDao equipmentDao;

    public YS_LoginResponseDTO login(String accountId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            String requestBody = String.format("{\"accountId\":\"%s\"}", accountId);
            
            return restTemplate.postForObject(
                YS_LOGIN_URL,
                new HttpEntity<>(requestBody, headers),
                YS_LoginResponseDTO.class
            );
        } catch (Exception e) {
            log.error("萤石云登录失败: {}", e.getMessage());
            throw new RuntimeException("萤石云登录失败: " + e.getMessage());
        }
    }

    public YS_CaptureResponseDTO capture(String accessToken, String deviceSerial, String channelNo, Integer quality) {
        try {
            if (accessToken == null || accessToken.trim().isEmpty()) {
                throw new RuntimeException("accessToken不能为空");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("accessToken", accessToken.trim().replace("\"", ""));
            map.add("deviceSerial", deviceSerial); 
            map.add("channelNo", channelNo);
            if (quality != null) {
                map.add("quality", quality.toString());
            }
            
            log.info("发送抓拍请求，参数：accessToken={}, deviceSerial={}, channelNo={}, quality={}", 
                accessToken, deviceSerial, channelNo, quality);
            
            YS_CaptureResponseDTO response = restTemplate.postForObject(
                YS_CAPTURE_URL,
                new HttpEntity<>(map, headers),
                YS_CaptureResponseDTO.class
            );
            
            if (response == null) {
                throw new RuntimeException("萤石云返回数据为空");
            }
            
            log.info("萤石云响应：{}", response);
            
            // 保存抓拍记录
            if ("200".equals(response.getCode())) {
                Equipment_Capture capture = new Equipment_Capture();
                // 通过deviceSerial查找Device
                Equipment equipment = equipmentDao.findByDeviceSerial(deviceSerial)
                    .orElseThrow(() -> new RuntimeException("设备不存在: " + deviceSerial));
                
                capture.setEquipment(equipment);  // 设置关联的设备
                capture.setPicUrl(response.getData().getPicUrl());
                capture.setCaptureTime(LocalDateTime.now());
                capture.setQuality(quality);
                equipment_CaptureDao.save(capture);
            }
            
            return response;
            
        } catch (Exception e) {
            log.error("萤石云抓拍失败: {}", e.getMessage());
            throw new RuntimeException("萤石云抓拍失败: " + e.getMessage());
        }
    }
} 