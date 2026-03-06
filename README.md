# Spring Statemachine 最佳实践模板项目

## 项目概述

基于 **订单状态管理** 业务场景，构建的功能完备的 Spring Statemachine 模板项目。

## 技术栈

| 技术栈 | 版本 |
|--------|------|
| Java | 17 |
| Spring Boot | 3.2.4 |
| Spring Statemachine | 4.0.0 |
| MySQL | 8.0+ |
| MyBatis-Plus | 3.5.5 |
| Redis | 7.x |
| Knife4j | 4.3.0 (OpenAPI 3) |
| Redisson | 3.25.0 |
| Hutool | 5.8.25 |

## 快速启动

### 1. 启动依赖服务

```bash
docker-compose up -d
```

### 2. 初始化数据库

```bash
mysql -h localhost -u root -proot123 < sql/schema.sql
mysql -h localhost -u root -proot123 statemachine_demo < sql/data.sql
```

### 3. 启动应用

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

访问 API 文档：http://localhost:8080/doc.html

## 订单状态流转

```
CREATED --[SUBMIT]--> PENDING --[PAY]--> PAID --[SHIP]--> SHIPPED --[RECEIVE]--> RECEIVED --[COMPLETE]--> COMPLETED
  |                     |                  |                                          |
  |[CANCEL]         [CANCEL/TIMEOUT]    [REFUND]                                  [RETURN]
  v                     v                  v                                          v
CANCELLED            CANCELLED/CLOSED   REFUNDING                                  CLOSED
                                          └─ REFUND_PENDING
                                               ├─[REFUND_APPROVE]─> REFUND_APPROVED ─[COMPLETE]─> CLOSED
                                               └─[REFUND_REJECT]──> REFUND_REJECTED ─[CANCEL]───> PAID
```

## 状态定义

| 状态 | 枚举值 | 编码 | 说明 |
|------|--------|------|------|
| CREATED | CREATED | 10 | 订单已创建 |
| PENDING | PENDING | 20 | 待支付 |
| PAID | PAID | 30 | 已支付 |
| SHIPPED | SHIPPED | 40 | 已发货 |
| RECEIVED | RECEIVED | 50 | 已签收 |
| COMPLETED | COMPLETED | 60 | 已完成 |
| CANCELLED | CANCELLED | 70 | 已取消 |
| CLOSED | CLOSED | 80 | 已关闭 |
| REFUNDING | REFUNDING | 90 | 退款中（父状态） |
| REFUND_PENDING | REFUND_PENDING | 91 | 退款待审核（子状态） |
| REFUND_APPROVED | REFUND_APPROVED | 92 | 退款已批准（子状态） |
| REFUND_REJECTED | REFUND_REJECTED | 93 | 退款已拒绝（子状态） |

## API 接口

### 订单管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/v1/orders | 创建订单 |
| GET | /api/v1/orders/{orderId} | 查询订单详情 |
| GET | /api/v1/orders | 分页查询（支持 state/userId 筛选） |
| GET | /api/v1/orders/{orderId}/available-events | 当前可触发事件列表 |

### 事件触发

| 方法 | 路径 | 事件 |
|------|------|------|
| POST | /api/v1/orders/{orderId}/events/submit | 提交订单 |
| POST | /api/v1/orders/{orderId}/events/pay | 支付 |
| POST | /api/v1/orders/{orderId}/events/cancel | 取消 |
| POST | /api/v1/orders/{orderId}/events/ship | 发货 |
| POST | /api/v1/orders/{orderId}/events/receive | 签收 |
| POST | /api/v1/orders/{orderId}/events/complete | 完成 |
| POST | /api/v1/orders/{orderId}/events/refund | 申请退款 |
| POST | /api/v1/orders/{orderId}/events/refund-approve | 退款审批通过 |
| POST | /api/v1/orders/{orderId}/events/refund-reject | 退款审批拒绝 |
| POST | /api/v1/orders/{orderId}/events | 通用事件触发 |

### 历史与溯源

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/v1/orders/{orderId}/state-history | 状态变更历史 |
| GET | /api/v1/orders/{orderId}/event-logs | 事件处理记录 |
| POST | /api/v1/orders/{orderId}/rollback | 状态回滚 |

### 状态机管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/v1/state-machine/definition | 获取状态机定义 |
| GET | /api/v1/state-machine/{machineId}/snapshot | 状态机快照 |
| POST | /api/v1/state-machine/{machineId}/reset | 重置状态机 |
| GET | /api/v1/state-machine/health | 健康检查 |

### 统计分析

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/v1/statistics/state-distribution | 状态分布统计 |
| GET | /api/v1/statistics/event-counts | 事件触发统计 |

## 项目结构

```
com.yubzhou.statemachine
├── config/                         # 配置层
│   ├── properties/                 # 配置属性类
│   ├── OrderStateMachineConfig.java # 状态机核心配置
│   ├── RedisConfig.java
│   ├── RedissonConfig.java
│   ├── MybatisPlusConfig.java
│   └── Knife4jConfig.java
│
├── statemachine/                   # 状态机核心
│   ├── enums/                      # OrderState, OrderEvent
│   ├── interceptor/                # 状态持久化拦截器
│   ├── listener/                   # 状态机监听器
│   ├── handler/                    # 事件处理器
│   ├── constant/                   # 常量定义
│   └── service/                    # 状态机服务
│
├── order/                          # 订单业务模块
│   ├── entity/                     # Order, OrderStateHistory, OrderEventLog
│   ├── dto/                        # 请求/响应 DTO
│   ├── mapper/                     # MyBatis-Plus Mapper
│   ├── service/                    # 业务服务
│   ├── controller/                 # REST 控制器
│   └── converter/                  # 对象转换器
│
├── management/                     # 状态机管理模块
├── statistics/                     # 统计分析模块
├── scheduler/                      # 定时任务模块
│
└── common/                         # 公共模块
    ├── result/                     # 统一响应 Result<T>
    ├── exception/                  # 异常处理
    ├── annotation/                 # @DistributedLock
    ├── aspect/                     # AOP 切面
    ├── util/                       # 工具类
    └── constant/                   # 常量
```

## 定时任务

| 任务 | 周期 | 说明 |
|------|------|------|
| 支付超时关闭 | 每分钟 | 自动关闭超时未支付订单 |
| 自动确认收货 | 每小时 | 发货超15天自动签收 |
| 事件日志清理 | 每天凌晨2点 | 清理90天以前的事件日志 |

## 核心特性

- ✅ Spring Statemachine 4.0.0 状态机工厂模式
- ✅ 层次状态机（退款子状态机）
- ✅ 状态持久化拦截器
- ✅ 状态机监听器
- ✅ Redisson 分布式锁（`@DistributedLock` 注解）
- ✅ MyBatis-Plus + 乐观锁
- ✅ 完整的状态变更历史记录
- ✅ 事件处理日志（ACCEPTED/DENIED/DEFERRED）
- ✅ 状态回滚（最近5条）
- ✅ Knife4j API 文档
- ✅ 统一异常处理
- ✅ 定时任务（可配置开关）
