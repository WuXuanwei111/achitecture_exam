package edu.ynu.se.xiecheng.achitectureclass.entity;

import edu.ynu.se.xiecheng.achitectureclass.common.entity.LogicEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@DynamicUpdate
@Where(clause = "is_deleted = 0")
@Table(name = "greenhouse")
public class Greenhouse extends LogicEntity {
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 255)
    private String address;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal area;

    // 添加与设备的一对多关系
    @OneToMany(mappedBy = "greenhouse", cascade = CascadeType.ALL)
    @Where(clause = "is_deleted = 0")
    private List<Equipment> equipment;
} 