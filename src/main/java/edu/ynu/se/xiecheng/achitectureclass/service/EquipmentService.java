package edu.ynu.se.xiecheng.achitectureclass.service;

import edu.ynu.se.xiecheng.achitectureclass.dao.EquipmentDao;
import edu.ynu.se.xiecheng.achitectureclass.dao.GreenhouseDao;
import edu.ynu.se.xiecheng.achitectureclass.entity.Equipment;
import edu.ynu.se.xiecheng.achitectureclass.entity.Greenhouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EquipmentService {
    private static final Logger logger = LoggerFactory.getLogger(EquipmentService.class);

    @Autowired
    private EquipmentDao equipmentDao;
    
    @Autowired
    private GreenhouseDao greenhouseDao;

    public Equipment bindDeviceToGreenhouse(String deviceSerial, Long greenhouseId) {
        // 获取设备
        Equipment equipment = equipmentDao.findById(deviceSerial)
            .orElseThrow(() -> new RuntimeException("Equipment not found: " + deviceSerial));
            
        // 获取大棚
        Greenhouse greenhouse = greenhouseDao.findById(greenhouseId)
            .orElseThrow(() -> new RuntimeException("Greenhouse not found: " + greenhouseId));
            
        // 检查大棚是否已经绑定了同类型的设备
        if (equipmentDao.existsByGreenhouseAndDeviceType(greenhouse, equipment.getDeviceType())) {
            throw new RuntimeException(String.format(
                "Greenhouse already has a %s equipment",
                equipment.getDeviceType().getDisplayName()
            ));
        }
        
        // 绑定设备到大棚
        equipment.setGreenhouse(greenhouse);
        Equipment savedEquipment = equipmentDao.save(equipment);
        
        logger.info("Equipment {} ({}) bound to greenhouse {}",
            equipment.getDeviceType().getDisplayName(),
            deviceSerial,
            greenhouseId);
            
        return savedEquipment;
    }

    public Equipment unbindDevice(String deviceSerial) {
        Equipment equipment = equipmentDao.findById(deviceSerial)
            .orElseThrow(() -> new RuntimeException("Equipment not found: " + deviceSerial));
            
        Long greenhouseId = equipment.getGreenhouse() != null ? equipment.getGreenhouse().getId() : null;
        equipment.setGreenhouse(null);
        Equipment savedEquipment = equipmentDao.save(equipment);
        
        logger.info("Equipment {} ({}) unbound from greenhouse {}",
            equipment.getDeviceType().getDisplayName(),
            deviceSerial,
            greenhouseId);
            
        return savedEquipment;
    }
} 