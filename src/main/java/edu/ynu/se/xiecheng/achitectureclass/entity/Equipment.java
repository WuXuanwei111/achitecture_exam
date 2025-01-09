package edu.ynu.se.xiecheng.achitectureclass.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "device", 
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"greenhouse_id", "device_type_name"},
            name = "uk_greenhouse_device_type"
        ),
        @UniqueConstraint(
            columnNames = {"device_serial"},
            name = "uk_device_serial"
        )
    }
)
public class Equipment {
    /**
     * 设备序列号作为主键
     * 对于摄像头设备：使用萤石云提供的设备序列号
     * 对于土壤检测设备：使用传感器的唯一ID
     */
    @Id
    @Column(name = "device_serial", length = 32)
    private String deviceSerial;

    // 设备名称
    @Column(nullable = false)
    private String name;
    
    // 设备分类（摄像头、氮检测器、温度检测器等）
    @Enumerated(EnumType.STRING)
    @Column(name = "device_type_name", nullable = false)
    private DeviceType deviceType;
    
    // 数据单位（如：%、℃，摄像头为null）
    @Column
    private String unit;
    
    // 监控地址（仅摄像头设备使用）
    @Column(name = "monitor_url", length = 512)
    private String monitorUrl;
    
    // 所属大棚（可以为空，表示设备未绑定）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "greenhouse_id")
    private Greenhouse greenhouse;
    
    @Column(name = "create_time", updatable = false)
    private Date createTime;
    
    @Column(name = "update_time")
    private Date updateTime;
    
    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }
} 