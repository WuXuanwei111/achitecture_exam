package edu.ynu.se.xiecheng.achitectureclass.service;

import edu.ynu.se.xiecheng.achitectureclass.common.dto.TokenResponseDTO;
import edu.ynu.se.xiecheng.achitectureclass.dto.SensorDataDTO;
import edu.ynu.se.xiecheng.achitectureclass.dao.EquipmentDao;
import edu.ynu.se.xiecheng.achitectureclass.dao.SensorDataDao;
import edu.ynu.se.xiecheng.achitectureclass.entity.Equipment;
import edu.ynu.se.xiecheng.achitectureclass.entity.DeviceType;
import edu.ynu.se.xiecheng.achitectureclass.entity.SensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.scheduling.annotation.Async;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class FboxService {
    private static final Logger logger = LoggerFactory.getLogger(FboxService.class);
    
    private static final String CLIENT_ID = "68db2e8bda8d47b5b5db7eaf71c7dcdd";
    private static final String CLIENT_SECRET = "76dc724c95004acab25482d344dab407";
    
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private EquipmentDao equipmentDao;
    @Autowired
    private SensorDataDao sensorDataDao;

    private String cachedToken = null;
    private long tokenExpireTime = 0;

    public TokenResponseDTO getToken() {
        // 检查缓存的token是否还有效（提前5分钟刷新）
        if (isTokenValid()) {
            return createCachedTokenResponse();
        }
        return requestNewToken();
    }

    private boolean isTokenValid() {
        return cachedToken != null && System.currentTimeMillis() < tokenExpireTime - 300_000L;
    }

    private TokenResponseDTO createCachedTokenResponse() {
        TokenResponseDTO cachedResponse = new TokenResponseDTO();
        cachedResponse.setAccessToken(cachedToken);
        cachedResponse.setExpiresIn(7200);
        cachedResponse.setTokenType("Bearer");
        cachedResponse.setScope("fbox");
        return cachedResponse;
    }

    private TokenResponseDTO requestNewToken() {
        String url = "https://fbox360.com/idserver/core/connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("scope", "fbox");
        map.add("client_id", CLIENT_ID);
        map.add("client_secret", CLIENT_SECRET);
        map.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<TokenResponseDTO> response = restTemplate.postForEntity(
                    url, request, TokenResponseDTO.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                TokenResponseDTO tokenResponse = response.getBody();
                updateTokenCache(tokenResponse);
                return tokenResponse;
            }
            throw new RuntimeException("Failed to get token: " + response.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get token: " + e.getMessage());
        }
    }

    private void updateTokenCache(TokenResponseDTO tokenResponse) {
        cachedToken = tokenResponse.getAccessToken();
        long expiresInMillis = (long) (tokenResponse.getExpiresIn() - 300) * 1000L;
        tokenExpireTime = System.currentTimeMillis() + expiresInMillis;
    }

    public List<SensorDataDTO> getSensorData(String boxNo, List<String> sensorIds) {
        logger.info("Getting sensor data for box: {}, sensors: {}", boxNo, sensorIds);
        try {
            String url = "https://fbox360.com/api/v2/dmon/value/get?boxNo=" + boxNo;
            
            // 获取token
            TokenResponseDTO tokenResponse = getToken();
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(tokenResponse.getAccessToken());

            // 设置请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("ids", sensorIds);

            // 创建请求实体
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // 发送POST请求
            ResponseEntity<List<SensorDataDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<List<SensorDataDTO>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<SensorDataDTO> sensorDataList = response.getBody();
                
                // 处理特殊传感器数据
                for (SensorDataDTO data : sensorDataList) {
                    // 空气温度和空气湿度需要除以10
                    if (data.getId().equals("327061375295689611") ||    // 空气温度
                        data.getId().equals("327061375295689612")) {    // 空气湿度
                        data.setValue(data.getValue() / 10.0);
                    }
                }
                
                logger.info("Received sensor data: {}", sensorDataList);
                
                // 异步保存数据到数据库
                saveSensorDataAsync(sensorDataList);
                
                return sensorDataList;
            }
            
            throw new RuntimeException("Failed to get sensor data: " + response.getStatusCode());
        } catch (Exception e) {
            logger.error("Failed to get sensor data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get sensor data: " + e.getMessage());
        }
    }

    @Async
    public void saveSensorDataAsync(List<SensorDataDTO> sensorDataList) {
        try {
            for (SensorDataDTO data : sensorDataList) {
                if (data.getId() != null) {
                    // 获取或创建设备
                    Equipment equipment = equipmentDao.findByDeviceSerial(data.getId())
                        .orElseGet(() -> createDevice(data));
                        
                    // 使用统一的保存方法    
                    saveSensorData(data, equipment);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to save sensor data: {}", e.getMessage(), e);
        }
    }

    private Equipment createDevice(SensorDataDTO data) {
        try {
            Equipment newEquipment = new Equipment();
            newEquipment.setDeviceSerial(data.getId());
            newEquipment.setName(data.getName());
            
            // 使用fromSerial方法获取设备类型
            DeviceType type = DeviceType.fromSerial(data.getId());
            newEquipment.setDeviceType(type);

            // 设置单位
            switch (type) {
                case AIR_TEMPERATURE:
                case SOIL_TEMPERATURE:
                    newEquipment.setUnit("℃");
                    break;
                case AIR_HUMIDITY:
                    newEquipment.setUnit("%RH");
                    break;
                case SOIL_PH:
                    newEquipment.setUnit("pH");
                    break;
                case CO2:
                    newEquipment.setUnit("ppm");
                    break;
                case CAMERA:
                    newEquipment.setUnit("");
                    break;
            }
            
            logger.info("Creating new device: {}", newEquipment);
            return equipmentDao.save(newEquipment);
        } catch (Exception e) {
            logger.error("Failed to create device: {} - {}", data, e.getMessage());
            throw new RuntimeException("Failed to create device: " + e.getMessage());
        }
    }

    private void saveSensorData(SensorDataDTO data, Equipment equipment) {
        try {
            // 创建新的传感器数据记录
            SensorData sensorData = new SensorData();
            sensorData.setEquipment(equipment);
            sensorData.setName(data.getName());
            sensorData.setValue(data.getValue());
            
            // 检查数值是否在合理范围内
            if (equipment.getDeviceType() != DeviceType.CAMERA) {
                validateSensorValue(data, equipment.getDeviceType());
            }
            
            // 保存新记录，@PrePersist会自动设置createTime和updateTime
            SensorData saved = sensorDataDao.save(sensorData);
            logger.info("Successfully saved new sensor data: {} at time: {}", 
                saved, saved.getCreateTime());
            
        } catch (Exception e) {
            logger.error("Failed to save sensor data: {} - {}", data, e.getMessage());
            throw new RuntimeException("Failed to save sensor data: " + e.getMessage());
        }
    }

    private void validateSensorValue(SensorDataDTO data, DeviceType type) {
        switch (type) {
            case AIR_TEMPERATURE:
            case SOIL_TEMPERATURE:
                if (data.getValue() < -40 || data.getValue() > 80) {
                    throw new RuntimeException("Temperature out of range (-40℃ to 80℃): " + data.getValue());
                }
                break;
            case AIR_HUMIDITY:
                if (data.getValue() < 0 || data.getValue() > 100) {
                    throw new RuntimeException("Humidity out of range (0% to 100%): " + data.getValue());
                }
                break;
            case SOIL_PH:
                if (data.getValue() < 0 || data.getValue() > 14) {
                    throw new RuntimeException("PH out of range (0 to 14): " + data.getValue());
                }
                break;
            case CO2:
                if (data.getValue() < 0 || data.getValue() > 5000) {
                    throw new RuntimeException("CO2 out of range (0 to 5000 ppm): " + data.getValue());
                }
                break;
        }
    }
} 