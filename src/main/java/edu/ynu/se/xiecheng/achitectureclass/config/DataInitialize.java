package edu.ynu.se.xiecheng.achitectureclass.config;

import edu.ynu.se.xiecheng.achitectureclass.dao.EquipmentDao;
import edu.ynu.se.xiecheng.achitectureclass.dao.GreenhouseDao;
import edu.ynu.se.xiecheng.achitectureclass.entity.Equipment;
import edu.ynu.se.xiecheng.achitectureclass.entity.DeviceType;
import edu.ynu.se.xiecheng.achitectureclass.entity.Greenhouse;
import edu.ynu.se.xiecheng.achitectureclass.service.EquipmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class DataInitialize implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitialize.class);

    @Autowired
    private GreenhouseDao greenhouseDao;

    @Autowired
    private EquipmentDao equipmentDao;

    @Autowired
    private EquipmentService equipmentService;

    @Override
    @Transactional
    public void run(String... args) {
        try {
            logger.info("开始数据初始化...");
            initializeData();
            logger.info("数据初始化成功.");
        } catch (Exception e) {
            logger.error("数据初始化失败: " + e.getMessage(), e);
        }
    }

    private void initializeData() {
        // 检查是否已经初始化
        if (greenhouseDao.count() > 0) {
            logger.info("数据已经初始化, 跳过...");
            return;
        }

        logger.info("Creating greenhouse...");
        // 创建大棚
        Greenhouse greenhouse = new Greenhouse();
        greenhouse.setName("智慧大棚001号");
        greenhouse.setAddress("云南大学呈贡校区");
        greenhouse.setArea(new BigDecimal("999.00"));
        greenhouse = greenhouseDao.save(greenhouse);
        logger.info("Greenhouse created with ID: {}", greenhouse.getId());

        logger.info("正在创建设备...");
        // 创建设备
        createDevices(greenhouse);
        logger.info("设备创建成功.");
    }

    private void createDevices(Greenhouse greenhouse) {
        // 使用实际的传感器ID
        createDevice("空气温度检测器", DeviceType.AIR_TEMPERATURE.getDeviceSerial(), 
                DeviceType.AIR_TEMPERATURE, "℃", null, greenhouse);
        createDevice("空气湿度检测器", DeviceType.AIR_HUMIDITY.getDeviceSerial(), 
                DeviceType.AIR_HUMIDITY, "%", null, greenhouse);
        createDevice("土壤温度检测器", DeviceType.SOIL_TEMPERATURE.getDeviceSerial(), 
                DeviceType.SOIL_TEMPERATURE, "℃", null, greenhouse);
        createDevice("土壤PH检测器", DeviceType.SOIL_PH.getDeviceSerial(), 
                DeviceType.SOIL_PH, "pH", null, greenhouse);
        createDevice("二氧化碳检测器", DeviceType.CO2.getDeviceSerial(), 
                DeviceType.CO2, "ppm", null, greenhouse);

        // 只保留一个摄像头
        createDevice("监控摄像头", "AB9831171", DeviceType.CAMERA, null, 
                "ezopen://open.ys7.com/AB9831171/1.hd.live", greenhouse);
    }

    private void createDevice(String name, String serial, DeviceType type, String unit, 
                            String monitorUrl, Greenhouse greenhouse) {
        try {
            // 检查设备是否已存在
            if (equipmentDao.findById(serial).isPresent()) {
                logger.info("Equipment with serial {} already exists, skipping...", serial);
                return;
            }

            // 创建设备
            Equipment equipment = new Equipment();
            equipment.setName(name);
            equipment.setDeviceSerial(serial);
            equipment.setDeviceType(type);
            equipment.setUnit(unit);
            equipment.setMonitorUrl(monitorUrl);
            
            // 保存设备
            equipment = equipmentDao.save(equipment);
            logger.info("Equipment created with serial: {}", equipment.getDeviceSerial());
            
            // 绑定设备到大棚
            try {
                equipment = equipmentService.bindDeviceToGreenhouse(equipment.getDeviceSerial(), greenhouse.getId());
                logger.info("设备已绑定到大棚: {}", equipment.getDeviceSerial());
            } catch (Exception e) {
                logger.error("Failed to bind equipment to greenhouse: {} - {}", serial, e.getMessage());
                // 如果绑定失败，不影响设备创建
            }
            
        } catch (Exception e) {
            logger.error("设备创建失败: {} - {}", serial, e.getMessage());
        }
    }
} 