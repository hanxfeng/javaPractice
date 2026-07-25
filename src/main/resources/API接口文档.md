# 瑞吉外卖 API 接口文档

---

## 目录

1. [员工管理 - /employee](#1-员工管理---employee)
2. [分类管理 - /category](#2-分类管理---category)
3. [菜品管理 - /dish](#3-菜品管理---dish)
4. [套餐管理 - /setmeal](#4-套餐管理---setmeal)
5. [订单管理 - /order](#5-订单管理---order)
6. [订单明细 - /orderDetail](#6-订单明细---orderdetail)
7. [购物车 - /shoppingCart](#7-购物车---shoppingcart)
8. [地址簿 - /addressBook](#8-地址簿---addressbook)
9. [用户管理 - /user](#9-用户管理---user)
10. [文件上传下载 - /common](#10-文件上传下载---common)
11. [0](#11-登录拦截机制)

---

## 1. 员工管理 - /employee

### 1.1 员工登录

- **URL**: `POST /employee/login`
- **Content-Type**: `application/json`
- **请求参数**:

```json
{
  "name": "管理员",
  "password": "123456"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 员工姓名（用于登录） |
| password | String | 是 | 密码（明文传输，后端使用 BCrypt 加密比对） |

- **返回结果**:

成功时：

```json
{
  "code": 1,
  "msg": null,
  "data": {
    "id": "1",
    "username": "admin",
    "name": "管理员",
    "phone": "13800000000",
    "sex": "0",
    "idNumber": "110101199001010001",
    "status": 1,
    "createTime": "2024-01-01 10:00:00",
    "updateTime": "2024-01-01 10:00:00",
    "createUser": null,
    "updateUser": null
  }
}
```

失败时：

```json
{
  "code": 0,
  "msg": "登陆失败，该用户不存在",
  "data": null
}
```

- **功能说明**: 员工使用姓名和密码登录管理后台。密码使用 Spring Security 的 `PasswordEncoder`（BCrypt）进行加密比对。登录成功后将员工 ID 以 `userId` 为 Key 存入 Session。可能返回的错误信息：
  - `"登陆失败，该用户不存在"` — 姓名不存在
  - `"登陆失败，密码错误"` — 密码不匹配
  - `"登陆失败，该账号已被禁用"` — 员工状态 status=0

---

### 1.2 员工退出登录

- **URL**: `POST /employee/logout`
- **请求参数**: 无（Session 自动传递）
- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": "退出成功"
}
```

- **功能说明**: 销毁当前 Session，清除所有登录状态信息。

---

### 1.3 新增员工

- **URL**: `POST /employee`
- **Content-Type**: `application/json`
- **请求参数**:

```json
{
  "name": "张三",
  "username": "zhangsan",
  "phone": "13812345678",
  "sex": "1",
  "idNumber": "110101199001010001"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 员工姓名 |
| username | String | 是 | 登录用户名 |
| phone | String | 是 | 手机号 |
| sex | String | 是 | 性别（"0"=女, "1"=男） |
| idNumber | String | 是 | 身份证号 |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": "新增员工成功"
}
```

- **功能说明**: 新增员工账号，系统自动设置初始密码（需使用 BCrypt 加密后存储）。创建时间和更新时间由 MyBatis-Plus 自动填充。

---

### 1.4 分页查询员工

- **URL**: `GET /employee/page`
- **请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 是 | 当前页码（从1开始） |
| pageSize | int | 是 | 每页记录数 |
| name | String | 否 | 按员工姓名模糊查询 |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": {
    "records": [
      {
        "id": "1",
        "name": "管理员",
        "username": "admin",
        "phone": "13800000000",
        "sex": "0",
        "idNumber": "110101199001010001",
        "status": 1,
        "createTime": "2024-01-01 10:00:00",
        "updateTime": "2024-01-01 10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

- **功能说明**: 分页查询所有员工，按更新时间降序排列。支持通过 name 参数进行模糊搜索。

---

### 1.5 修改员工信息

- **URL**: `PUT /employee`
- **Content-Type**: `application/json`
- **请求参数**:

```json
{
  "id": "1",
  "name": "管理员",
  "username": "admin",
  "phone": "13800000000",
  "sex": "0",
  "idNumber": "110101199001010001",
  "status": 1
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 员工ID |
| name | String | 否 | 姓名 |
| username | String | 否 | 用户名 |
| phone | String | 否 | 手机号 |
| sex | String | 否 | 性别 |
| idNumber | String | 否 | 身份证号 |
| status | Integer | 否 | 状态（0=禁用, 1=启用） |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": "员工信息修改成功"
}
```

- **功能说明**: 根据员工 ID 更新员工信息。更新时间自动填充。

---

### 1.6 根据ID查询员工

- **URL**: `GET /employee/{id}`
- **请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 员工ID（路径参数） |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": {
    "id": "1",
    "name": "管理员",
    "username": "admin",
    "phone": "13800000000",
    "sex": "0",
    "idNumber": "110101199001010001",
    "status": 1,
    "createTime": "2024-01-01 10:00:00",
    "updateTime": "2024-01-01 10:00:00"
  }
}
```

- **功能说明**: 根据员工 ID 查询单个员工信息。

---

## 2. 分类管理 - /category

### 2.1 新增分类

- **URL**: `POST /category`
- **Content-Type**: `application/json`
- **请求参数**:

```json
{
  "type": 1,
  "name": "热销菜品",
  "sort": 1
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | Integer | 是 | 分类类型（1=菜品分类, 2=套餐分类） |
| name | String | 是 | 分类名称 |
| sort | Integer | 否 | 排序值（数值越小越靠前） |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": "新增分类成功"
}
```

- **功能说明**: 创建新的菜品或套餐分类。创建时间自动填充。

---

### 2.2 分页查询分类

- **URL**: `GET /category/page`
- **请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 是 | 当前页码 |
| pageSize | int | 是 | 每页记录数 |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": {
    "records": [
      {
        "id": "1",
        "type": 1,
        "name": "热销菜品",
        "sort": 1,
        "createTime": "2024-01-01 10:00:00",
        "updateTime": "2024-01-01 10:00:00",
        "createUser": null,
        "updateUser": null
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

- **功能说明**: 分页查询所有分类，按 sort 字段降序排列。

---

### 2.3 删除分类

- **URL**: `DELETE /category`
- **请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 分类ID（Query参数） |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": "删除成功"
}
```

关联失败时：

```json
{
  "code": 0,
  "msg": "该分类关联菜品或套餐，请取消关联后再试",
  "data": null
}
```

- **功能说明**: 删除指定分类。删除前需检查该分类下是否关联了菜品或套餐，如果存在关联则不允许删除并抛出业务异常。

---

### 2.4 修改分类

- **URL**: `PUT /category`
- **Content-Type**: `application/json`
- **请求参数**:

```json
{
  "id": "1",
  "type": 1,
  "name": "热销菜品",
  "sort": 2
}
```

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": "修改成功"
}
```

- **功能说明**: 根据分类 ID 更新分类信息。

---

### 2.5 条件查询分类列表

- **URL**: `GET /category/list`
- **请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | Integer | 否 | 分类类型（1=菜品分类, 2=套餐分类），不传则查询所有 |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": [
    {
      "id": "1",
      "type": 1,
      "name": "热销菜品",
      "sort": 1,
      "createTime": "2024-01-01 10:00:00",
      "updateTime": "2024-01-01 10:00:00"
    },
    {
      "id": "2",
      "type": 1,
      "name": "凉菜",
      "sort": 2,
      "createTime": "2024-01-01 10:00:00",
      "updateTime": "2024-01-01 10:00:00"
    }
  ]
}
```

- **功能说明**: 用于移动端或管理端下拉列表，根据 type 筛选菜品或套餐分类。按 sort 升序、updateTime 降序排列。

---

## 3. 菜品管理 - /dish

### 3.1 新增菜品

- **URL**: `POST /dish`
- **Content-Type**: `application/json`
- **请求参数**:

```json
{
  "name": "宫保鸡丁",
  "categoryId": "1",
  "price": 2800,
  "code": "",
  "image": "abc123.jpg",
  "description": "经典川菜，麻辣鲜香",
  "status": 1,
  "sort": 1,
  "flavors": [
    {
      "name": "辣度",
      "value": "[\"微辣\",\"中辣\",\"重辣\"]"
    },
    {
      "name": "温度",
      "value": "[\"热\"]"
    }
  ]
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 菜品名称 |
| categoryId | Long | 是 | 所属分类ID |
| price | Integer | 是 | 价格（单位：分） |
| code | String | 否 | 菜品编码 |
| image | String | 否 | 菜品图片文件名 |
| description | String | 否 | 菜品描述 |
| status | Integer | 否 | 售卖状态（0=停售, 1=起售），默认1 |
| sort | Integer | 否 | 排序 |
| flavors | List\<DishFlavor\> | 否 | 口味列表 |

**DishFlavor 对象**:

| 字段 | 类型 | 说明 |
|------|------|------|
| name | String | 口味名称（如"辣度"、"温度"） |
| value | String | 口味选项JSON数组字符串 |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": "新增菜品成功"
}
```

- **功能说明**: 新增菜品基本信息到 `dish` 表，同时批量插入口味数据到 `dish_flavor` 表。新增成功后清除 Redis 中对应分类的菜品缓存。

---

### 3.2 分页查询菜品

- **URL**: `GET /dish/page`
- **请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 是 | 当前页码 |
| pageSize | int | 是 | 每页记录数 |
| name | String | 否 | 按菜品名称模糊查询 |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": {
    "records": [
      {
        "id": "1",
        "name": "宫保鸡丁",
        "categoryId": "1",
        "categoryName": "热销菜品",
        "price": 2800,
        "code": "",
        "image": "abc123.jpg",
        "description": "经典川菜，麻辣鲜香",
        "status": 1,
        "sort": 1,
        "createTime": "2024-01-01 10:00:00",
        "updateTime": "2024-01-01 10:00:00",
        "flavors": null,
        "copies": null
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

- **功能说明**: 分页查询菜品，返回的 DTO 中包含关联的 `categoryName`（通过 categoryId 查询分类表获取）。按更新时间降序排列。

---

### 3.3 根据ID查询菜品

- **URL**: `GET /dish/{id}`
- **请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 菜品ID（路径参数） |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": {
    "id": "1",
    "name": "宫保鸡丁",
    "categoryId": "1",
    "price": 2800,
    "status": 1,
    "flavors": [
      {
        "id": "1",
        "dishId": "1",
        "name": "辣度",
        "value": "[\"微辣\",\"中辣\",\"重辣\"]"
      }
    ]
  }
}
```

- **功能说明**: 查询菜品基本信息及其关联的口味列表。

---

### 3.4 修改菜品

- **URL**: `PUT /dish`
- **Content-Type**: `application/json`
- **请求参数**: 同 [3.1 新增菜品](#31-新增菜品)，需额外传入 `id` 字段

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": "修改菜品成功"
}
```

- **功能说明**: 更新菜品基本信息，删除原有口味数据后重新插入新的口味数据。更新成功后清除 Redis 中对应分类的菜品缓存。

---

### 3.5 条件查询菜品列表

- **URL**: `GET /dish/list`
- **请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| categoryId | Long | 否 | 所属分类ID |
| status | Integer | 否 | 售卖状态（1=起售），移动端通常传1 |
| name | String | 否 | 菜品名称 |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": [
    {
      "id": "1",
      "name": "宫保鸡丁",
      "categoryId": "1",
      "categoryName": "热销菜品",
      "price": 2800,
      "image": "abc123.jpg",
      "description": "经典川菜，麻辣鲜香",
      "status": 1,
      "flavors": [
        {
          "id": "1",
          "dishId": "1",
          "name": "辣度",
          "value": "[\"微辣\",\"中辣\",\"重辣\"]"
        }
      ]
    }
  ]
}
```

- **功能说明**: 移动端查询菜品列表，使用 Redis 缓存提高性能。缓存 Key 格式：`dish_{categoryId}_{status}`，缓存有效期为 1 小时。优先从 Redis 获取数据，缓存未命中时查询数据库并写入缓存。

---

### 3.6 删除菜品

- **URL**: `DELETE /dish`
- **请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | Long | 是 | 菜品ID（Query参数） |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": "删除菜品成功"
}
```

- **功能说明**: 根据菜品 ID 删除单个菜品。若 ID 为空则返回错误；若菜品不存在则返回删除失败。

---

## 4. 套餐管理 - /setmeal

### 4.1 新增套餐

- **URL**: `POST /setmeal`
- **Content-Type**: `application/json`
- **请求参数**:

```json
{
  "name": "超值双人餐",
  "categoryId": "2",
  "price": 8800,
  "status": 1,
  "code": "",
  "description": "双人豪华套餐",
  "image": "setmeal001.jpg",
  "setmealDishes": [
    {
      "dishId": "1",
      "name": "宫保鸡丁",
      "price": 2800,
      "copies": 1,
      "sort": 1
    },
    {
      "dishId": "2",
      "name": "鱼香肉丝",
      "price": 2600,
      "copies": 2,
      "sort": 2
    }
  ]
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 套餐名称 |
| categoryId | Long | 是 | 所属分类ID（必须是套餐分类 type=2） |
| price | Integer | 是 | 套餐价格（单位：分） |
| status | Integer | 否 | 售卖状态（0=停售, 1=起售） |
| code | String | 否 | 套餐编码 |
| description | String | 否 | 套餐描述 |
| image | String | 否 | 套餐图片文件名 |
| setmealDishes | List\<SetmealDish\> | 是 | 套餐包含的菜品列表 |

**SetmealDish 对象**:

| 字段 | 类型 | 说明 |
|------|------|------|
| dishId | Long | 菜品ID |
| name | String | 菜品名称（冗余） |
| price | Integer | 菜品单价（冗余） |
| copies | Integer | 份数 |
| sort | Integer | 排序 |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": "新增套餐成功"
}
```

- **功能说明**: 新增套餐基本信息到 `setmeal` 表，同时批量插入套餐-菜品关联数据到 `setmeal_dish` 表。新增后清除 Spring Cache 中的 `setmealCache`。

---

### 4.2 分页查询套餐

- **URL**: `GET /setmeal/page`
- **请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 是 | 当前页码 |
| pageSize | int | 是 | 每页记录数 |
| name | String | 否 | 按套餐名称模糊查询 |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": {
    "records": [
      {
        "id": "1",
        "name": "超值双人餐",
        "categoryId": "2",
        "categoryName": "超值套餐",
        "price": 8800,
        "status": 1,
        "image": "setmeal001.jpg",
        "description": "双人豪华套餐",
        "createTime": "2024-01-01 10:00:00",
        "updateTime": "2024-01-01 10:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

- **功能说明**: 分页查询套餐，返回的 DTO 中包含关联的分类名称。

---

### 4.3 删除套餐

- **URL**: `DELETE /setmeal`
- **请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| ids | List\<Long\> | 是 | 套餐ID列表（Query参数，前端传数组） |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": "套餐数据删除成功"
}
```

- **功能说明**: 批量删除套餐。删除前检查套餐是否正在售卖（status=1），正在售卖的套餐不允许删除。删除套餐同时删除关联的 `setmeal_dish` 数据。删除后清除 Spring Cache 中的 `setmealCache`。

---

### 4.4 条件查询套餐列表

- **URL**: `GET /setmeal/list`
- **请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| categoryId | Long | 否 | 所属分类ID |
| status | Integer | 否 | 售卖状态（1=起售） |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": [
    {
      "id": "1",
      "name": "超值双人餐",
      "categoryId": "2",
      "price": 8800,
      "status": 1,
      "description": "双人豪华套餐",
      "image": "setmeal001.jpg"
    }
  ]
}
```

- **功能说明**: 移动端查询套餐列表，使用 Spring Cache `@Cacheable` 注解进行缓存。缓存 Key 格式：`{categoryId}_{status}`。

---

### 4.5 修改套餐售卖状态

- **URL**: `POST /setmeal/status/{status}`
- **请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| status | Integer | 是 | 目标状态（路径参数：0=停售, 1=起售） |
| ids | Long | 是 | 套餐ID（Query参数） |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": "修改成功"
}
```

- **功能说明**: 批量修改套餐的售卖状态（起售/停售）。

---

## 5. 订单管理 - /order

### 5.1 用户下单

- **URL**: `POST /order/submit`
- **Content-Type**: `application/json`
- **请求参数**:

```json
{
  "addressBookId": "1",
  "payMethod": 1,
  "remark": "少放辣"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| addressBookId | Long | 是 | 收货地址ID |
| payMethod | Integer | 否 | 支付方式（1=微信, 2=支付宝） |
| remark | String | 否 | 订单备注 |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": "下单成功"
}
```

- **功能说明**: 完整的下单流程：
  1. 从 ThreadLocal 获取当前用户 ID
  2. 查询该用户的购物车数据（若为空则抛异常）
  3. 查询用户信息和收货地址信息
  4. 使用 MyBatis-Plus 的 IdWorker 生成订单号
  5. 将购物车数据转换为订单明细数据
  6. 计算订单总金额
  7. 保存订单到 `orders` 表
  8. 批量保存订单明细到 `order_detail` 表
  9. 清空当前用户的购物车数据

---

## 6. 订单明细 - /orderDetail

> 当前无已定义的接口方法，该 Controller 为空壳，预留用于后续扩展订单明细查询功能。

---

## 7. 购物车 - /shoppingCart

### 7.1 添加到购物车

- **URL**: `POST /shoppingCart/add`
- **Content-Type**: `application/json`
- **请求参数**:

```json
{
  "dishId": "1",
  "name": "宫保鸡丁",
  "image": "abc123.jpg",
  "amount": 2800,
  "dishFlavor": "辣度:中辣"
}
```

或添加套餐：

```json
{
  "setmealId": "1",
  "name": "超值双人餐",
  "image": "setmeal001.jpg",
  "amount": 8800
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| dishId | Long | 条件必填 | 菜品ID（与 setmealId 二选一） |
| setmealId | Long | 条件必填 | 套餐ID（与 dishId 二选一） |
| name | String | 否 | 菜品/套餐名称 |
| image | String | 否 | 图片 |
| amount | Integer | 否 | 单价（分） |
| dishFlavor | String | 否 | 口味选择（菜品专用） |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": {
    "id": "10",
    "userId": "1",
    "dishId": "1",
    "name": "宫保鸡丁",
    "image": "abc123.jpg",
    "amount": 2800,
    "number": 1,
    "dishFlavor": "辣度:中辣",
    "createTime": "2024-01-01 10:00:00"
  }
}
```

- **功能说明**: 将菜品或套餐加入当前用户的购物车。系统从 ThreadLocal 获取当前用户 ID。若该菜品/套餐已在购物车中，则数量 +1；否则新增一条记录（数量默认为1）。

---

### 7.2 从购物车减少

- **URL**: `POST /shoppingCart/sub`
- **Content-Type**: `application/json`
- **请求参数**: 同 [7.1 添加到购物车](#71-添加到购物车)

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": {
    "id": "10",
    "userId": "1",
    "dishId": "1",
    "number": 0
  }
}
```

- **功能说明**: 减少购物车中指定菜品/套餐的数量。若当前数量 > 1，则数量 -1；若数量 = 1，则删除该条购物车记录。

---

### 7.3 查看购物车

- **URL**: `GET /shoppingCart/list`
- **请求参数**: 无（从 ThreadLocal 获取当前用户 ID）
- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": [
    {
      "id": "10",
      "userId": "1",
      "dishId": "1",
      "name": "宫保鸡丁",
      "image": "abc123.jpg",
      "amount": 2800,
      "number": 2,
      "dishFlavor": "辣度:中辣",
      "createTime": "2024-01-01 10:00:00"
    }
  ]
}
```

- **功能说明**: 查询当前登录用户的所有购物车数据，按创建时间升序排列。

---

### 7.4 清空购物车

- **URL**: `DELETE /shoppingCart/clean`
- **请求参数**: 无
- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": "清空购物车成功"
}
```

- **功能说明**: 根据当前用户 ID 删除该用户在购物车中的所有记录。

---

## 8. 地址簿 - /addressBook

### 8.1 新增地址

- **URL**: `POST /addressBook`
- **Content-Type**: `application/json`
- **请求参数**:

```json
{
  "consignee": "张三",
  "phone": "13812345678",
  "sex": "1",
  "provinceCode": "110000",
  "provinceName": "北京市",
  "cityCode": "110100",
  "cityName": "北京市",
  "districtCode": "110101",
  "districtName": "东城区",
  "detail": "某某街道某某小区1号楼1单元101",
  "label": "公司",
  "isDefault": 0
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| consignee | String | 是 | 收货人姓名 |
| phone | String | 是 | 收货人手机号 |
| sex | String | 否 | 性别（"0"=女, "1"=男） |
| provinceCode | String | 否 | 省份编码 |
| provinceName | String | 否 | 省份名称 |
| cityCode | String | 否 | 城市编码 |
| cityName | String | 否 | 城市名称 |
| districtCode | String | 否 | 区县编码 |
| districtName | String | 否 | 区县名称 |
| detail | String | 否 | 详细地址 |
| label | String | 否 | 标签（如"公司"、"家"、"学校"） |
| isDefault | Integer | 否 | 是否默认地址（0=否, 1=是） |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": {
    "id": "1",
    "userId": "1",
    "consignee": "张三"
  }
}
```

- **功能说明**: 新增收货地址，自动从 ThreadLocal 获取当前用户 ID 并设置。

---

### 8.2 设置默认地址

- **URL**: `PUT /addressBook/default`
- **Content-Type**: `application/json`
- **请求参数**:

```json
{
  "id": "1"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 要设为默认的地址ID |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": { ... }
}
```

- **功能说明**: 先将当前用户所有地址的 `isDefault` 设为 0，再将指定地址的 `isDefault` 设为 1。

---

### 8.3 根据ID查询地址

- **URL**: `GET /addressBook/{id}`
- **请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | Long | 是 | 地址ID（路径参数） |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": {
    "id": "1",
    "consignee": "张三",
    "phone": "13812345678",
    "detail": "某某街道某某小区1号楼1单元101",
    ...
  }
}
```

- **功能说明**: 根据地址 ID 查询单个地址信息。

---

### 8.4 查询默认地址

- **URL**: `GET /addressBook/default`
- **请求参数**: 无（从 ThreadLocal 获取当前用户 ID）
- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": {
    "id": "1",
    "consignee": "张三",
    "isDefault": 1,
    ...
  }
}
```

- **功能说明**: 查询当前用户的默认收货地址（isDefault=1）。

---

### 8.5 查询用户全部地址

- **URL**: `GET /addressBook/list`
- **请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | 否 | 用户ID（通常不传，后端自动获取当前用户） |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": [
    {
      "id": "1",
      "consignee": "张三",
      "phone": "13812345678",
      "isDefault": 1,
      ...
    },
    {
      "id": "2",
      "consignee": "李四",
      "phone": "13887654321",
      "isDefault": 0,
      ...
    }
  ]
}
```

- **功能说明**: 查询当前用户的所有收货地址，按更新时间降序排列。

---

## 9. 用户管理 - /user

### 9.1 发送验证码

- **URL**: `POST /user/sendMsg`
- **Content-Type**: `application/json`
- **请求参数**:

```json
{
  "phone": "13812345678"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 是 | 手机号 |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": "短信发送成功"
}
```

- **功能说明**: 生成 4 位随机数字验证码，将验证码存入 Redis（Key 为手机号，TTL 为 5 分钟），并打印到日志。实际生产环境中应接入阿里云短信服务发送验证码。

---

### 9.2 /*

- **URL**: `POST /user/login`
- **Content-Type**: `application/json`
- **请求参数**:

```json
{
  "phone": "13812345678",
  "code": "1234"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 是 | 手机号 |
| code | String | 是 | 短信验证码 |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": {
    "id": "1",
    "name": null,
    "phone": "13812345678",
    "sex": null,
    "idNumber": null,
    "avatar": null,
    "status": 1
  }
}
```

- **功能说明**: 移动端使用手机号 + 验证码登录。流程：
  1. 从 Redis 中获取该手机号对应的验证码
  2. 比对用户输入的验证码与 Redis 中的验证码
  3. 验证通过后，若该手机号对应的用户不存在则自动注册（status 默认为 1）
  4. 登录成功后将用户 ID 存入 Session
  5. 删除 Redis 中的验证码

---

## 10. 文件上传下载 - /common

### 10.1 文件上传

- **URL**: `POST /common/upload`
- **Content-Type**: `multipart/form-data`
- **请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | MultipartFile | 是 | 上传的文件（表单字段名必须为 `file`） |

- **返回结果**:

```json
{
  "code": 1,
  "msg": null,
  "data": "a1b2c3d4e5f6.jpg"
}
```

- **功能说明**: 上传图片文件到服务器的 `images/` 目录。文件名使用 UUID 随机生成以避免冲突。最大上传大小为 10MB。上传成功后返回文件的新文件名。

---

### 10.2 文件下载

- **URL**: `GET /common/download`
- **请求参数**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 文件名（Query参数，即上传时返回的文件名） |

- **返回结果**: 二进制图片数据（直接在浏览器中展示）

- **功能说明**: 根据文件名从服务器 `images/` 目录读取文件，以二进制流形式返回给浏览器。自动识别文件 MIME 类型。

---

## 11. 登录拦截机制

### 拦截器：LoginCheckFilter

> **当前状态**: 拦截器业务代码已清空，待实现。以下为预期功能描述。

- **拦截路径**: `/*`（所有请求）
- **白名单（无需登录即可访问）**:
  - `/employee/login` — 员工登录
  - `/employee/logout` — 员工退出
  - `/backend/**` — 管理后台静态资源
  - `/front/**` — 移动端静态资源
  - `/common/**` — 文件上传下载
  - `/user/sendMsg` — 发送验证码
  - `/user/login` — 用户登录

- **待实现的拦截逻辑**:
  1. 判断请求 URI 是否在白名单中 → 是则放行
  2. 检查 Session 中是否存在 `userId` 属性 → 有则放行（管理端已登录）
  3. 检查 Session 中是否存在 `user` 属性 → 有则放行（移动端已登录）
  4. 以上均不满足 → 返回 `{"code":0,"msg":"NOTLOGIN"}`

- **附加功能**: 登录成功后，将当前用户 ID 存入 `BaseContext`（ThreadLocal），供后续业务方法通过 `BaseContext.getCurrentId()` 获取当前操作用户。

---

## 附录：返回码说明

| code | 说明 |
|------|------|
| 1 | 操作成功 |
| 0 | 操作失败（具体错误信息见 msg 字段） |

## 附录：实体类与数据库表对应关系

| 实体类 | 数据库表 | 说明 |
|--------|----------|------|
| Employee | employee | 员工信息 |
| Category | category | 菜品/套餐分类 |
| Dish | dish | 菜品基本信息 |
| DishFlavor | dish_flavor | 菜品口味 |
| Setmeal | setmeal | 套餐基本信息 |
| SetmealDish | setmeal_dish | 套餐-菜品关联 |
| Orders | orders | 订单 |
| OrderDetail | order_detail | 订单明细 |
| ShoppingCart | shopping_cart | 购物车 |
| AddressBook | address_book | 收货地址簿 |
| User | user | C端用户 |

## 附录：通用返回格式 R\<T\>

```json
{
  "code": 1,
  "msg": null,
  "data": { }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 状态码（1=成功, 0=失败） |
| msg | String | 提示信息 |
| data | T (泛型) | 返回数据 |
