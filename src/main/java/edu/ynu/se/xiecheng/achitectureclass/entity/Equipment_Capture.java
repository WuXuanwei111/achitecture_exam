package edu.ynu.se.xiecheng.achitectureclass.entity;

import edu.ynu.se.xiecheng.achitectureclass.common.entity.LogicEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "device_capture")
@EqualsAndHashCode(callSuper = true)
public class Equipment_Capture extends LogicEntity {
    
    // 所属设备ID（外键）
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_serial", nullable = false)
    private Equipment equipment;
    
    // 抓拍图片URL（照片）
    @Column(nullable = false, length = 512)
    private String picUrl;
    
    // 抓拍时间
    @Column(nullable = false)
    private LocalDateTime captureTime;
    
    // 图片质量 0-流畅,1-高清,2-4CIF,3-1080P,4-400w
    @Column
    private Integer quality;
} 