# CLAUDE.md

## 项目概述

瑞吉外卖 (Reggie Take-out) — 基于 Spring Boot 3 + MyBatis-Plus 的餐厅管理系统学习项目。包含管理后台（Element UI）和移动端（Vant）前端。

## 技术栈

- **Java 17**, **Spring Boot 3.2.5**, **Spring Security** (PasswordEncoder)
- **MyBatis-Plus 3.5.5** (`mybatis-plus-spring-boot3-starter`)
- **MySQL** + **Redis** + **Druid 1.2.20** 连接池
- **Lombok**, **fastjson 1.2.83**, **commons-lang3**
- **JUnit 5** (Spring Boot Test)

## "已检查，书写正确" 规则

当 Java 文件（类、方法、或代码块）的上方出现注释 `// 已检查，书写正确` 时，表示该段代码已经过 AI 审查但未经过人工测试。你**不需要**：
- 再次检查该段代码的正确性
- 对该段代码提出修改建议
- 对该段代码进行重构

你**仍然可以**：
- 在该段代码的上下文中添加新代码
- 引用该段代码中的类、方法、字段
- 在该文件的其他未标记部分进行修改

检查时，忽略 spring Cache 部分的问题

**重要**: 不要删除已有 `// 已检查，书写正确` 注释，除非对应的代码被实质性修改。

## 项目结构

```
src/main/java/com/example/javaPractice/
├── JavaPracticeApplication.java   # 主入口（@MapperScan + @ServletComponentScan）
├── Config/                        # 配置类
│   ├── BaseContext.java           # ThreadLocal 用户ID存储
│   ├── JacksonObjectMapper.java   # JSON序列化配置
│   ├── MybatisPlusConfig.java     # 分页插件
│   ├── MyMetaObjecthandler.java   # 自动填充 createTime/updateTime
│   ├── RedisConfig.java           # Redis 序列化配置
│   └── WebMvcConfig.java          # 静态资源映射 + 消息转换器
├── common/                        # 公共组件
│   ├── CustomException.java       # 自定义业务异常
│   └── GlobalExceptionHandler.java # 全局异常处理
├── Entity/                        # 实体类（11个表实体 + R<T> 通用响应）
├── dto/                           # 数据传输对象（DishDto, SetmealDto）
├── mapper/                        # MyBatis-Plus Mapper 接口
├── Service/                       # Service 接口 + Impl 实现
├── Controller/                    # REST 控制器（10个模块）
├── filter/                        # 登录拦截过滤器
└── utils/                         # 工具类（验证码生成、短信服务）
```

## API 文档

完整的 API 接口文档位于 `src/main/resources/API接口文档.md`，包含所有接口的请求参数、返回值和功能说明。修改 Controller 后请同步更新该文档。

## 当前项目状态

本项目是一个**学习模板**——所有 Controller 和 ServiceImpl 的方法体已被清空（只保留方法签名和 `return null`），供逐步实现业务逻辑使用。框架层代码（Config、common、Entity、Mapper、Service接口、Filter、Utils）已完整保留并审查通过。

## 关键约定

- 统一响应格式：`R<T>`（code=1 成功，code=0 失败）
- 实体类 ID 类型为 Long，序列化为 JSON 时转为 String（避免 JS 精度丢失）
- 时间格式：`yyyy-MM-dd HH:mm:ss`
- 数据库时间字段由 MyBatis-Plus MetaObjectHandler 自动填充
- 静态资源映射：`/backend/**` → `classpath:/backend/`，`/front/**` → `classpath:/front/`
- 文件上传目录：`images/`（相对于项目根目录）
