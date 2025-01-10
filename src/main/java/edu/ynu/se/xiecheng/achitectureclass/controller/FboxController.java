package edu.ynu.se.xiecheng.achitectureclass.controller;

import edu.ynu.se.xiecheng.achitectureclass.common.dto.TokenResponseDTO;
import edu.ynu.se.xiecheng.achitectureclass.dto.SensorDataDTO;
import edu.ynu.se.xiecheng.achitectureclass.service.FboxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Arrays;
import java.util.Map;

@Api(tags = "Fbox接口")
@RestController
@RequestMapping("/api/fbox")
@CrossOrigin(origins = "http://localhost:5173") // 允许前端访问
public class FboxController {
    private static final Logger logger = LoggerFactory.getLogger(FboxController.class);

    @Autowired
    private FboxService fboxService;

    @ApiOperation("获取Token")
    @PostMapping("/token")
    public TokenResponseDTO getToken() {
        logger.info("正在获取Token");
        return fboxService.getToken();
    }

    @ApiOperation("获取传感器数据")
    @PostMapping("/sensor/data")
    public List<SensorDataDTO> getSensorData(
        @RequestParam String boxNo,
        @RequestBody(required = false) Map<String, List<String>> request) {
        
        try {
            // 如果没有传入ids，使用默认的传感器列表
            List<String> sensorIds = (request != null && request.get("ids") != null) 
                ? request.get("ids")
                : Arrays.asList(
                    "327061375295689615", // 土壤含水率
                    "327061375295689614", // 土壤温度
                    "327061375295689605", // 土壤电导率
                    "327061375295689606", // 土壤PH
                    "327061375295689607", // 土壤氮含量
                    "327061375295689608", // 土壤磷含量
                    "327061375295689609", // 土壤钾含量
                    "327061375295689611", // 空气温度
                    "327061375295689612", // 空气湿度
                    "327061375295689613", // 光照度
                    "327061375295689610"  // CO2
                );
                
            logger.info("从box中获取传感器数据: {}, 传感器: {}", boxNo, sensorIds);
            return fboxService.getSensorData(boxNo, sensorIds);
        } catch (Exception e) {
            logger.error("获取传感器数据失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取传感器数据失败: " + e.getMessage());
        }
    }
} 