package edu.ynu.se.xiecheng.achitectureclass.dao;

import edu.ynu.se.xiecheng.achitectureclass.service.FboxService;
import edu.ynu.se.xiecheng.achitectureclass.dto.SensorDataDTO;
import edu.ynu.se.xiecheng.achitectureclass.entity.Equipment;
import edu.ynu.se.xiecheng.achitectureclass.entity.DeviceType;
import edu.ynu.se.xiecheng.achitectureclass.entity.SensorData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EquipmentDataTest {

    @Autowired
    private FboxService fboxService;
    
    @Autowired
    private EquipmentDao equipmentDao;
    
    @Autowired
    private SensorDataDao sensorDataDao;

    @Test
    @Transactional
    public void testSaveDeviceAndData() {
        // 1. 创建并保存设备
        Equipment equipment = new Equipment();
        equipment.setName("空气温度检测器");
        equipment.setDeviceSerial("327061375295689611");
        equipment.setDeviceType(DeviceType.AIR_TEMPERATURE);
        equipment.setUnit("℃");
        equipmentDao.save(equipment);

        // 2. 获取传感器数据
        List<String> sensorIds = Collections.singletonList("327061375295689611");
        List<SensorDataDTO> dataList = fboxService.getSensorData("300023040085", sensorIds);

        // 3. 保存传感器数据
        for (SensorDataDTO dto : dataList) {
            SensorData sensorData = new SensorData();
            sensorData.setEquipment(equipment);
            sensorData.setName(dto.getName());
            sensorData.setValue(dto.getValue());
            sensorDataDao.save(sensorData);
        }

        // 4. 验证数据
        List<SensorData> savedData = sensorDataDao.findByEquipment(equipment);
        assertNotNull(savedData);
        assertFalse(savedData.isEmpty());
        System.out.println("Saved sensor data: " + savedData);
    }
} 