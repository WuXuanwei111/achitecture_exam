package edu.ynu.se.xiecheng.achitectureclass.dao;

import edu.ynu.se.xiecheng.achitectureclass.entity.Equipment;
import edu.ynu.se.xiecheng.achitectureclass.entity.DeviceType;
import edu.ynu.se.xiecheng.achitectureclass.entity.Greenhouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipmentDao extends JpaRepository<Equipment, String> {
    // 根据设备序列号查找设备
    Optional<Equipment> findByDeviceSerial(String deviceSerial);
    
    //查找大棚下的所有设备
    List<Equipment> findByGreenhouse(Greenhouse greenhouse);
    
    // 检查大棚是否已经绑定了同类型的设备
    boolean existsByGreenhouseAndDeviceType(Greenhouse greenhouse, DeviceType deviceType);
} 