package edu.ynu.se.xiecheng.achitectureclass.controller;

import edu.ynu.se.xiecheng.achitectureclass.dto.YS_LoginRequestDTO;
import edu.ynu.se.xiecheng.achitectureclass.dto.YS_LoginResponseDTO;
import edu.ynu.se.xiecheng.achitectureclass.dto.YS_CaptureResponseDTO;
import edu.ynu.se.xiecheng.achitectureclass.service.YS_Service;
import edu.ynu.se.xiecheng.achitectureclass.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ys")
public class YS_Controller {
    private static final Logger log = LoggerFactory.getLogger(YS_Controller.class);

    @Autowired
    private YS_Service ys_Service;

    @PostMapping("/token")
    public ResponseResult<YS_LoginResponseDTO> login(@RequestBody YS_LoginRequestDTO request) {
        try {
            YS_LoginResponseDTO response = ys_Service.login(request.getAccountId());
            return ResponseResult.success(response);
        } catch (Exception e) {
            return ResponseResult.error(e.getMessage());
        }
    }

    @PostMapping("/capture")
    public ResponseResult<YS_CaptureResponseDTO> capture(
            @RequestParam String accessToken,
            @RequestParam String deviceSerial,
            @RequestParam(defaultValue = "1") String channelNo,
            @RequestParam(required = false) Integer quality) {
        try {
            log.info("接收到抓拍请求：token={}, deviceSerial={}, channelNo={}, quality={}", 
                accessToken, deviceSerial, channelNo, quality);
            
            YS_CaptureResponseDTO response = ys_Service.capture(
                accessToken,
                deviceSerial,
                channelNo,
                quality
            );
            
            log.info("抓拍成功：{}", response);
            return ResponseResult.success(response);
        } catch (Exception e) {
            log.error("抓拍失败：{}", e.getMessage());
            return ResponseResult.error(e.getMessage());
        }
    }
} 