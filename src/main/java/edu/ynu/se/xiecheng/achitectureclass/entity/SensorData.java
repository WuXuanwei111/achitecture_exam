package edu.ynu.se.xiecheng.achitectureclass.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@DynamicUpdate
@Where(clause = "is_deleted = 0")
@Table(name = "sensor_data")
public class SensorData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false)
    private Double value;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_serial", nullable = false)
    private Equipment equipment;
    
    @Column(name = "create_time", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createTime;
    
    @Column(name = "update_time")
    @UpdateTimestamp
    private Date updateTime;
    
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
    
    @Column(name = "deleted_time")
    private Date deletedTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        updateTime = new Date();
        isDeleted = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = new Date();
    }
} 