# 课程代码

#### 介绍
用于《软件设计与体系结构》课程


#### 安装教程

1.  打开IDEA
2.  点击运行ArchitectureClassApplication.java

#### 使用说明
1. 系统架构:
- 使用 Spring Boot + JPA
- 支持多种传感器数据采集
- 包含设备管理、大棚管理、数据采集等功能

2. 主要功能模块:
- 设备管理 (Device)
- 大棚管理 (Greenhouse)
- 传感器数据采集 (SensorData)
- 摄像头图像抓取 (DeviceCapture)

3. 关键服务:
- FboxService: 处理Fbox平台的传感器数据采集
- YsService: 处理萤石云摄像头的登录和图像抓取
- DeviceService: 设备与大棚绑定/解绑

常用接口:
- `/api/fbox/sensor/data`: 获取传感器数据
- `/api/ys/capture`: 摄像头抓图
- `/api/fbox/token`: 获取平台Token
