package edu.ynu.se.xiecheng.achitectureclass.dao;

import edu.ynu.se.xiecheng.achitectureclass.entity.Equipment;
import edu.ynu.se.xiecheng.achitectureclass.entity.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorDataDao extends JpaRepository<SensorData, Long> {
    // 根据设备查找最新的一条数据
    Optional<SensorData> findFirstByEquipmentOrderByCreateTimeDesc(Equipment equipment);
    
    // 根据设备查找所有数据
    List<SensorData> findByEquipment(Equipment equipment);
}