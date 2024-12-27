# yututu

[TOC]





## 背景介绍

- Reference

  



### 需求分析



### 项目规划



### 技术选型 ✔







### 架构设计 ✔







### 功能梳理

#### 用户模块

- 用户模块

  用户注册、用户登陆；获取当前用户信息、用户注销

  ```java
  // 用户注册
  input: 账号、密码、确认密码；邀请码
  output: 
  
  // 用户登陆
  input: 账号、密码
  output: 
  
  // 获取当前用户信息
  input: 
  output: 用户信息 (不用重复登陆)
  
  // 用户注销
  input: 
  output: 
  
  ```
  
- 页面

  用户注册页、用户登陆页、用户管理页、

- 扩展

  Github、QQ邮箱登陆、QQ登陆、微信登陆 (OAuth2)

  https://cloud.tencent.com/developer/article/1447723

  https://developer.work.weixin.qq.com/document/path/91335

  



#### 用户管理

- 用户管理

  用户权限控制；

  创建用户(admin)、根据id删除用户(admin)、更新用户(admin)、分页获取用户列表(admin且脱敏)、根据id获取用户(脱敏)

  ```
  // 用户权限控制
  角色：普通用户、管理员、vip、svip
      
  // 创建用户(admin)
  input: 账号、密码
  output: 
  
  // 删除用户(admin)
  input: id
  output: 
  
  // 更新用户(admin)
  input: id
  output: 
  
  // 查询用户(admin)
  input: id
  output: 
  
  ```

  



#### 图片模块

- 图片的上传和下载

  本地服务器 (不方便管理 不利于扩展 不方便迁移 不安全权限)

  对象存储 开源MinIO 亚马逊AmazonS3 阿里云OSS 腾讯云COS 

  



#### 图片管理

- 图片管理

  根据id删除图片(admin)、更新图片(admin)、分页获取图片列表(admin-不需要脱敏和限制条数)、根据id获取图片(admin-不需要脱敏)

  分页获取图片列表(需要脱敏和限制条数)、根据id获取图片(需要脱敏)、修改图片

  



### 业务流程

#### 用户模块：登陆流程

- 用户登陆流程

  session, token, jwt (酒店房卡)

  ```
  sequenceDiagram
      participant F as Frontend
      participant S as Server
      participant B as Browser
  
      F->>S: 建立连接
      S->>F: 创建匿名Session 并返回SessionID
      
      F->>S: 用户输入用户名和密码
      S->>S: 验证登录信息 并更新Session存储用户信息
      S->>F: 返回Set-Cookie指令 (SessionID)
      F->>B: 浏览器保存Cookie (SessionID)
      
      F->>S: 后续请求时自动附带Cookie
      S->>S: 提取Cookie查找对应Session 并从Session中获取用户信息
      S->>F: 返回结果 
  
  
  sequenceDiagram
      participant F as Frontend
      participant S as Server
      participant B as Browser
  
      F->>S: 用户输入用户名和密码
      S->>S: 验证用户身份 并生成Token
      S->>F: 返回Token
      F->>B: 存储Token
      
      F->>S: 后续请求时携带Token
      S->>S: 验证Token 执行业务逻辑
      S->>F: 返回结果
  
  
  sequenceDiagram
      participant F as Frontend
      participant S as Server
      participant B as Browser
  
      F->>S: 用户输入用户名和密码
      S->>S: 验证用户身份 并生成JWT
      S->>F: 返回JWT
      F->>B: 存储JWT
      
      F->>S: 后续请求时携带JWT
      S->>S: 验证JWT签名 解析JWT信息 执行业务逻辑
      S->>F: 返回结果
  
  ```

- session实现

  ```mermaid
  sequenceDiagram
      participant F as Frontend
      participant S as Server
      participant B as Browser
  
      F->>S: 建立连接
      S->>F: 创建匿名Session 并返回SessionID
      
      F->>S: 用户输入用户名和密码
      S->>S: 验证登录信息 并更新Session存储用户信息
      S->>F: 返回Set-Cookie指令 (SessionID)
      F->>B: 浏览器保存Cookie (SessionID)
      
      F->>S: 后续请求时自动附带Cookie
      S->>S: 提取Cookie查找对应Session 并从Session中获取用户信息
      S->>F: 返回结果 
  ```

  



#### 用户模块：权限控制

- What

  未登录也能使用

  登陆才能使用

  未登录能使用 但登陆能更多操作 (引流)

  仅管理员使用

- How

  每个接口单独写 X

  统一的接口拦截和权限校验 (SpringAOP切面 + 自定义权限校验注解) ✔

  权限框架 Shiro / SpringSecurity / Sa-Token

  



#### 用户模块：会员付费

- What

  角色划分 (普通用户 vip svip)

  会员有效期 

  会员开通 (微信支付 支付宝支付 兑换码 邀请好友)

  会员退费 

  会员查询 

  会员功能 (AOP)

  会员积分 

  



#### 图片模块：创建图片

- 创建图片

  上传图片文件

  补充图片信息到数据库

  ```
  方案1
  
  
  方案2
  用户上传图片 -> 立刻保存到数据库 得到id
  用户在当前界面 重新上传图片 -> 更新之前的图片 而不是新产生id
  
  
  ```

- 解析图片

  后端服务器：Java.ImageIO, Python.Pillow, OpenCV

  第三方云存储服务：[COS.CI](https://cloud.tencent.com/product/ci), AWS.S3; ImageMagick, ExifTool

  



#### 图片模块：文件下载

- 文件下载

  Way 1: 直接下载COS的文件到后端服务器 (适合服务器端处理文件)

  Way 2: 获取文件下载输入流 (适合返回给前端用户)

  Way 3: 直接通过URL路径链接访问 (适合单一的可被公开访问的资源 如*用户头像*或*本项目公开文件*)

  ```
  COS -> Backend(Save) -> Frontend
  
  Cos -> Backend(Stream) -> Frontend
  
  Frontend -> Backend(Auth) 
  Frontend + keyTemp -> Cos
  COS -> Frontend
  
  ```

  



## 文档汇总

### 页面设计 



#### 页面 X



#### 页面 X



### 库表设计 

- 汇总SQL

  ```sql
  
  ```

  



#### user

- user

  ```
  id 【标配】
  账号 密码 【登陆】
  用户名 头像 简介 角色 【基本信息】
  会员过期时间 会员兑换码 会员编号 【会员模块】
  分享码 被哪个用户邀请 【用户邀请】
  手机号 邮箱号 QQ号 微信号 github 【三方登陆】
  编辑时间(用户编辑) 创建时间(何时产生) 更新时间(管理员更新) 逻辑删除 【标配】
  
  唯一：账号 
  索引：用户名 (书的目录-检索速度快 占用空间不大 字段区分度要大)
  
  ```
  
  



#### picture

- picture

  ```
  id 【标配】
  编辑时间(用户编辑) 创建时间(何时产生) 更新时间(管理员更新) 逻辑删除 【标配】
  url 名称 简介 分类 标签(json数组) 创建人 【基本信息】
  体积 宽度 高度 宽高比 格式 【基本信息】
  审核状态 审核信息 审核人 审核时间 【用户上传功能】
  
  索引：
  注意：
  
  方案1：json数组
  方案2：中间表
  方案3：es
  
  ```

  





### 接口数据



#### Interface X



#### Interface X





## 仓库初始化

### 新建仓库

#### 本地仓库

- 新建本地仓库

  ```bash
  cd /d/code3/dev/yututu/
  git init 
  
  ```

  



#### 远程仓库

- [新建远程仓库gitee](https://gitee.com/plktime1043/yututu)

- [新建远程仓库github](https://github.com/time1043/yututu)

  



### 推送远程仓库

#### gitee

- gitee

  ```bash
  # 远程仓库名为gitee
  git remote add gitee <...>
  # 查看所有远程仓库
  git remote -v
  
  git add .
  git commit  # init project
  git push gitee main  # 分支名为main
  
  # 查看分支名
  git branch -a
  # 查看分支提交情况
  git log --graph --decorate --all
  
  ```

  



#### github

- github

  ```bash
  # 远程仓库名为github
  git remote add github <...>
  # 查看所有远程仓库
  git remote -v
  
  git add .
  git commit  # init project
  git push github main  # 分支名为main
  
  # 查看分支名
  git branch -a
  # 查看分支提交情况
  git log --graph --decorate --all
  
  ```

  









## 后端初始化

- 前后端开发

  前端：页面逻辑(页面结构 样式优化)、权限控制(隐藏页面)

  后端：复杂业务逻辑、权限控制(接口鉴权)

  联调：前端向后端发请求(ajax axios)

  



### 项目新建

#### 项目新建

- 项目新建

  ```bash
  # jdk17
  # https://start.aliyun.com/
  # com.oswin902
  
  
  cd /d/code3/dev/yututu/yututu-backend/
  # 通用代码
  mkdir -p src/main/java/com/oswin902/yututubackend/common
  mkdir -p src/main/java/com/oswin902/yututubackend/exception
  mkdir -p src/main/java/com/oswin902/yututubackend/config
  mkdir -p src/main/java/com/oswin902/yututubackend/constant
  # 库表设计
  mkdir sql && touch sql/init.sql
  # 接口开发
  mkdir -p src/main/java/com/oswin902/yututubackend/controller
  mkdir -p src/main/java/com/oswin902/yututubackend/service
  mkdir -p src/main/java/com/oswin902/yututubackend/mapper
  mkdir -p src/main/java/com/oswin902/yututubackend/model/entity
  mkdir -p src/main/java/com/oswin902/yututubackend/model/enums
  mkdir -p src/main/java/com/oswin902/yututubackend/model/dto
  mkdir -p src/main/java/com/oswin902/yututubackend/model/vo
  mkdir -p src/main/java/com/oswin902/yututubackend/annotation
  mkdir -p src/main/java/com/oswin902/yututubackend/aop
  
  
  # 自己业务自定义异常
  touch src/main/java/com/oswin902/yututubackend/exception/BusinessException.java
  touch src/main/java/com/oswin902/yututubackend/exception/ErrorCode.java
  touch src/main/java/com/oswin902/yututubackend/exception/GlobalExceptionHandler.java
  touch src/main/java/com/oswin902/yututubackend/exception/ThrowUtils.java
  # 请求响应格式
  touch src/main/java/com/oswin902/yututubackend/common/BaseResponse.java
  touch src/main/java/com/oswin902/yututubackend/common/DeleteRequest.java
  touch src/main/java/com/oswin902/yututubackend/common/PageRequest.java
  touch src/main/java/com/oswin902/yututubackend/common/ResultUtils.java
  # 配置 (全局跨域)
  touch src/main/java/com/oswin902/yututubackend/config/CorsConfig.java
  # 接口开发
  touch src/main/java/com/oswin902/yututubackend/contoller/MainContoller.java  # 健康检查
  
  
  # 用户模块
  touch src/main/java/com/oswin902/yututubackend/model/dto/UserRegisterRequest.java
  touch src//main/java/com/oswin902/yututubackend/controller/UserController.java
  
  touch src/main/java/com/oswin902/yututubackend/constant/UserConstant.java
  
  ```
  
  项目结构
  
  ```bash
  
  ```
  
  



#### 项目依赖

- 项目依赖

  springboot2.7.6, spring web, lombok, aop 【web】

  msql, mybatis, [mybatis-plus](https://baomidou.com/getting-started/) 【数据库操作】

  [hutool](https://doc.hutool.cn/pages/index/#%F0%9F%8D%8Amaven) 字符串操作 日期操作 ... 【工具箱】

  [knife4j](https://doc.xiaominfo.com/docs/quick-start#spring-boot-2) 【接口文档】

- yututu-backend\pom.xml

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
      <groupId>com.oswin902</groupId>
      <artifactId>yututu-backend</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <name>yututu-backend</name>
      <description>yututu-backend</description>
  
      <properties>
          <java.version>1.8</java.version>
          <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
          <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
          <spring-boot.version>2.7.6</spring-boot.version>
      </properties>
  
      <dependencies>
          <!-- springboot-web -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-web</artifactId>
          </dependency>
          <!-- springboot-test -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-test</artifactId>
              <scope>test</scope>
          </dependency>
          <!-- springboot-aop -->
          <dependency>
              <groupId>org.springframework.boot</groupId>
              <artifactId>spring-boot-starter-aop</artifactId>
          </dependency>
  
          <!-- lombok -->
          <dependency>
              <groupId>org.projectlombok</groupId>
              <artifactId>lombok</artifactId>
              <optional>true</optional>
          </dependency>
          <!-- hutool: https://doc.hutool.cn/pages/index -->
          <dependency>
              <groupId>cn.hutool</groupId>
              <artifactId>hutool-all</artifactId>
              <version>5.8.26</version>
          </dependency>
          <!-- 接口文档knife4j: https://doc.xiaominfo.com/docs/quick-start#spring-boot-2 -->
          <dependency>
              <groupId>com.github.xiaoymin</groupId>
              <artifactId>knife4j-openapi2-spring-boot-starter</artifactId>
              <version>4.4.0</version>
          </dependency>
  
          <!-- mysql -->
          <dependency>
              <groupId>com.mysql</groupId>
              <artifactId>mysql-connector-j</artifactId>
              <scope>runtime</scope>
          </dependency>
          <!-- mybatis-plus: https://baomidou.com/introduce/ -->
          <dependency>
              <groupId>com.baomidou</groupId>
              <artifactId>mybatis-plus-boot-starter</artifactId>
              <version>3.5.9</version>
          </dependency>
          <!-- mybatis-plus 分页组件 (jdk11+) -->
          <dependency>
              <groupId>com.baomidou</groupId>
              <artifactId>mybatis-plus-jsqlparser</artifactId>
          </dependency>
      </dependencies>
  
      <dependencyManagement>
          <dependencies>
              <dependency>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-dependencies</artifactId>
                  <version>${spring-boot.version}</version>
                  <type>pom</type>
                  <scope>import</scope>
              </dependency>
              <!-- mybatis-plus 分页组件 -->
              <dependency>
                  <groupId>com.baomidou</groupId>
                  <artifactId>mybatis-plus-bom</artifactId>
                  <version>3.5.9</version>
                  <type>pom</type>
                  <scope>import</scope>
              </dependency>
          </dependencies>
      </dependencyManagement>
  
      <build>
          <plugins>
              <plugin>
                  <groupId>org.apache.maven.plugins</groupId>
                  <artifactId>maven-compiler-plugin</artifactId>
                  <version>3.8.1</version>
                  <configuration>
                      <source>1.8</source>
                      <target>1.8</target>
                      <encoding>UTF-8</encoding>
                  </configuration>
              </plugin>
              <plugin>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-maven-plugin</artifactId>
                  <version>${spring-boot.version}</version>
                  <configuration>
                      <mainClass>com.oswin902.yututubackend.YututuBackendApplication</mainClass>
                      <skip>true</skip>
                  </configuration>
                  <executions>
                      <execution>
                          <id>repackage</id>
                          <goals>
                              <goal>repackage</goal>
                          </goals>
                      </execution>
                  </executions>
              </plugin>
          </plugins>
      </build>
  
  </project>
  
  ```
  
  



#### 中间件配置

- mysql8

  ```
  # docker mysql8
  mkdir -p /opt/data/mysql/data /opt/data/mysql/conf /opt/data/mysql/init  # rz -E
  
  docker run -d \
    --name mysql \
    -p 3306:3306 \
    -e TZ=Asia/Shanghai \
    -e MYSQL_ROOT_PASSWORD=123456 \
    -v /opt/data/mysql/data:/var/lib/mysql \
    -v /opt/data/mysql/conf:/etc/mysql/conf.d \
    -v /opt/data/mysql/init:/docker-entrypoint-initdb.d \
    mysql:8
  
  docker exec -it mysql bash
  mysql -uroot -p123456
  
  ```
  
  



#### 接口文档







#### Q mybatis-plus 3.5.9

- 版本更新

  https://baomidou.com/plugins/pagination/

  于 `v3.5.9` 起，`PaginationInnerInterceptor` 已分离出来。如需使用，则需单独引入 `mybatis-plus-jsqlparser` 依赖 

  https://baomidou.com/getting-started/install/

  引入 `MyBatis-Plus` 之后请不要再次引入 `MyBatis` 以及 `mybatis-spring-boot-starter`和`MyBatis-Spring`，以避免因版本差异导致的问题

  



### 项目配置

#### 项目配置

- src\main\resources\application.yml (.properties)

  ```yml
  server:
    port: 8011
    servlet:
      context-path: /api
  
  spring:
    # 应用配置
    application:
      name: yututu-backend
    # 数据库配置
    datasource:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3308/yututu
      username: root
      password: 123456
  
  # mybatis-plus配置 https://baomidou.com/reference/
  mybatis-plus:
    configuration:
      map-underscore-to-camel-case: false
      log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # 开发阶段打印日志
    global-config:
      db-config:
        logic-delete-field: isDeleted  # 逻辑删除字段
        logic-delete-value: 1  # 逻辑已删除值 默认1
        logic-not-delete-value: 0  # 逻辑未删除值 默认0
  
  # 接口文档knife4j
  # http://localhost:8011/api/doc.html 【页面】
  # http://localhost:8011/api/v2/api-docs?group=%E9%BB%98%E8%AE%A4%E5%88%86%E7%BB%84 【json】
  knife4j:
    enable: true
    openapi:
      title: 后端接口文档
      description: 鱼图图后端接口文档
      concat: oswin902
      version: v0.0.1
      group:
        default:
          group-name: 默认分组
          api-rule: package
          api-rule-resources:
            - com.oswin902.yututubackend.controller  # 扫描的包路径
  
  ```

  



#### 服务器启动类

- src\main\java\com\oswin902\yututubackend\YututuBackendApplication.java

  ```java
  package com.oswin902.yututubackend;
  
  import org.mybatis.spring.annotation.MapperScan;
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.context.annotation.EnableAspectJAutoProxy;
  
  @SpringBootApplication
  @MapperScan("com.oswin902.yututubackend.mapper")  // mybatis-plus
  @EnableAspectJAutoProxy(exposeProxy = true) // spring-aop  AopContext.currentProxy()
  public class YututuBackendApplication {
  
      public static void main(String[] args) {
          SpringApplication.run(YututuBackendApplication.class, args);
      }
  
  }
  
  ```

  



### 通用基础代码

#### 异常状态码 (意料异常) ✔

- src\main\java\com\oswin902\yututubackend\exception\ErrorCode.java

  ```java
  package com.oswin902.yututubackend.exception;
  
  import lombok.Getter;
  
  /**
   * 异常状态枚举
   */
  @Getter
  public enum ErrorCode {
  
      SUCCESS(0, "ok"),
      // http 400: bad request; 401: unauthorized; 403: forbidden; 404: not found; 【客户端错误】
      PARAMS_ERROR(40000, "请求参数错误"),
      NOT_LOGIN_ERROR(40100, "未登录"),
      NO_AUTH_ERROR(40101, "无权限"),
      FORBIDDEN_ERROR(40300, "禁止访问"),
      NOT_FOUND_ERROR(40400, "请求数据不存在"),
      // http 500: internal server error 【服务器错误】
      SYSTEM_ERROR(50000, "系统内部异常"),
      OPERATION_ERROR(50001, "操作失败");
  
      /**
       * 状态码
       */
      private final int code;
  
      /**
       * 信息
       */
      private final String message;
  
      ErrorCode(int code, String message) {
          this.code = code;
          this.message = message;
      }
  
  }
  
  ```

  



#### 异常类自定义

- src\main\java\com\oswin902\yututubackend\exception\BusinessException.java

  ```java
  package com.oswin902.yututubackend.exception;
  
  import lombok.Getter;
  
  /**
   * 异常类自定义
   */
  @Getter
  public class BusinessException extends RuntimeException {
  
      /**
       * 错误码
       */
      private final int code;
  
      public BusinessException(int code, String message) {
          super(message);
          this.code = code;
      }
  
      public BusinessException(ErrorCode errorCode) {
          super(errorCode.getMessage());
          this.code = errorCode.getCode();
      }
  
      public BusinessException(ErrorCode errorCode, String message) {
          super(message);
          this.code = errorCode.getCode();
      }
  
      public static void main(String[] args) {
  
      }
  }
  
  // Way 1
  // if (condition) {
  //    throw new RuntimeException("error message");
  // }
  //
  // Way 2
  // if (condition) {
  //    throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "error message");
  // }
  
  ```

  



#### 静态工具类

- src\main\java\com\oswin902\yututubackend\exception\ThrowUtils.java

  ```java
  package com.oswin902.yututubackend.exception;
  
  /**
   * 异常处理工具类
   */
  public class ThrowUtils {
  
      /**
       * 抛出异常 (类似断言类)
       *
       * @param condition        条件
       * @param runtimeException 异常
       */
      public static void throwIf(boolean condition, RuntimeException runtimeException) {
          if (condition) {
              throw runtimeException;
          }
      }
  
      /**
       * 抛出异常-错误码枚举
       *
       * @param condition 条件
       * @param errorCode 错误码枚举
       */
      public static void throwIf(boolean condition, ErrorCode errorCode) {
          throwIf(condition, new BusinessException(errorCode));
      }
  
      /**
       * 抛出异常-错误码枚举
       *
       * @param condition 条件
       * @param errorCode 错误码枚举
       * @param message   错误信息
       */
      public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
          throwIf(condition, new BusinessException(errorCode, message));
      }
  }
  
  
  // Way 1
  // if (condition) {
  //    throw new RuntimeException("error message");
  // }
  //
  // Way 2
  // if (condition) {
  //    throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "error message");
  // }
  //
  // Way 3 ✔
  // ThrowUtils.throwIf(condition, ErrorCode.FORBIDDEN_ERROR, "error message");
  
  ```

  



#### 异常全局拦截器 (意外异常)

- src\main\java\com\oswin902\yututubackend\exception\GlobalExceptionHandler.java

  ```java
  package com.oswin902.yututubackend.exception;
  
  import com.oswin902.yututubackend.common.BaseResponse;
  import com.oswin902.yututubackend.common.ResultUtils;
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.web.bind.annotation.ExceptionHandler;
  import org.springframework.web.bind.annotation.RestControllerAdvice;
  
  /**
   * 全局异常处理器
   */
  @RestControllerAdvice
  @Slf4j
  public class GlobalExceptionHandler {
  
      /**
       * 自定义业务异常 处理器
       *
       * @param e
       * @return
       */
      @ExceptionHandler(BusinessException.class)
      public BaseResponse<?> businessExceptionHandler(BusinessException e) {
          log.error("BusinessException", e);
          return ResultUtils.error(e.getCode(), e.getMessage());
      }
  
      /**
       * 其他运行时异常 处理器
       *
       * @param e
       * @return
       */
      @ExceptionHandler(RuntimeException.class)
      public BaseResponse<?> businessExceptionHandler(RuntimeException e) {
          log.error("RuntimeException", e);
          return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
      }
  }
  
  ```

  



#### 响应封装 ✔

- src\main\java\com\oswin902\yututubackend\common\BaseResponse.java

  ```java
  package com.oswin902.yututubackend.common;
  
  import com.oswin902.yututubackend.exception.ErrorCode;
  import lombok.Data;
  
  import java.io.Serializable;
  
  /**
   * 响应封装类
   *
   * @param <T> 响应数据类型
   */
  @Data
  public class BaseResponse<T> implements Serializable {
  
      private int code;
      private T data;
      private String message;
  
      public BaseResponse(int code, T data, String message) {
          this.code = code;
          this.data = data;
          this.message = message;
      }
  
      public BaseResponse(int code, T data) {
          this(code, data, "");
      }
  
      public BaseResponse(ErrorCode errorCode) {
          this(errorCode.getCode(), null, errorCode.getMessage());
      }
  
  }
  
  // Way 1
  // public String hello(@RequestParam(name = "name", defaultValue = "unknown user") String name) {
  //    return "Hello, " + name;
  // }
  //
  // Way 2
  // public BaseResponse<String> hello(@RequestParam(name = "name", defaultValue = "unknown user") String name) {
  //    return new BaseResponse<>(200, "Hello, " + name);
  // }
  
  ```

  



#### 静态工具类

- src\main\java\com\oswin902\yututubackend\common\ResultUtils.java

  ```java
  package com.oswin902.yututubackend.common;
  
  
  import com.oswin902.yututubackend.exception.ErrorCode;
  
  /**
   * 响应工具类
   */
  public class ResultUtils {
  
      /**
       * 成功
       *
       * @param data 数据
       * @param <T>  数据类型
       * @return 响应
       */
      public static <T> BaseResponse<T> success(T data) {
          return new BaseResponse<>(0, data, "ok");
      }
  
      /**
       * 失败
       *
       * @param errorCode 错误码
       * @return 响应
       */
      public static BaseResponse<?> error(ErrorCode errorCode) {
          return new BaseResponse<>(errorCode);
      }
  
      /**
       * 失败
       *
       * @param code    错误码
       * @param message 错误信息
       * @return 响应
       */
      public static BaseResponse<?> error(int code, String message) {
          return new BaseResponse<>(code, null, message);
      }
  
      /**
       * 失败
       *
       * @param errorCode 错误码
       * @return 响应
       */
      public static BaseResponse<?> error(ErrorCode errorCode, String message) {
          return new BaseResponse<>(errorCode.getCode(), null, message);
      }
  }
  
  // Way 1
  // public String hello(@RequestParam(name = "name", defaultValue = "unknown user") String name) {
  //    return "Hello, " + name;
  // }
  //
  // Way 2
  // public BaseResponse<String> hello(@RequestParam(name = "name", defaultValue = "unknown user") String name) {
  //    return new BaseResponse<>(200, "Hello, " + name);
  // }
  //
  // Way 3 ✔
  // public BaseResponse<String> hello(@RequestParam(name = "name", defaultValue = "unknown user") String name) {
  //    return ResultUtils.success("Hello, " + name);
  // }
  
  ```

  



#### 请求封装 (分页请求) ✔

- src\main\java\com\oswin902\yututubackend\common\PageRequest.java

  ```java
  package com.oswin902.yututubackend.common;
  
  import lombok.Data;
  
  /**
   * 通用的分页请求类
   */
  @Data
  public class PageRequest {
  
      /**
       * 当前页号
       */
      private int current = 1;
  
      /**
       * 页面大小
       */
      private int pageSize = 10;
  
      /**
       * 排序字段
       */
      private String sortField;
  
      /**
       * 排序顺序（默认升序）
       */
      private String sortOrder = "descend";
  }
  
  ```

  



#### 请求封装 (删除请求)

- src\main\java\com\oswin902\yututubackend\common\DeleteRequest.java

  ```java
  package com.oswin902.yututubackend.common;
  
  import lombok.Data;
  
  import java.io.Serializable;
  
  /**
   * 通用的删除请求类
   */
  @Data
  public class DeleteRequest implements Serializable {
  
      /**
       * id
       */
      private Long id;
  
      private static final long serialVersionUID = 1L;
  }
  
  ```

  



#### 后端跨域配置 ✔

- 跨域问题

  是什么：只存在于浏览器、不存在于桌面端系统调用

  如何解决：后端支持跨域、代理转发(nginx 前端脚手架)

  ```
  # 后端允许跨域
  127.0.0.1:5173 【前端】
  127.0.0.1:8011 【后端】
  
  # 代理
  127.0.0.1:5173 【前端】
  127.0.0.1:5173/proxy -> 127.0.0.1:8011 【代理】
  127.0.0.1:8011 【后端】
  
  ```

  



---

- src\main\java\com\oswin902\yututubackend\config\CorsConfig.java (全局跨域配置)

  ```java
  package com.oswin902.yututubackend.config;
  
  import org.springframework.context.annotation.Configuration;
  import org.springframework.web.servlet.config.annotation.CorsRegistry;
  import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
  
  /**
   * 全局跨域配置
   */
  @Configuration
  public class CorsConfig implements WebMvcConfigurer {
  
      @Override
      public void addCorsMappings(CorsRegistry registry) {
          // 覆盖所有请求
          registry.addMapping("/**")
                  // 允许发送 Cookie
                  .allowCredentials(true)
                  // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
                  .allowedOriginPatterns("*")
                  .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                  .allowedHeaders("*")
                  .exposedHeaders("*");
      }
  }
  
  ```

  



#### 接口健康检查

- src\main\java\com\oswin902\yututubackend\controller\MainController.java

  ```java
  package com.oswin902.yututubackend.controller;
  
  import com.oswin902.yututubackend.common.BaseResponse;
  import com.oswin902.yututubackend.common.ResultUtils;
  import org.springframework.web.bind.annotation.GetMapping;
  import org.springframework.web.bind.annotation.RequestMapping;
  import org.springframework.web.bind.annotation.RestController;
  
  @RestController
  @RequestMapping("/")
  public class MainController {
  
      /**
       * 健康检查
       *
       * @return BaseResponse<String>
       */
      @GetMapping("/health")
      public BaseResponse<String> health() {
          return ResultUtils.success("Healthy");
      }
  }
  
  ```

  



## 前端初始化

### 项目新建

#### 项目新建

- 项目新建

  ```bash
  # node20
  # https://cn.vuejs.org/guide/quick-start.html
  # create-vue 脚手架
  
  # 脚手架新建项目
  npm create vue@3.12.2
  # yututu-frontend
  # TypeScript？是
  # JSX？否
  # Vue Router单页面应用？是
  # Pinia状态管理？是
  # Vitest用于单元测试？否
  # 端到端测试工具？不需要
  # ESLint代码质量检测？是
  # Prettier代码格式化？是
  cd yututu-frontend && npm i
  npm run dev
  
  # 组件库UI
  # https://antdv.com/docs/vue/getting-started-cn
  npm i --save ant-design-vue@4.x
  # 全局注册
  
  # 请求后端axios
  npm install axios
  # 生成请求代码openapi
  npm i @umijs/openapi -D
  
  
  cd /d/code3/dev/yututu/yututu-frontend
  # 布局 组件
  mkdir -p src/layouts
  touch src/layouts/BasicLayout.vue
  touch src/components/GlobalHeader.vue
  
  mkdir -p src/common && touch src/common/request.ts src/common/config.ts  # 请求封装
  touch openapi.config.js  # 生成请求代码
  touch src/stores/useLoginUserStore.ts  # 全局状态管理
  
  # 页面开发
  rm -rf src/views
  mkdir -p src/pages
  touch src/pages/HomePage.vue
  touch src/pages/AboutPage.vue
  
  ```
  
  项目结构
  
  ```bash
  yututu-frontend $ tree -L 2
  .
  |-- README.md
  |-- env.d.ts
  |-- eslint.config.js
  |-- index.html              # 单页面
  |-- node_modules
  |-- package-lock.json
  |-- package.json
  |-- public
  |   `-- favicon.ico
  |-- src
  |   |-- App.vue
  |   |-- assets
  |   |-- components
  |   |-- main.ts
  |   |-- router
  |   |-- stores
  |   `-- views             # 页面
  |-- tsconfig.app.json
  |-- tsconfig.json
  |-- tsconfig.node.json
  `-- vite.config.ts
  
  ```
  
  



#### 项目依赖

- package.json

  ```json
  {
    "name": "yututu-frontend",
    "version": "0.0.0",
    "private": true,
    "type": "module",
    "scripts": {
      "dev": "vite --port 5011",
      "openapi": "node openapi.config.js",
      "build": "run-p type-check \"build-only {@}\" --",
      "preview": "vite preview",
      "build-only": "vite build",
      "type-check": "vue-tsc --build",
      "lint": "eslint . --fix",
      "format": "prettier --write src/"
    },
    "dependencies": {
      "ant-design-vue": "^4.2.6",
      "axios": "^1.7.9",
      "dotenv": "^16.4.7",
      "pinia": "^2.2.6",
      "vue": "^3.5.13",
      "vue-router": "^4.4.5"
    },
    "devDependencies": {
      "@tsconfig/node22": "^22.0.0",
      "@types/node": "^22.9.3",
      "@umijs/openapi": "^1.13.0",
      "@vitejs/plugin-vue": "^5.2.1",
      "@vue/eslint-config-prettier": "^10.1.0",
      "@vue/eslint-config-typescript": "^14.1.3",
      "@vue/tsconfig": "^0.7.0",
      "eslint": "^9.14.0",
      "eslint-plugin-vue": "^9.30.0",
      "npm-run-all2": "^7.0.1",
      "prettier": "^3.3.3",
      "typescript": "~5.6.3",
      "vite": "^6.0.1",
      "vite-plugin-vue-devtools": "^7.6.5",
      "vue-tsc": "^2.1.10"
    }
  }
  
  ```
  
  



#### 开发规范

- vue3组合式API

  ```vue
  <template>
    <div id="xxPage"></div>
  </template>
  
  <script setup lang="ts">
  </script>
  
  <style scoped>
  #xxPage {
  }
  </style>
  
  ```

  



### 项目配置

#### 项目配置

- src\common\config.ts

  ```typescript
  const Config = {
    BACKEND_URL: 'http://localhost:8011', // 后端配置跨域
    //BACKEND_URL: '',  // 前端配置跨域
  }
  
  export default Config
  
  ```
  
  



#### 路由

- src\router\index.ts

  ```typescript
  import { createRouter, createWebHistory } from 'vue-router'
  import HomePage from '@/pages/HomePage.vue'
  import UserLoginPage from '@/pages/user/UserLoginPage.vue'
  import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
  import UserManagePage from '@/pages/admin/UserManagePage.vue'
  import Error403Page from '@/pages/error/403.vue'
  import Error404Page from '@/pages/error/404.vue'
  import Error500Page from '@/pages/error/500.vue'
  
  const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
      {
        path: '/',
        name: '主页',
        component: HomePage,
      },
      {
        path: '/user/login',
        name: '用户登录',
        component: UserLoginPage,
      },
      {
        path: '/user/register',
        name: '用户注册',
        component: UserRegisterPage,
      },
      {
        path: '/admin/userManage',
        name: '用户管理',
        component: UserManagePage,
      },
      {
        path: '/about',
        name: '关于',
        component: () => import('../pages/AboutPage.vue'), // lazy-loaded
      },
      {
        path: '/error/403',
        name: '无权限',
        component: Error403Page,
      },
      {
        path: '/error/404',
        name: '未找到',
        component: Error404Page,
      },
      {
        path: '/error/500',
        name: '服务器错误',
        component: Error500Page,
      },
    ],
  })
  
  export default router
  
  ```
  
  



#### 单页面

- index.html

  ```html
  <!doctype html>
  <html lang="">
    <head>
      <!-- https://www.bitbug.net/ -->
      <link rel="icon" href="/ai-icon16.ico" />
      <title>鱼图图</title>
      <meta charset="UTF-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      <meta name="description" content="智能云图库平台 海量图片素材免费获取" />
    </head>
    
    <body>
      <div id="app"></div>
      <script type="module" src="/src/main.ts"></script>
    </body>
  </html>
  
  ```

  



#### 应用入口类

- src\main.ts

  ```typescript
  import Antd from 'ant-design-vue'
  import 'ant-design-vue/dist/reset.css'
  import { createPinia } from 'pinia'
  import { createApp } from 'vue'
  
  import App from './App.vue'
  import router from './router'
  import '@/common/access'
  
  const app = createApp(App)
  
  app.use(createPinia())
  app.use(router)
  app.use(Antd)
  
  app.mount('#app')
  
  ```
  
  



#### 初始化页面

- src\App.vue

  ```vue
  <template>
    <div id="app">
      <a-config-provider :locale="locale">
        <component :is="currentLayout"></component>
      </a-config-provider>
    </div>
  </template>
  
  <script setup lang="ts">
  import { computed, ref } from 'vue'
  import { useRoute } from 'vue-router'
  import zhCN from 'ant-design-vue/es/locale/zh_CN'
  import { useLoginUserStore } from '@/stores/useLoginUserStore'
  import BasicLayout from '@/layouts/BasicLayout.vue'
  import ZeroLayout from '@/layouts/ZeroLayout.vue'
  
  // 默认中文
  // https://antdv.com/docs/vue/i18n-cn
  const locale = ref(zhCN)
  
  // // 后端获取当前用户信息
  // const loginUserStore = useLoginUserStore()
  // loginUserStore.fetchLoginUser()
  
  // 当前布局判断
  const route = useRoute()
  const currentLayout = computed(() => {
    // if (route.path === '/user/login') return ZeroLayout
    const whiteList = ['/user/login', '/user/register', '/error/403', '/error/404', '/error/500']
    if (whiteList.includes(route.path)) {
      return ZeroLayout
    }
    return BasicLayout
  })
  </script>
  
  <style scoped></style>
  
  ```
  
  



### 通用基础代码

#### 布局 ✔

- src\layouts\BasicLayout.vue

  ```vue
  <template>
    <!-- https://antdv.com/components/layout-cn -->
    <div id="basicLayout">
      <a-layout style="min-height: 100vh">
        <!-- 顶部：导航 -->
        <a-layout-header class="header">
          <GlobalHeader />
        </a-layout-header>
  
        <!-- 中间：内容 vue-router -->
        <a-layout-content class="content">
          <router-view></router-view>
        </a-layout-content>
  
        <!-- 底部：版权信息 -->
        <a-layout-footer class="footer">
          <a href="http://github.com/time1043" target="_blank"> <GithubOutlined /> </a>
          yututu by oswin902
        </a-layout-footer>
      </a-layout>
    </div>
  </template>
  
  <script setup lang="ts">
  import GlobalHeader from '@/components/GlobalHeader.vue'
  import { GithubOutlined } from '@ant-design/icons-vue'
  </script>
  
  <style scoped>
  #basicLayout .header {
    background: #fff;
    color: unset;
    margin-bottom: 16px;
    padding-inline: 20px;
    /* 绝对定位 */
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 10;
  }
  
  #basicLayout .content {
    padding: 20px;
    background: linear-gradient(to right, #fefefe, #fff);
    margin-bottom: 30px;
    margin-top: 80px;
    margin-left: 20px;
    margin-right: 20px;
  }
  
  #basicLayout .footer {
    text-align: center;
    background: #efefef;
    padding: 16px;
    /* 绝对定位 */
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    text-align: center;
  }
  </style>
  
  ```

- src\layouts\ZeroLayout.vue

  ```vue
  <template>
    <!-- https://antdv.com/components/layout-cn -->
    <div id="basicLayout">
      <a-layout style="min-height: 100vh">
        <!-- 中间：内容 vue-router -->
        <a-layout-content class="content">
          <router-view></router-view>
        </a-layout-content>
  
        <!-- 底部：版权信息 -->
        <a-layout-footer class="footer">
          <a href="http://github.com/time1043" target="_blank"> yututu by oswin902 </a>
        </a-layout-footer>
      </a-layout>
    </div>
  </template>
  
  <script setup lang="ts"></script>
  
  <style scoped>
  #basicLayout .content {
    padding: 20px;
    background: linear-gradient(to right, #fefefe, #fff);
    margin-bottom: 30px;
  }
  
  #basicLayout .footer {
    text-align: center;
    background: #efefef;
    padding: 16px;
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    text-align: center;
  }
  </style>
  
  ```

  



#### 组件：导航条

- src\components\GlobalHeader.vue

  ```vue
  <template>
    <!-- Grid布局 https://antdv.com/components/grid-cn/#components-grid-demo-flex-stretch -->
    <div id="globalHeader">
      <a-row :wrap="false">
        <!-- 图标和标题 -->
        <a-col flex="150px">
          <router-link to="/">
            <div class="title-bar">
              <img src="@/assets/ai-icon64.ico" alt="/user/logo" class="logo" />
              <div class="title">鱼图图</div>
            </div>
          </router-link>
        </a-col>
  
        <!-- 菜单 https://antdv.com/components/menu-cn/ -->
        <a-col flex="auto">
          <a-menu
            v-model:selectedKeys="current"
            mode="horizontal"
            :items="items"
            @click="doMeanClick"
          />
        </a-col>
  
        <!-- 登陆跳转 or 用户信息 -->
        <!-- 头像 https://antdv.com/components/avatar-cn/ -->
        <!-- 下拉菜单 https://antdv.com/components/dropdown-cn/ -->
        <a-col flex="130px">
          <div class="user-login-status" style="text-align: right">
            <!-- 已登录 -->
            <div v-if="loginUserStore.loginUser.id">
              <a-dropdown>
                <a-space>
                  {{ loginUserStore.loginUser.userName ?? '无名' }}
                  <a-avatar :src="loginUserStore.loginUser.userAvatar" :size="48" />
                </a-space>
                <template #overlay>
                  <a-menu>
                    <a-menu-item @click="doLogout"> <LogoutOutlined /> 退出登录 </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </div>
            <!-- 未登录 -->
            <div v-else>
              <a-button type="primary" href="/user/login">登陆</a-button>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>
  </template>
  
  <script setup lang="ts">
  import { h, ref } from 'vue'
  import { useRouter } from 'vue-router'
  import {
    HomeOutlined,
    LogoutOutlined,
    UserSwitchOutlined,
    MoreOutlined,
    LinkOutlined,
  } from '@ant-design/icons-vue' // https://antdv.com/components/icon-cn
  import { message, type MenuProps } from 'ant-design-vue'
  import { useLoginUserStore } from '@/stores/useLoginUserStore'
  import { userLogoutUsingPost } from '@/api/userController'
  
  const current = ref<string[]>(['']) // 高亮显示
  const router = useRouter() // 路由跳转
  const loginUserStore = useLoginUserStore() // 状态管理 当前用户
  
  // 路由信息
  const items = ref<MenuProps['items']>([
    {
      key: '/',
      label: '主页',
      title: '主页',
      icon: h(HomeOutlined),
    },
    {
      key: '/admin/userManage',
      label: '用户管理',
      title: '用户管理',
      icon: h(UserSwitchOutlined),
    },
    {
      key: '/about',
      label: '关于',
      title: '关于',
      icon: h(LinkOutlined),
    },
    // {
    //   key: '/others',
    //   label: h('a', { href: 'https://github.com/time1043', target: '_blank' }, 'time1043'),
    //   title: '其他',
    // },
  ])
  
  /**
   * 路由跳转事件
   * https://antdv.com/components/menu-cn/#api
   *
   * @param item 菜单项
   * @param key 菜单项的key
   * @param keyPath 菜单项的key路径
   */
  const doMeanClick = ({ item, key, keyPath }: any) => {
    router.push({ path: key })
    //current.value = [key]  // 高亮显示
  }
  
  /**
   * 钩子 每次跳转到新页面前 都会执行
   *
   * @param to 新页面
   * @param from 旧页面
   * @param next 后续处理
   */
  router.afterEach((to, from, next) => {
    current.value = [to.path] // 高亮显示
  })
  
  /**
   * 退出登录
   */
  const doLogout = async () => {
    const res = await userLogoutUsingPost()
    if (res.data.code === 0) {
      loginUserStore.setLoginUser({ userName: '未登录' })
      message.success('退出登录成功')
      await router.push({ path: '/user/login' })
    } else {
      message.error('退出登录失败 ' + res.data.message)
    }
  }
  </script>
  
  <style scoped>
  #globalHeader .title-bar {
    display: flex;
    align-items: center;
  }
  
  #globalHeader .title-bar .logo {
    width: 48px;
    height: 48px;
  }
  
  #globalHeader .title-bar .title {
    font-size: 24px;
    margin-left: 10px;
    color: rgb(86, 21, 203);
  }
  </style>
  
  ```
  
  



### 页面 错误

- src\pages\error\403.vue

  ```vue
  <template>
    <a-result status="403" title="403" sub-title="Sorry, you are not authorized to access this page.">
      <template #extra>
        <a-button type="primary" href="/">回到主页</a-button>
      </template>
    </a-result>
  </template>
  
  <script setup lang="ts"></script>
  
  <style scoped></style>
  
  ```

  src\pages\error\404.vue

  ```vue
  <template>
    <a-result status="500" title="500" sub-title="Sorry, the server is wrong.">
      <template #extra>
        <a-button type="primary">回到主页</a-button>
      </template>
    </a-result>
  </template>
  
  <script setup lang="ts"></script>
  
  <style scoped></style>
  
  ```

  src\pages\error\500.vue

  ```vue
  <template>
    <a-result status="404" title="404" sub-title="Sorry, the page you visited does not exist.">
      <template #extra>
        <a-button type="primary" href="/">回到主页</a-button>
      </template>
    </a-result>
  </template>
  
  <script setup lang="ts"></script>
  
  <style scoped></style>
  
  ```

  



#### 请求封装 axios ✔

- src\common\request.ts

  ```typescript
  // https://www.axios-http.cn/docs/instance
  // https://www.axios-http.cn/docs/interceptors
  import axios from 'axios'
  import { message } from 'ant-design-vue'
  import Config from './config'
  
  const { BACKEND_URL: baseURL } = Config
  const myAxios = axios.create({
    baseURL,
    timeout: 60000, // 60s=6wms
    withCredentials: true, // 允许跨域携带cookie
  })
  
  // 添加请求拦截器
  myAxios.interceptors.request.use(
    // 在发送请求之前做些什么
    function (config) {
      return config
    },
    // 对请求错误做些什么
    function (error) {
      return Promise.reject(error)
    },
  )
  
  // 添加响应拦截器
  myAxios.interceptors.response.use(
    // 2xx 范围内的状态码都会触发该函数
    // 对响应数据做点什么
    function (response) {
      const { data } = response
      // 未登录40100
      // 未登录，跳转到登录页面
      // 未登录，但不用跳转：已经在登陆页面、当前请求接口不用用户信息
      if (data.code === 40100) {
        if (
          !response.request.responseURL.includes('user/get/login') &&
          !window.location.pathname.includes('user/login') &&
          !window.location.pathname.includes('user/register')
        ) {
          message.warning('未登录，请先登录')
          window.location.href = `/user/login?redirect=${window.location.href}`
        }
      }
      return response
    },
    // 超出 2xx 范围的状态码都会触发该函数
    // 对响应错误做点什么
    function (error) {
      return Promise.reject(error)
    },
  )
  
  export default myAxios
  
  ```
  
  



#### 生成请求代码 openapi

- openapi.config.js

  ```js
  // npm run openapi
  // https://www.npmjs.com/package/@umijs/openapi
  import { generateService } from '@umijs/openapi'
  
  const baseURL = 'http://127.0.0.1:8011'
  generateService({
    requestLibPath: "import request from '@/common/request'", // 请求库
    schemaPath: `${baseURL}/api/v2/api-docs?group=default`, // 文档请求接口
    serversPath: './src', // 生成src/api
  })
  
  // import { healthUsingGet } from '@/api/mainController'
  // await healthUsingGet().then((res) => {
  //   console.log(res.data?.data)
  // })
  
  ```

  



#### 前端跨域配置 vite.nodejs ✔

- vite.config.ts

  ```typescript
  import { fileURLToPath, URL } from 'node:url'
  
  import { defineConfig } from 'vite'
  import vue from '@vitejs/plugin-vue'
  import vueDevTools from 'vite-plugin-vue-devtools'
  
  // https://vite.dev/config/
  export default defineConfig({
    // 127.0.0.1:5011/api -> 127.0.0.1:8011/api
    server: {
      proxy: {
        '/api': 'http://127.0.0.1:8011',
      },
    },
    plugins: [vue(), vueDevTools()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
  })
  
  ```

  src\common\config.ts

  ```typescript
  const Config = {
    //BACKEND_URL: 'http://localhost:8011', // 后端配置跨域
    BACKEND_URL: '',  // 前端配置跨域
  }
  
  export default Config
  
  ```

- 三方代理服务器

  whistle, nginx

  



#### 全局状态管理 ✔

- 全局状态

  所有页面全局共享的数据

  如当前用户信息

- 实现

  Way1 全局变量文件 (js变量的改变触发页面视图的更新 X)

  Way2 三方工具Vuex, [Pinia](https://pinia.vuejs.org/zh/introduction.html)(create-vue整合)

  



---

- src\stores\useLoginUserStore.ts

  ```typescript
  import { ref } from 'vue'
  import { defineStore } from 'pinia'
  import { getLoginUserUsingGet } from '@/api/userController'
  
  /**
   * 状态管理 (登录的用户信息)
   */
  export const useLoginUserStore = defineStore('loginUser', () => {
    const loginUser = ref<API.LoginUserVO>({ userName: '未登录' }) // 全局状态初始化
  
    // 后端获取登录用户信息
    async function fetchLoginUser() {
      const res = await getLoginUserUsingGet() // 后端接口
      if (res.data.code === 0 && res.data.data) {
        loginUser.value = res.data.data // 全局状态设置
      }
  
      // // mock 5m后自动登陆
      // setTimeout(() => {
      //   loginUser.value.id = 1 // 凭证
      //   loginUser.value.userName = 'oswin902'
      //   loginUser.value.userAvatar = 'https://avatars.githubusercontent.com/u/132178516?v=4'
      // }, 5000)
    }
  
    /**
     * 设置登录用户
     * @param newLoginUser 登录用户信息
     */
    function setLoginUser(newLoginUser: API.LoginUserVO) {
      loginUser.value = newLoginUser // 全局状态设置
    }
  
    return { loginUser, fetchLoginUser, setLoginUser }
  })
  
  // import { useLoginUserStore } from '@/stores/useLoginUserStore'
  // const loginUserStore = useLoginUserStore()
  // loginUserStore.fetchLoginUser()
  
  ```
  




## 后端接口 用户模块 ✔

### 代码生成 MyBatisX

- MyBatisX插件

  MyBatisX-Generator

  -> module path

  -> annotation (MyBatis-Plus3) options (comment lombok actualColumn Model) template (mybatis-plus3)

  



### 数据模型 (model.entity)

- src\main\java\com\oswin902\yututubackend\model\entity\User.java (ASSIGN_ID, TableLogic)

  ```java
  package com.oswin902.yututubackend.model.entity;
  
  import com.baomidou.mybatisplus.annotation.*;
  
  import java.io.Serializable;
  import java.util.Date;
  
  import lombok.Data;
  
  /**
   * 用户
   *
   * @TableName user
   */
  @TableName(value = "user")
  @Data
  public class User implements Serializable {
      /**
       * id
       */
      // https://baomidou.com/reference/annotation/#tableid
      @TableId(type = IdType.ASSIGN_ID)  // 防爬虫
      private Long id;
  
      /**
       * 账号
       */
      private String userAccount;
  
      /**
       * 密码
       */
      private String userPassword;
  
      /**
       * 用户昵称
       */
      private String userName;
  
      /**
       * 用户头像
       */
      private String userAvatar;
  
      /**
       * 用户简介
       */
      private String userProfile;
  
      /**
       * 用户角色：user/admin/vip/svip
       */
      private String userRole;
  
      /**
       * 会员过期时间
       */
      private Date vipExpireTime;
  
      /**
       * 会员兑换码
       */
      private String vipCode;
  
      /**
       * 会员编号
       */
      private String vipNumber;
  
      /**
       * 分享码
       */
      private String shareCode;
  
      /**
       * 被哪个用户邀请 用户id
       */
      private Long inviteUser;
  
      /**
       * 编辑时间
       */
      private Date editTime;
  
      /**
       * 创建时间
       */
      private Date createTime;
  
      /**
       * 更新时间
       */
      private Date updateTime;
  
      /**
       * 是否删除
       */
      @TableLogic  // 逻辑删除
      private Integer isDelete;
  
      @TableField(exist = false)
      private static final long serialVersionUID = 1L;
  }
  
  ```

  



### 枚举类 (model.enums)

- src\main\java\com\oswin902\yututubackend\model\enums\UserRoleEnum.java

  ```java
  package com.oswin902.yututubackend.model.enums;
  
  import cn.hutool.core.util.ObjUtil;
  import lombok.Getter;
  
  import java.util.HashMap;
  import java.util.Map;
  
  /**
   * 用户角色枚举
   */
  @Getter
  public enum UserRoleEnum {
      USER("用户", "user"),
      ADMIN("管理员", "admin"),
      VIP("VIP", "vip"),
      SVIP("SVIP", "svip");
  
      private final String text;
      private final String value;
  
      UserRoleEnum(String text, String value) {
          this.text = text;
          this.value = value;
      }
  
      /**
       * 根据值获取枚举
       *
       * @param value 值
       * @return 枚举
       */
      public static UserRoleEnum getEnumByValue(String value) {
          if (ObjUtil.isEmpty(value)) return null;
          // Map<String, UserRoleEnum> map = new HashMap<>();
          for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
              if (userRoleEnum.getValue().equals(value)) return userRoleEnum;
          }
          return null;
      }
  }
  
  ```

  



### 请求封装类 (model.dto)

- src\main\java\com\oswin902\yututubackend\model\dto\UserRegisterRequest.java

  ```java
  package com.oswin902.yututubackend.model.dto;
  
  import lombok.Data;
  
  import java.io.Serializable;
  
  /**
   * 用户注册请求对象
   * 需要序列化 Plugin: GeneratedSerialVersionUID
   */
  @Data
  public class UserRegisterRequest implements Serializable {
  
      // 序列化ID 【alt+insert】
      private static final long serialVersionUID = -3318155080588640494L;
  
      /**
       * 账号
       */
      private String userAccount;
  
      /**
       * 密码
       */
      private String userPassword;
  
      /**
       * 确认密码
       */
      private String checkPassword;
  }
  
  ```

  src\main\java\com\oswin902\yututubackend\model\dto\UserLoginRequest.java

  ```java
  package com.oswin902.yututubackend.model.dto;
  
  import lombok.Data;
  
  import java.io.Serializable;
  
  /**
   * 用户注册请求对象
   * 需要序列化 Plugin: GeneratedSerialVersionUID
   */
  @Data
  public class UserLoginRequest implements Serializable {
  
      // 序列化ID 【alt+insert】
      private static final long serialVersionUID = -8142046106039382504L;
      
      /**
       * 账号
       */
      private String userAccount;
  
      /**
       * 密码
       */
      private String userPassword;
  }
  
  ```

  



### 响应封装类 (model.vo)

- src\main\java\com\oswin902\yututubackend\model\vo\LoginUserVO.java

  ```java
  package com.oswin902.yututubackend.model.vo;
  
  import lombok.Data;
  
  import java.io.Serializable;
  import java.util.Date;
  
  /**
   * 已登录用户视图（脱敏）
   */
  @Data
  public class LoginUserVO implements Serializable {
  
      /**
       * id
       */
      private Long id;
  
      /**
       * 账号
       */
      private String userAccount;
  
      /**
       * 用户昵称
       */
      private String userName;
  
      /**
       * 用户头像
       */
      private String userAvatar;
  
      /**
       * 用户简介
       */
      private String userProfile;
  
      /**
       * 用户角色：user/admin
       */
      private String userRole;
  
      /**
       * 编辑时间
       */
      private Date editTime;
  
      /**
       * 创建时间
       */
      private Date createTime;
  
      /**
       * 更新时间
       */
      private Date updateTime;
  
      private static final long serialVersionUID = 1L;
  }
  
  ```

  



### 常量类 (constant)

- src\main\java\com\oswin902\yututubackend\constant\UserConstant.java

  ```java
  package com.oswin902.yututubackend.constant;
  
  /**
   * 用户模块的常量
   */
  public interface UserConstant {
  
      /**
       * 用户登录态的key
       */
      String USER_LOGIN_STATE = "user_login_state";
  
      // region 用户信息
  
      /**
       * 默认头像URL
       */
      String DEFAULT_AVATAR = "https://miro.medium.com/v2/resize:fit:640/format:webp/1*4j2A9niz0eq-mRaCPUffpg.png";
  
      /**
       * 默认密码
       */
      String DEFAULT_PASSWORD = "12345678";
  
      /**
       * 默认用户名
       */
      String DEFAULT_USERNAME = "未名";
  
      // endregion
  
      // region 权限
  
      /**
       * 默认角色
       */
      String DEFAULT_ROLE = "user";
  
      /**
       * 管理员角色
       */
      String ADMIN_ROLE = "admin";
  
      /**
       * VIP角色
       */
      String VIP_ROLE = "vip";
  
      /**
       * SVIP角色
       */
      String SVIP_ROLE = "svip";
  
      // endregion
  }
  
  ```
  
  



### 数据库访问层 (mapper) ✔





### 业务逻辑层 (service) ✔

- src\main\java\com\oswin902\yututubackend\service\UserService.java

  ```java
  package com.oswin902.yututubackend.service;
  
  import cn.hutool.http.server.HttpServerRequest;
  import com.oswin902.yututubackend.model.dto.UserRegisterRequest;
  import com.oswin902.yututubackend.model.entity.User;
  import com.baomidou.mybatisplus.extension.service.IService;
  import com.oswin902.yututubackend.model.vo.LoginUserVO;
  
  import javax.servlet.http.HttpServletRequest;
  
  /**
   * @author oswin902
   * @description 针对表【user(用户)】的数据库操作Service
   * @createDate 2024-12-22 20:29:23
   */
  public interface UserService extends IService<User> {
      /**
       * 用户注册
       *
       * @param userAccount   用户账号
       * @param userPassword  用户密码
       * @param checkPassword 确认密码
       * @return 用户ID
       */
      long userRegister(String userAccount, String userPassword, String checkPassword);
  
      /**
       * 用户登录
       *
       * @param userAccount  用户账号
       * @param userPassword 用户密码
       * @param request      HTTP请求 (登录态信息)
       * @return 用户信息(脱敏)
       */
      LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);
  
      /**
       * 获取登录用户信息 (不返回前端)
       *
       * @param request HTTP请求
       * @return 登录用户信息
       */
      User getLoginUser(HttpServletRequest request);
  
      /**
       * 用户注销 (清除登录态)
       *
       * @param request HTTP请求
       * @return 登录用户信息
       */
      boolean userLogout(HttpServletRequest request);
  
      /**
       * 密码加密加盐 【工具】
       *
       * @param userPassword 用户输入密码
       * @return 加密后的密码
       */
      String getEncryptPassword(String userPassword);
  
      /**
       * 获取用户信息(脱敏) 【工具】
       *
       * @param user 用户信息
       * @return 脱敏后的用户信息
       */
      LoginUserVO getLoginUserVO(User user);
  }
  
  ```

- src\main\java\com\oswin902\yututubackend\service\impl\UserServiceImpl.java

  // 校验参数非空 // 入库查询 用户账号不能重复 // 密码加密加盐 // 入库 // 返回用户id

  // 校验参数 // 密码加密加盐 // 入库查询 账号密码是否正确 // 保存用户登录态 // 返回用户信息(脱敏)

  ```java
  package com.oswin902.yututubackend.service.impl;
  
  import cn.hutool.core.bean.BeanUtil;
  import cn.hutool.core.util.StrUtil;
  import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
  import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
  import com.oswin902.yututubackend.constant.UserConstant;
  import com.oswin902.yututubackend.exception.ErrorCode;
  import com.oswin902.yututubackend.exception.ThrowUtils;
  import com.oswin902.yututubackend.mapper.UserMapper;
  import com.oswin902.yututubackend.model.entity.User;
  import com.oswin902.yututubackend.model.enums.UserRoleEnum;
  import com.oswin902.yututubackend.model.vo.LoginUserVO;
  import com.oswin902.yututubackend.service.UserService;
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.stereotype.Service;
  import org.springframework.util.DigestUtils;
  
  import javax.servlet.http.HttpServletRequest;
  
  /**
   * @author oswin902
   * @description 针对表【user(用户)】的数据库操作Service实现
   * @createDate 2024-12-22 20:29:23
   */
  @Service
  @Slf4j
  public class UserServiceImpl extends ServiceImpl<UserMapper, User>
          implements UserService {
  
      /**
       * 用户注册
       *
       * @param userAccount   用户账号
       * @param userPassword  用户密码
       * @param checkPassword 确认密码
       * @return 用户ID
       */
      @Override
      public long userRegister(String userAccount, String userPassword, String checkPassword) {
  
          // 校验参数 (非空 太短)
          ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword, checkPassword), ErrorCode.PARAMS_ERROR, "参数不能为空");
          ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "账号长度不能少于4位");
          ThrowUtils.throwIf(userPassword.length() < 6 || checkPassword.length() < 6, ErrorCode.PARAMS_ERROR, "密码长度不能少于6位");
          ThrowUtils.throwIf(!userPassword.equals(checkPassword), ErrorCode.PARAMS_ERROR, "两次密码输入不一致");
  
          // 入库查询 用户账号不能重复
          QueryWrapper<User> queryWrapper = new QueryWrapper<>();
          queryWrapper.eq("userAccount", userAccount);
          Long count = this.baseMapper.selectCount(queryWrapper);
          ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "用户账号已存在");
  
          // 密码加密加盐
          String encryptPassword = getEncryptPassword(userPassword);
  
          // 入库
          User user = new User();
          user.setUserAccount(userAccount);
          user.setUserPassword(encryptPassword);
          user.setUserName(UserConstant.DEFAULT_USERNAME);
          user.setUserRole(UserConstant.DEFAULT_ROLE);
          user.setUserAvatar(UserConstant.DEFAULT_AVATAR);
  
          boolean saveResult = this.save(user);
          ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "用户注册失败，数据库错误");
  
          // 返回用户id
          return user.getId();
      }
  
      /**
       * 用户登录
       *
       * @param userAccount  用户账号
       * @param userPassword 用户密码
       * @param request      HTTP请求 (登录态信息)
       * @return 用户信息(脱敏)
       */
      @Override
      public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
  
          // 校验参数
          ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword), ErrorCode.PARAMS_ERROR, "参数不能为空");
          ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "账号错误");
          ThrowUtils.throwIf(userPassword.length() < 6, ErrorCode.PARAMS_ERROR, "密码错误");
  
          // 密码加密加盐
          String encryptPassword = getEncryptPassword(userPassword);
  
          // 入库查询 账号密码是否正确
          QueryWrapper<User> queryWrapper = new QueryWrapper<>();
          queryWrapper.eq("userAccount", userAccount);
          queryWrapper.eq("userPassword", encryptPassword);
          User user = this.baseMapper.selectOne(queryWrapper);
          if (user == null) log.error("user login failed, userAccount cannot match userPassword");
          ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "账号或密码错误");
  
          // 保存用户登录态
          request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
  
          // 返回用户信息(脱敏)
          return this.getLoginUserVO(user);
      }
  
      /**
       * 获取登录用户信息 (不返回前端)
       *
       * @param request HTTP请求
       * @return 登录用户信息
       */
      @Override
      public User getLoginUser(HttpServletRequest request) {
  
          // 非必要不用缓存 (数据不一致)
          Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
          User currentUser = (User) userObj;
          ThrowUtils.throwIf(userObj == null || currentUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
  
          // 数据库查询 (不追求极致性能优化)
          Long userId = currentUser.getId();
          currentUser = this.getById(userId);
          ThrowUtils.throwIf(currentUser == null, ErrorCode.SYSTEM_ERROR, "用户信息获取失败");
  
          return currentUser;
      }
  
      /**
       * 用户注销 (清除登录态)
       *
       * @param request HTTP请求
       * @return 登录用户信息
       */
      @Override
      public boolean userLogout(HttpServletRequest request) {
  
          // 判断是否登陆
          Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
          ThrowUtils.throwIf(userObj == null, ErrorCode.OPERATION_ERROR, "用户未登录");
  
          // 清除登录态
          request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
          return true;
      }
  
      /**
       * 密码加密加盐 【工具】
       *
       * @param userPassword 用户输入密码
       * @return 加密后的密码
       */
      @Override
      public String getEncryptPassword(String userPassword) {
          final String SALT = "RunYouClearBoyAndRememberMe";
          return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
      }
  
      /**
       * 获取用户信息(脱敏) 【工具】
       *
       * @param user 用户信息
       * @return 脱敏后的用户信息
       */
      @Override
      public LoginUserVO getLoginUserVO(User user) {
          if (user == null) return null;
  
          LoginUserVO loginUserVO = new LoginUserVO();
          BeanUtil.copyProperties(user, loginUserVO);
          return loginUserVO;
      }
  }
  
  ```
  
  



### 接口访问层 (controller) ✔

- src\main\java\com\oswin902\yututubackend\controller\UserController.java

  ```java
  package com.oswin902.yututubackend.controller;
  
  import com.oswin902.yututubackend.annotation.AuthCheck;
  import com.oswin902.yututubackend.common.BaseResponse;
  import com.oswin902.yututubackend.common.ResultUtils;
  import com.oswin902.yututubackend.constant.UserConstant;
  import com.oswin902.yututubackend.exception.ErrorCode;
  import com.oswin902.yututubackend.exception.ThrowUtils;
  import com.oswin902.yututubackend.model.dto.UserLoginRequest;
  import com.oswin902.yututubackend.model.dto.UserRegisterRequest;
  import com.oswin902.yututubackend.model.entity.User;
  import com.oswin902.yututubackend.model.vo.LoginUserVO;
  import com.oswin902.yututubackend.service.UserService;
  import org.springframework.web.bind.annotation.*;
  
  import javax.annotation.Resource;
  import javax.servlet.http.HttpServletRequest;
  
  @RestController
  @RequestMapping("/user")
  public class UserController {
  
      @Resource
      private UserService userService;
  
      /**
       * 用户注册
       *
       * @param userRegisterRequest 用户注册信息
       * @return 注册成功的用户ID
       */
      @PostMapping("/register")
      //@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)  // admin才允许注册
      public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
          ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
  
          // Plugin: GenerateGetterAndSetter
          String userAccount = userRegisterRequest.getUserAccount();
          String userPassword = userRegisterRequest.getUserPassword();
          String checkPassword = userRegisterRequest.getCheckPassword();
  
          long result = userService.userRegister(userAccount, userPassword, checkPassword);
          return ResultUtils.success(result);
      }
  
      /**
       * 用户登录
       *
       * @param userLoginRequest 用户登录信息
       * @return 登录成功的用户信息
       */
      @PostMapping("/login")
      public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
          ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
  
          String userAccount = userLoginRequest.getUserAccount();
          String userPassword = userLoginRequest.getUserPassword();
  
          LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
          return ResultUtils.success(loginUserVO);
      }
  
      /**
       * 获取当前用户信息
       *
       * @param request HTTP请求
       * @return 当前用户信息(脱敏)
       */
      @GetMapping("/current")
      public BaseResponse<LoginUserVO> getCurrentUser(HttpServletRequest request) {
          User loginUser = userService.getLoginUser(request);
          return ResultUtils.success(userService.getLoginUserVO(loginUser));  // 脱敏
      }
  
      /**
       * 用户退出登录
       *
       * @param request HTTP请求
       * @return 成功或失败
       */
      @PostMapping("/logout")
      public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
          boolean result = userService.userLogout(request);
          return ResultUtils.success(result);
      }
  }
  
  ```

  



### 测试接口





## 后端接口 用户管理

### 注解

- src\main\java\com\oswin902\yututubackend\annotation\AuthCheck.java

  ```java
  package com.oswin902.yututubackend.annotation;
  
  import java.lang.annotation.ElementType;
  import java.lang.annotation.Retention;
  import java.lang.annotation.RetentionPolicy;
  import java.lang.annotation.Target;
  
  /**
   * 用户必须登陆 且进一步划分
   */
  @Target(ElementType.METHOD)  // 作用于方法上
  @Retention(RetentionPolicy.RUNTIME)  // 运行时注解
  public @interface AuthCheck {
  
      /**
       * 必须具有某个角色
       */
      String mustRole() default "";
      //String[] mustRoles() default {""};  // 数组 满足任一角色即可放行
  }
  
  ```

  



### AOP

- src\main\java\com\oswin902\yututubackend\aop\AuthInterceptor.java

  ```java
  package com.oswin902.yututubackend.aop;
  
  import com.oswin902.yututubackend.annotation.AuthCheck;
  import com.oswin902.yututubackend.exception.ErrorCode;
  import com.oswin902.yututubackend.exception.ThrowUtils;
  import com.oswin902.yututubackend.model.entity.User;
  import com.oswin902.yututubackend.model.enums.UserRoleEnum;
  import com.oswin902.yututubackend.service.UserService;
  import org.aspectj.lang.ProceedingJoinPoint;
  import org.aspectj.lang.annotation.Around;
  import org.aspectj.lang.annotation.Aspect;
  import org.springframework.stereotype.Component;
  import org.springframework.web.context.request.RequestAttributes;
  import org.springframework.web.context.request.RequestContextHolder;
  import org.springframework.web.context.request.ServletRequestAttributes;
  
  import javax.annotation.Resource;
  import javax.servlet.http.HttpServletRequest;
  
  @Aspect
  @Component
  public class AuthInterceptor {
  
      @Resource
      private UserService userService;
  
      /**
       * 执行拦截
       *
       * @param joinPoint 切入点 (对哪些方法进行拦截)
       * @param authCheck 权限校验注解
       * @return 放行或拦截
       */
      @Around("@annotation(authCheck)")
      public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
  
          RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
          HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
          User loginUser = userService.getLoginUser(request);  // 获取登录用户 (未登录拦截)
          UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());  // 字符串转枚举
  
          String mustRole = authCheck.mustRole();
          UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);  // 字符串转枚举
  
          // 某功能不需要额外权限 放行
          if (mustRoleEnum == null) return joinPoint.proceed();
  
          // 某功能需要特定权限 校验权限
          ThrowUtils.throwIf(userRoleEnum == null, ErrorCode.NO_AUTH_ERROR, "用户无权限");
          // 当前功能需要admin权限 && 用户不是admin (vip, svip)
          ThrowUtils.throwIf(UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum),
                  ErrorCode.NO_AUTH_ERROR, "当前用户不是admin");
          ThrowUtils.throwIf(UserRoleEnum.VIP.equals(mustRoleEnum) && !UserRoleEnum.VIP.equals(userRoleEnum),
                  ErrorCode.NO_AUTH_ERROR, "当前用户不是vip");
          ThrowUtils.throwIf(UserRoleEnum.SVIP.equals(mustRoleEnum) && !UserRoleEnum.SVIP.equals(userRoleEnum),
                  ErrorCode.NO_AUTH_ERROR, "当前用户不是svip");
          // 最后放行
          return joinPoint.proceed();
  
      }
  }
  
  ```

  



### 请求封装类 (model.dto)

- src\main\java\com\oswin902\yututubackend\model\dto\user\UserAddRequest.java 【新增】

  ```java
  package com.oswin902.yututubackend.model.dto.user;
  
  import lombok.Data;
  
  import java.io.Serializable;
  
  /**
   * 用户创建请求
   */
  @Data
  public class UserAddRequest implements Serializable {
  
      /**
       * 用户昵称
       */
      private String userName;
  
      /**
       * 账号
       */
      private String userAccount;
  
      /**
       * 用户头像
       */
      private String userAvatar;
  
      /**
       * 用户简介
       */
      private String userProfile;
  
      /**
       * 用户角色: user, admin
       */
      private String userRole;
  
      private static final long serialVersionUID = 1L;
  }
  
  ```

- src\main\java\com\oswin902\yututubackend\model\dto\user\UserUpdateRequest.java 【管理员更新】

  ```java
  package com.oswin902.yututubackend.model.dto.user;
  
  import lombok.Data;
  
  import java.io.Serializable;
  
  /**
   * 更新用户请求
   */
  @Data
  public class UserUpdateRequest implements Serializable {
  
      /**
       * id
       */
      private Long id;
  
      /**
       * 用户昵称
       */
      private String userName;
  
      /**
       * 用户头像
       */
      private String userAvatar;
  
      /**
       * 简介
       */
      private String userProfile;
  
      /**
       * 用户角色：user/admin
       */
      private String userRole;
  
      private static final long serialVersionUID = 1L;
  }
  
  ```

- src\main\java\com\oswin902\yututubackend\model\dto\user\UserQueryRequest.java 【查询】

  ```java
  package com.oswin902.yututubackend.model.dto.user;
  
  import com.oswin902.yututubackend.common.PageRequest;
  import lombok.Data;
  import lombok.EqualsAndHashCode;
  
  import java.io.Serializable;
  
  /**
   * 用户查询请求
   */
  
  @EqualsAndHashCode(callSuper = true)
  @Data
  public class UserQueryRequest extends PageRequest implements Serializable {
  
      /**
       * id
       */
      private Long id;
  
      /**
       * 用户昵称
       */
      private String userName;
  
      /**
       * 账号
       */
      private String userAccount;
  
      /**
       * 简介
       */
      private String userProfile;
  
      /**
       * 用户角色：user/admin/ban
       */
      private String userRole;
  
      private static final long serialVersionUID = 1L;
  }
  
  ```

  



### 响应封装类 (model.vo)

- src\main\java\com\oswin902\yututubackend\model\vo\UserVO.java

  ```java
  package com.oswin902.yututubackend.model.vo;
  
  import lombok.Data;
  
  import java.io.Serializable;
  import java.util.Date;
  
  /**
   * 用户视图（脱敏-信息更少）
   */
  @Data
  public class UserVO implements Serializable {
  
      /**
       * id
       */
      private Long id;
  
      /**
       * 账号
       */
      private String userAccount;
  
      /**
       * 用户昵称
       */
      private String userName;
  
      /**
       * 用户头像
       */
      private String userAvatar;
  
      /**
       * 用户简介
       */
      private String userProfile;
  
      /**
       * 用户角色：user/admin
       */
      private String userRole;
  
      /**
       * 创建时间
       */
      private Date createTime;
  
      private static final long serialVersionUID = 1L;
  }
  
  ```

  



### 数据库访问层 (mapper) ✔





### 业务逻辑层 (service) ✔

- src\main\java\com\oswin902\yututubackend\service\UserService.java

  ```java
  package com.oswin902.yututubackend.service;
  
  import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
  import com.oswin902.yututubackend.model.dto.user.UserQueryRequest;
  import com.oswin902.yututubackend.model.entity.User;
  import com.baomidou.mybatisplus.extension.service.IService;
  import com.oswin902.yututubackend.model.vo.LoginUserVO;
  import com.oswin902.yututubackend.model.vo.UserVO;
  
  import javax.servlet.http.HttpServletRequest;
  import java.util.List;
  
  /**
   * @author oswin902
   * @description 针对表【user(用户)】的数据库操作Service
   * @createDate 2024-12-22 20:29:23
   */
  public interface UserService extends IService<User> {
  
      /**
       * 用户注册
       *
       * @param userAccount   用户账号
       * @param userPassword  用户密码
       * @param checkPassword 确认密码
       * @return 用户ID
       */
      long userRegister(String userAccount, String userPassword, String checkPassword);
  
      /**
       * 用户登录
       *
       * @param userAccount  用户账号
       * @param userPassword 用户密码
       * @param request      HTTP请求 (登录态信息)
       * @return 用户信息(脱敏)
       */
      LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);
  
      /**
       * 获取登录用户信息 (不返回前端)
       *
       * @param request HTTP请求
       * @return 登录用户信息
       */
      User getLoginUser(HttpServletRequest request);
  
      /**
       * 用户注销 (清除登录态)
       *
       * @param request HTTP请求
       * @return 登录用户信息
       */
      boolean userLogout(HttpServletRequest request);
  
      /**
       * 密码加密加盐 【工具】
       *
       * @param userPassword 用户输入密码
       * @return 加密后的密码
       */
      String getEncryptPassword(String userPassword);
  
      /**
       * 获取用户信息(脱敏) 【工具】
       *
       * @param user 用户信息
       * @return 脱敏后的用户信息
       */
      LoginUserVO getLoginUserVO(User user);
  
      /**
       * 获取用户信息(脱敏-信息更少) 【工具】
       *
       * @param user 用户信息
       * @return 脱敏后的用户信息
       */
      UserVO getUserVO(User user);
  
      /**
       * 获取用户信息(脱敏-信息更少) 【工具】
       *
       * @param userList 用户信息列表
       * @return 脱敏后的用户信息列表
       */
      List<UserVO> getUserVOList(List<User> userList);
  
      /**
       * 获取查询条件 【工具】
       *
       * @param userQueryRequest 用户查询请求
       * @return QueryWrapper
       */
      QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
  
      /**
       * 判断用户是否为管理员 【工具】
       *
       * @param user 用户
       * @return 是否为管理员
       */
      boolean isAdmin(User user);
  }
  
  ```

- src\main\java\com\oswin902\yututubackend\service\impl\UserServiceImpl.java

  ```java
  package com.oswin902.yututubackend.service.impl;
  
  import cn.hutool.core.bean.BeanUtil;
  import cn.hutool.core.collection.CollUtil;
  import cn.hutool.core.util.ObjUtil;
  import cn.hutool.core.util.StrUtil;
  import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
  import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
  import com.oswin902.yututubackend.constant.UserConstant;
  import com.oswin902.yututubackend.exception.ErrorCode;
  import com.oswin902.yututubackend.exception.ThrowUtils;
  import com.oswin902.yututubackend.mapper.UserMapper;
  import com.oswin902.yututubackend.model.dto.user.UserQueryRequest;
  import com.oswin902.yututubackend.model.entity.User;
  import com.oswin902.yututubackend.model.enums.UserRoleEnum;
  import com.oswin902.yututubackend.model.vo.LoginUserVO;
  import com.oswin902.yututubackend.model.vo.UserVO;
  import com.oswin902.yututubackend.service.UserService;
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.stereotype.Service;
  import org.springframework.util.DigestUtils;
  
  import javax.servlet.http.HttpServletRequest;
  import java.util.ArrayList;
  import java.util.List;
  import java.util.stream.Collectors;
  
  /**
   * @author oswin902
   * @description 针对表【user(用户)】的数据库操作Service实现
   * @createDate 2024-12-22 20:29:23
   */
  @Service
  @Slf4j
  public class UserServiceImpl extends ServiceImpl<UserMapper, User>
          implements UserService {
  
      /**
       * 用户注册
       *
       * @param userAccount   用户账号
       * @param userPassword  用户密码
       * @param checkPassword 确认密码
       * @return 用户ID
       */
      @Override
      public long userRegister(String userAccount, String userPassword, String checkPassword) {
  
          // 校验参数 (非空 太短)
          ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword, checkPassword), ErrorCode.PARAMS_ERROR, "参数不能为空");
          ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "账号长度不能少于4位");
          ThrowUtils.throwIf(userPassword.length() < 6 || checkPassword.length() < 6, ErrorCode.PARAMS_ERROR, "密码长度不能少于6位");
          ThrowUtils.throwIf(!userPassword.equals(checkPassword), ErrorCode.PARAMS_ERROR, "两次密码输入不一致");
  
          // 入库查询 用户账号不能重复
          QueryWrapper<User> queryWrapper = new QueryWrapper<>();
          queryWrapper.eq("userAccount", userAccount);
          Long count = this.baseMapper.selectCount(queryWrapper);
          ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "用户账号已存在");
  
          // 密码加密加盐
          String encryptPassword = getEncryptPassword(userPassword);
  
          // 入库
          User user = new User();
          user.setUserAccount(userAccount);
          user.setUserPassword(encryptPassword);
          user.setUserName(UserConstant.DEFAULT_USERNAME);
          user.setUserRole(UserConstant.DEFAULT_ROLE);
          user.setUserAvatar(UserConstant.DEFAULT_AVATAR);
  
          boolean saveResult = this.save(user);
          ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "用户注册失败，数据库错误");
  
          // 返回用户id
          return user.getId();
      }
  
      /**
       * 用户登录
       *
       * @param userAccount  用户账号
       * @param userPassword 用户密码
       * @param request      HTTP请求 (登录态信息)
       * @return 用户信息(脱敏)
       */
      @Override
      public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
  
          // 校验参数
          ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword), ErrorCode.PARAMS_ERROR, "参数不能为空");
          ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "账号错误");
          ThrowUtils.throwIf(userPassword.length() < 6, ErrorCode.PARAMS_ERROR, "密码错误");
  
          // 密码加密加盐
          String encryptPassword = getEncryptPassword(userPassword);
  
          // 入库查询 账号密码是否正确
          QueryWrapper<User> queryWrapper = new QueryWrapper<>();
          queryWrapper.eq("userAccount", userAccount);
          queryWrapper.eq("userPassword", encryptPassword);
          User user = this.baseMapper.selectOne(queryWrapper);
          if (user == null) log.error("user login failed, userAccount cannot match userPassword");
          ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "账号或密码错误");
  
          // 保存用户登录态
          request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
  
          // 返回用户信息(脱敏)
          return this.getLoginUserVO(user);
      }
  
      /**
       * 获取登录用户信息 (不返回前端)
       *
       * @param request HTTP请求
       * @return 登录用户信息
       */
      @Override
      public User getLoginUser(HttpServletRequest request) {
  
          // 非必要不用缓存 (数据不一致)
          Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
          User currentUser = (User) userObj;
          ThrowUtils.throwIf(userObj == null || currentUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
  
          // 数据库查询 (不追求极致性能优化)
          Long userId = currentUser.getId();
          currentUser = this.getById(userId);
          ThrowUtils.throwIf(currentUser == null, ErrorCode.SYSTEM_ERROR, "用户信息获取失败");
  
          return currentUser;
      }
  
      /**
       * 用户注销 (清除登录态)
       *
       * @param request HTTP请求
       * @return 登录用户信息
       */
      @Override
      public boolean userLogout(HttpServletRequest request) {
  
          // 判断是否登陆
          Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
          ThrowUtils.throwIf(userObj == null, ErrorCode.OPERATION_ERROR, "用户未登录");
  
          // 清除登录态
          request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
          return true;
      }
  
      /**
       * 密码加密加盐 【工具】
       *
       * @param userPassword 用户输入密码
       * @return 加密后的密码
       */
      @Override
      public String getEncryptPassword(String userPassword) {
          final String SALT = "RunYouClearBoyAndRememberMe";
          return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
      }
  
      /**
       * 获取用户信息(脱敏) 【工具】
       *
       * @param user 用户信息
       * @return 脱敏后的用户信息
       */
      @Override
      public LoginUserVO getLoginUserVO(User user) {
          if (user == null) return null;
  
          LoginUserVO loginUserVO = new LoginUserVO();
          BeanUtil.copyProperties(user, loginUserVO);
          return loginUserVO;
      }
  
      /**
       * 获取用户信息(脱敏-信息更少) 【工具】
       *
       * @param user 用户信息
       * @return 脱敏后的用户信息
       */
      @Override
      public UserVO getUserVO(User user) {
          if (user == null) return null;
  
          UserVO userVO = new UserVO();
          BeanUtil.copyProperties(user, userVO);
          return userVO;
      }
  
      /**
       * 获取用户信息(脱敏-信息更少) 【工具】
       *
       * @param userList 用户信息列表
       * @return 脱敏后的用户信息列表
       */
      @Override
      public List<UserVO> getUserVOList(List<User> userList) {
          if (CollUtil.isEmpty(userList)) return new ArrayList<>();
  
          return userList.stream()
                  .map(this::getUserVO)
                  .collect(Collectors.toList());
      }
  
      /**
       * 获取查询条件 【工具】
       *
       * @param userQueryRequest 用户查询请求
       * @return QueryWrapper
       */
      @Override
      public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
          ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR, "用户查询请求为空");
  
          Long id = userQueryRequest.getId();
          String userName = userQueryRequest.getUserName();
          String userAccount = userQueryRequest.getUserAccount();
          String userProfile = userQueryRequest.getUserProfile();
          String userRole = userQueryRequest.getUserRole();
          String sortField = userQueryRequest.getSortField();
          String sortOrder = userQueryRequest.getSortOrder();
  
          QueryWrapper<User> queryWrapper = new QueryWrapper<>();
          // 精确查询：id、角色
          //queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
          queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
          // 模糊查询：账号、用户名、简介
          queryWrapper.like(ObjUtil.isNotNull(id), "id", id);
          queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
          queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
          queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
          // 排序：ascend, descend
          queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
  
          return queryWrapper;
      }
  
      /**
       * 判断用户是否为管理员 【工具】
       *
       * @param user 用户
       * @return 是否为管理员
       */
      @Override
      public boolean isAdmin(User user) {
          //if (user == null) return false;
          //if (UserRoleEnum.ADMIN.getValue().equals(user.getUserRole())) return false;
          //return true;  // 放行
          return (user != null) && (UserRoleEnum.ADMIN.getValue().equals(user.getUserRole()));
      }
  }
  
  ```
  
  



### 接口访问层 (controller) ✔

- src\main\java\com\oswin902\yututubackend\controller\UserController.java

  ```java
  package com.oswin902.yututubackend.controller;
  
  import cn.hutool.core.bean.BeanUtil;
  import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
  import com.oswin902.yututubackend.annotation.AuthCheck;
  import com.oswin902.yututubackend.common.BaseResponse;
  import com.oswin902.yututubackend.common.DeleteRequest;
  import com.oswin902.yututubackend.common.ResultUtils;
  import com.oswin902.yututubackend.constant.UserConstant;
  import com.oswin902.yututubackend.exception.ErrorCode;
  import com.oswin902.yututubackend.exception.ThrowUtils;
  import com.oswin902.yututubackend.model.dto.user.*;
  import com.oswin902.yututubackend.model.entity.User;
  import com.oswin902.yututubackend.model.vo.LoginUserVO;
  import com.oswin902.yututubackend.model.vo.UserVO;
  import com.oswin902.yututubackend.service.UserService;
  import org.springframework.beans.BeanUtils;
  import org.springframework.web.bind.annotation.*;
  
  import javax.annotation.Resource;
  import javax.servlet.http.HttpServletRequest;
  import java.util.List;
  
  @RestController
  @RequestMapping("/user")
  public class UserController {
  
      @Resource
      private UserService userService;
  
      /**
       * 用户注册
       *
       * @param userRegisterRequest 用户注册信息
       * @return 注册成功的用户ID
       */
      @PostMapping("/register")
      //@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)  // admin才允许注册
      public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
          ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
  
          // Plugin: GenerateGetterAndSetter
          String userAccount = userRegisterRequest.getUserAccount();
          String userPassword = userRegisterRequest.getUserPassword();
          String checkPassword = userRegisterRequest.getCheckPassword();
  
          long result = userService.userRegister(userAccount, userPassword, checkPassword);
          return ResultUtils.success(result);
      }
  
      /**
       * 用户登录
       *
       * @param userLoginRequest 用户登录信息
       * @return 登录成功的用户信息
       */
      @PostMapping("/login")
      public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
          ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
  
          String userAccount = userLoginRequest.getUserAccount();
          String userPassword = userLoginRequest.getUserPassword();
  
          LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
          return ResultUtils.success(loginUserVO);
      }
  
      /**
       * 获取当前用户信息
       *
       * @param request HTTP请求
       * @return 当前用户信息(脱敏)
       */
      @GetMapping("/current")
      public BaseResponse<LoginUserVO> getCurrentUser(HttpServletRequest request) {
          User loginUser = userService.getLoginUser(request);
          return ResultUtils.success(userService.getLoginUserVO(loginUser));  // 脱敏
      }
  
      /**
       * 用户退出登录
       *
       * @param request HTTP请求
       * @return 成功或失败
       */
      @PostMapping("/logout")
      public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
          boolean result = userService.userLogout(request);
          return ResultUtils.success(result);
      }
  
      /**
       * 用户添加 【用户管理 admin】
       *
       * @param userAddRequest 用户添加信息
       * @return 注册成功的用户ID
       */
      @PostMapping("/add")
      @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
      public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
          ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR, "请求参数不能为空");
  
          User user = new User();
          BeanUtil.copyProperties(userAddRequest, user);
          String encryptPassword = userService.getEncryptPassword(UserConstant.DEFAULT_PASSWORD);  // 密码加密加盐
          user.setUserPassword(encryptPassword);  // 指定默认密码
          //user.setUserRole(UserConstant.DEFAULT_ROLE); // 用户指定 不用默认
  
          // 入库
          boolean result = userService.save(user);
          ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "添加用户失败");
          return ResultUtils.success(user.getId());
      }
  
      /**
       * 根据id获取用户 【用户管理 admin】
       *
       * @param id 用户ID
       * @return 用户信息
       */
      @GetMapping("/get")
      @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
      public BaseResponse<User> getUserById(Long id) {
          ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "请求参数错误");
          User user = userService.getById(id);
          ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
          return ResultUtils.success(user);
      }
  
      /**
       * 根据id获取用户VO(信息更少) 【user】
       *
       * @param id 用户ID
       * @return 用户信息
       */
      @GetMapping("/get/vo")
      public BaseResponse<UserVO> getUserVOById(Long id) {
          BaseResponse<User> response = getUserById(id);
          User user = response.getData();
          return ResultUtils.success(userService.getUserVO(user));  // 脱敏
      }
  
      /**
       * 用户删除 【用户管理 admin】
       *
       * @param deleteRequest 删除请求(id)
       * @return 成功或失败
       */
      @PostMapping("/delete")
      @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
      public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
          ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0,
                  ErrorCode.PARAMS_ERROR, "请求参数错误");
          boolean result = userService.removeById(deleteRequest.getId());
          return ResultUtils.success(result);
      }
  
      /**
       * 用户更新 【用户管理 admin】
       *
       * @param userUpdateRequest 更新请求
       * @return 成功或失败
       */
      @PostMapping("/update")
      @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
      public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
          ThrowUtils.throwIf(userUpdateRequest == null || userUpdateRequest.getId() == null,
                  ErrorCode.PARAMS_ERROR, "请求参数错误");
  
          User user = new User();
          BeanUtils.copyProperties(userUpdateRequest, user);
  
          boolean result = userService.updateById(user);
          ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新用户失败");
          return ResultUtils.success(true);
      }
  
      /**
       * 用户列表 【用户管理 admin】
       *
       * @param userQueryRequest 查询请求
       * @return 用户列表
       */
      @PostMapping("/list/page/vo")
      @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
      public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
          ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数错误");
  
          long current = userQueryRequest.getCurrent();
          long pageSize = userQueryRequest.getPageSize();
          Page<User> userPage = userService.page(new Page<>(current, pageSize), userService.getQueryWrapper(userQueryRequest));
  
          Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
          List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
          userVOPage.setRecords(userVOList);
          return ResultUtils.success(userVOPage);
      }
  }
  
  ```

  



### 配置 (mybatis-plus分页插件)

- src\main\java\com\oswin902\yututubackend\config\MybatisPlusConfig.java

  ```java
  package com.oswin902.yututubackend.config;
  
  import com.baomidou.mybatisplus.annotation.DbType;
  import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
  import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
  import org.mybatis.spring.annotation.MapperScan;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  
  /**
   * MybatisPlus配置类 (于 v3.5.9 起，PaginationInnerInterceptor 已分离出来)
   * https://baomidou.com/plugins/pagination/
   */
  @Configuration
  @MapperScan("com.oswin902.yututubackend.mapper")
  public class MybatisPlusConfig {
  
      /**
       * 添加分页插件
       */
      @Bean
      public MybatisPlusInterceptor mybatisPlusInterceptor() {
          MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
          interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL)); // 如果配置多个插件, 切记分页最后添加
          // 如果有多数据源可以不配具体类型, 否则都建议配上具体的 DbType
          return interceptor;
      }
  }
  	
  ```

  



### 配置 (前端精读丢失)

- src\main\java\com\oswin902\yututubackend\config\JsonConfig.java

  ```java
  package com.oswin902.yututubackend.config;
  
  import com.fasterxml.jackson.databind.ObjectMapper;
  import com.fasterxml.jackson.databind.module.SimpleModule;
  import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
  import org.springframework.boot.jackson.JsonComponent;
  import org.springframework.context.annotation.Bean;
  import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
  
  /**
   * Spring MVC Json 配置
   */
  @JsonComponent
  public class JsonConfig {
  
      /**
       * 添加 Long 转 json 精度丢失的配置
       */
      @Bean
      public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
          ObjectMapper objectMapper = builder.createXmlMapper(false).build();
          SimpleModule module = new SimpleModule();
          module.addSerializer(Long.class, ToStringSerializer.instance);
          module.addSerializer(Long.TYPE, ToStringSerializer.instance);
          objectMapper.registerModule(module);
          return objectMapper;
      }
  }
  
  ```

  



### 测试接口





## 前端页面 用户模块

### 页面 登陆页

- src\pages\user\UserLoginPage.vue

  ```vue
  <template>
    <!-- 表单form https://antdv.com/components/form-cn/ -->
    <div id="userLoginPage">
      <!-- 标题 -->
      <h2 class="title">鱼图图 - 用户登陆</h2>
      <div class="desc">企业级智能协作云图库</div>
  
      <!-- 登陆表单 -->
      <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
        <a-form-item
          name="userAccount"
          :rules="[
            { required: true, message: '用户账号是必填项' },
            { min: 4, message: '用户名长度不能少于4位' },
          ]"
        >
          <a-input v-model:value="formState.userAccount" placeholder="请输入用户账号" />
        </a-form-item>
  
        <a-form-item
          name="userPassword"
          :rules="[
            { required: true, message: '用户密码是必填项' },
            { min: 6, message: '密码长度不能少于6位' },
          ]"
        >
          <a-input-password v-model:value="formState.userPassword" placeholder="请输入用户密码" />
        </a-form-item>
  
        <div class="tips">没有账号? <RouterLink to="/user/register">去注册</RouterLink></div>
  
        <a-form-item>
          <a-button type="primary" html-type="submit" style="width: 100%">登陆</a-button>
        </a-form-item>
      </a-form>
    </div>
  </template>
  
  <script setup lang="ts">
  import { userLoginUsingPost } from '@/api/userController'
  import router from '@/router'
  import { useLoginUserStore } from '@/stores/useLoginUserStore'
  import { message } from 'ant-design-vue'
  import { reactive } from 'vue'
  
  // 全局状态管理
  const loginUserStore = useLoginUserStore()
  
  // 用于接受用户表单的值
  const formState = reactive<API.UserLoginRequest>({
    userAccount: '',
    userPassword: '',
  })
  
  /**
   * 登陆表单提交
   * @param values 用户表单值
   */
  const handleSubmit = async (values: any) => {
    const res = await userLoginUsingPost(values) // 后端接口
  
    // 登陆成功 (登录态保存在全局状态中)
    if (res.data.code === 0 && res.data.data) {
      await loginUserStore.fetchLoginUser()
      message.success('登陆成功')
  
      // 重定向 首页
      const redirect = new URLSearchParams(window.location.search).get('redirect')
      if (redirect) window.location.href = redirect
      else router.push({ path: '/', replace: true })
    } else {
      message.error('登陆失败 ' + res.data.message)
    }
  }
  </script>
  
  <style scoped>
  #userLoginPage {
    max-width: 360px;
    margin: 0 auto;
    padding: 20px;
    border: 1px solid #f0f0f0;
    border-radius: 5px;
  }
  
  .title {
    text-align: center;
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 16px;
  }
  
  .desc {
    text-align: center;
    font-size: 14px;
    color: #999;
    margin-bottom: 16px;
  }
  
  .tips {
    text-align: right;
    color: #bbb;
    margin-bottom: 16px;
  }
  </style>
  
  ```
  
  



### 页面 注册页

- src\pages\user\UserRegisterPage.vue

  ```vue
  <template>
    <!-- 表单form https://antdv.com/components/form-cn/ -->
    <div id="userRegisterPage">
      <!-- 标题 -->
      <h2 class="title">鱼图图 - 用户注册</h2>
      <div class="desc">企业级智能协作云图库</div>
  
      <!-- 登陆表单 -->
      <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
        <a-form-item
          name="userAccount"
          :rules="[
            { required: true, message: '用户账号是必填项' },
            { min: 4, message: '用户名长度不能少于4位' },
          ]"
        >
          <a-input v-model:value="formState.userAccount" placeholder="请输入用户账号" />
        </a-form-item>
  
        <a-form-item
          name="userPassword"
          :rules="[
            { required: true, message: '用户密码是必填项' },
            { min: 6, message: '密码长度不能少于6位' },
          ]"
        >
          <a-input-password v-model:value="formState.userPassword" placeholder="请输入用户密码" />
        </a-form-item>
  
        <a-form-item
          name="checkPassword"
          :rules="[
            { required: true, message: '确认密码是必填项' },
            { min: 6, message: '密码长度不能少于6位' },
          ]"
        >
          <a-input-password v-model:value="formState.checkPassword" placeholder="请输入确认密码" />
        </a-form-item>
  
        <div class="tips">已有账号? <RouterLink to="/user/login">去登陆</RouterLink></div>
  
        <a-form-item>
          <a-button type="primary" html-type="submit" style="width: 100%">注册</a-button>
        </a-form-item>
      </a-form>
    </div>
  </template>
  
  <script setup lang="ts">
  import { userRegisterUsingPost } from '@/api/userController'
  import router from '@/router'
  import { message } from 'ant-design-vue'
  import { reactive } from 'vue'
  
  // 用于接受用户表单的值
  const formState = reactive<API.UserRegisterRequest>({
    userAccount: '',
    userPassword: '',
    checkPassword: '',
  })
  
  // 注册表单提交
  const handleSubmit = async (values: any) => {
    // 检验参数 两次密码不一致
    if (values.userPassword !== values.checkPassword) {
      message.error('两次密码不一致')
      return
    }
  
    const res = await userRegisterUsingPost(values) // 后端接口
  
    // 注册成功 (跳转到登陆页面)
    if (res.data.code === 0 && res.data.data) {
      message.success('注册成功')
      router.push({ path: '/user/login', replace: true })
    } else {
      message.error('注册失败 ' + res.data.message)
    }
  }
  </script>
  
  <style scoped>
  #userRegisterPage {
    max-width: 360px;
    margin: 0 auto;
    padding: 20px;
    border: 1px solid #f0f0f0;
    border-radius: 5px;
  }
  
  .title {
    text-align: center;
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 16px;
  }
  
  .desc {
    text-align: center;
    font-size: 14px;
    color: #999;
    margin-bottom: 16px;
  }
  
  .tips {
    text-align: right;
    color: #bbb;
    margin-bottom: 16px;
  }
  </style>
  
  ```
  
  




### 页面 用户管理页

- src\pages\admin\UserManagePage.vue

  ```vue
  <template>
    <div id="userManagePage">
      <!-- 上方 表单搜索栏 https://antdv.com/components/form-cn/ -->
      <a-form layout="inline" :model="searchParams" @finish="doSearch">
        <a-form-item>
          <a-radio-group v-model:value="searchParams.userRole">
            <a-radio-button value="user">普通用户</a-radio-button>
            <a-radio-button value="vip">VIP</a-radio-button>
            <a-radio-button value="svip">SVIP</a-radio-button>
            <a-radio-button value="admin">管理员</a-radio-button>
          </a-radio-group>
        </a-form-item>
  
        <a-form-item>
          <a-input v-model:value="searchParams.id" placeholder="输入ID" allow-clear />
        </a-form-item>
  
        <a-form-item>
          <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" allow-clear />
        </a-form-item>
  
        <a-form-item>
          <a-input v-model:value="searchParams.userName" placeholder="输入用户名" allow-clear />
        </a-form-item>
  
        <a-form-item>
          <a-button type="primary" html-type="submit">搜索</a-button>
        </a-form-item>
        <a-form-item>
          <a-button type="default" @click="doReset">重置</a-button>
        </a-form-item>
      </a-form>
      <div style="margin-bottom: 16px" />
  
      <!-- 下方 表格(支持分页) https://antdv.com/components/table-cn/ -->
      <!-- :scroll="{ x: '0', y: '700px' }" -->
      <a-table
        :columns="columns"
        :data-source="dataList"
        :pagination="pagination"
        @change="doTableChange"
      >
        <!-- 表格体 插槽 -->
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'userAvatar'">
            <a-image :src="record.userAvatar" :width="120" />
          </template>
  
          <template v-else-if="column.dataIndex === 'id'">
            <a-typography-paragraph :copyable="{ tooltip: false }">
              {{ record.id }}
            </a-typography-paragraph>
          </template>
  
          <template v-else-if="column.dataIndex === 'userRole'">
            <div v-if="record.userRole === 'admin'">
              <a-tag color="green">管理员</a-tag>
            </div>
            <div v-else-if="record.userRole === 'vip'">
              <a-tag color="yellow">VIP</a-tag>
            </div>
            <div v-else-if="record.userRole === 'svip'">
              <a-tag color="yellow">SVIP</a-tag>
            </div>
            <div v-else>
              <a-tag color="blue">普通用户</a-tag>
            </div>
          </template>
  
          <template v-else-if="column.dataIndex === 'createTime'">
            {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
          </template>
  
          <template v-else-if="column.key === 'action'">
            <a-button danger @click="doDelete(record.id)"> 删除 </a-button>
            <!-- <a-modal v-model:open="openDoDelete" title="确认删除" @ok="doDelete(record.id)">
              <p>{{ `确认删除用户 ${record.userName} 吗？` }}</p>
            </a-modal> -->
          </template>
        </template>
      </a-table>
    </div>
  </template>
  
  <script setup lang="ts">
  import { computed, onMounted, reactive, ref } from 'vue'
  import { deleteUserUsingPost, listUserVoByPageUsingPost } from '@/api/userController'
  import { message } from 'ant-design-vue'
  
  import dayjs from 'dayjs'
  
  // 页面加载时获取数据，请求一次
  onMounted(() => {
    fetchData()
  })
  
  // 定义列名
  const columns = [
    {
      title: 'id',
      dataIndex: 'id', // dataIndex 后端对应
      width: 90, // 宽度
      // ellipsis: true, // 显示省略号
    },
    {
      title: '账号',
      dataIndex: 'userAccount',
    },
    {
      title: '用户名',
      dataIndex: 'userName',
    },
    {
      title: '头像',
      dataIndex: 'userAvatar',
      width: 150, // 宽度
    },
    {
      title: '简介',
      dataIndex: 'userProfile',
      ellipsis: true, // 显示省略号
    },
    {
      title: '用户角色',
      dataIndex: 'userRole',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      ellipsis: true, // 显示省略号
    },
    {
      title: '操作',
      key: 'action', // key 操作空间自定义
    },
  ]
  
  // 定义数据
  // ref 响应式变量 数组整体变化才会触发重新渲染
  const dataList = ref<API.UserVO[]>([])
  const total = ref<number>(0)
  
  // 搜索条件
  // reactive 响应式变量 对象中任何一个属性发生变化都会触发重新渲染
  const searchParams = reactive<API.UserQueryRequest>({
    current: 1,
    pageSize: 10,
    sortField: 'createTime',
    sortOrder: 'ascend',
    userRole: '',
    id: undefined,
  })
  
  // 分页参数
  const pagination = computed(() => {
    return {
      current: searchParams.current,
      pageSize: searchParams.pageSize,
      total: total.value,
      showSizeChanger: true,
      showTotal: (total: number) => `共 ${total} 条`,
    }
  })
  
  /**
   * 获取数据
   */
  const fetchData = async () => {
    const res = await listUserVoByPageUsingPost({ ...searchParams }) // 后端接口
    if (res.data.code === 0 && res.data.data) {
      dataList.value = res.data.data.records ?? []
      total.value = Number(res.data.data.total) ?? 0
    } else {
      message.error('获取数据失败 ' + res.data.message)
    }
  }
  
  /**
   * 表格变化之后 重新获取数据
   * @param page 分页参数
   */
  const doTableChange = (page: any) => {
    searchParams.current = page.current
    searchParams.pageSize = page.pageSize
    fetchData()
  }
  
  /**
   * 搜索数据
   */
  const doSearch = () => {
    searchParams.current = 1 // 重置页码
    fetchData()
  }
  
  /**
   * 重置搜索条件
   */
  const doReset = () => {
    searchParams.userAccount = ''
    searchParams.userName = ''
    searchParams.userRole = ''
    searchParams.id = undefined
    doSearch()
  }
  
  /**
   * 删除用户 (admin)
   * @param id 要删除的用户id
   */
  // const openDoDelete = ref<boolean>(false)
  // const doDeleteShow = () => {
  //   openDoDelete.value = true
  // }
  const doDelete = async (id: number) => {
    if (!id) return
    const res = await deleteUserUsingPost({ id }) // 后端接口
    if (res.data.code === 0) {
      // openDoDelete.value = false
      message.success('删除成功')
      fetchData() // 刷新数据
    } else {
      message.error('删除失败 ' + res.data.message)
    }
  }
  </script>
  
  <style scoped>
  /* 选择标签 <a-form-item> */
  .ant-form-item {
    margin-top: 10px;
    margin-bottom: 10px;
  }
  </style>
  
  ```

  



### 权限控制 ✔

- src\common\access.ts

  ```typescript
  import router from '@/router'
  import { useLoginUserStore } from '@/stores/useLoginUserStore'
  import { message } from 'ant-design-vue'
  
  // 是否为首次获取用户信息
  let firstFetchLoginUser: boolean = true
  
  /**
   * 全局权限校验 (在用户进入页面前 判断用户权限)
   */
  router.beforeEach(async (to, from, next) => {
    // 用户权限
    const loginUserStore = useLoginUserStore() // 全局状态
    let loginUser = loginUserStore.loginUser
  
    // 确保页面刷新时 首次加载时 能等待后端返回用户信息后再校验权限
    if (firstFetchLoginUser) {
      await loginUserStore.fetchLoginUser() // 后端接口
      loginUser = loginUserStore.loginUser
      firstFetchLoginUser = false // flag
    }
  
    // 页面权限
    // 自定义权限校验规则 管理员才能访问/admin
    const toUrl = to.fullPath
    if (toUrl.startsWith('/admin')) {
      if (!loginUser || loginUser.userRole !== 'admin') {
        message.error('你没有权限访问该页面')
  
        //next(`/user/login?redirect=${toUrl}`)
        next('/error/403')
        return
      }
    }
    next() // 放行
  })
  
  ```

  



## 后端接口 图片模块 (通用) ✔

### 配置 (COS)

- src\main\java\com\oswin902\yututubackend\config\CosClientConfig.java

  ```java
  package com.oswin902.yututubackend.config;
  
  import com.qcloud.cos.COSClient;
  import com.qcloud.cos.ClientConfig;
  import com.qcloud.cos.auth.BasicCOSCredentials;
  import com.qcloud.cos.auth.COSCredentials;
  import com.qcloud.cos.http.HttpProtocol;
  import com.qcloud.cos.region.Region;
  import lombok.Data;
  import org.springframework.boot.context.properties.ConfigurationProperties;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  
  @Configuration
  @ConfigurationProperties(prefix = "cos.client")
  @Data
  public class CosClientConfig {
  
      /**
       * 域名
       */
      private String host;
  
      /**
       * secretId
       */
      private String secretId;
  
      /**
       * 密钥（注意不要泄露）
       */
      private String secretKey;
  
      /**
       * 区域
       */
      private String region;
  
      /**
       * 桶名
       */
      private String bucket;
  
      @Bean
      public COSClient cosClient() {
          // 1 初始化用户身份信息（secretId, secretKey）。
          // SECRETID 和 SECRETKEY 请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
          COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
  
          // 2 设置 bucket 的地域, COS 地域的简称请参见 https://cloud.tencent.com/document/product/436/6224
          // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
          ClientConfig clientConfig = new ClientConfig(new Region(region));
          // 这里建议设置使用 https 协议  // 从 5.6.54 版本开始，默认使用了 https
          clientConfig.setHttpProtocol(HttpProtocol.https);
  
          // 3 生成 cos 客户端。
          return new COSClient(cred, clientConfig);
      }
  }
  
  ```

  



### 常量类 (constant)

- src\main\java\com\oswin902\yututubackend\constant\PictureConstant.java

  ```java
  package com.oswin902.yututubackend.constant;
  
  import java.util.Arrays;
  import java.util.List;
  
  public interface PictureConstant {
  
      /**
       * 文件大小 1M
       */
      Long ONE_M = 1024 * 1024L;  // 1m=1024k 1k=1024b
  
      /**
       * 允许上传的文件后缀列表
       */
      List<String> ALLOWED_FORMAT_LIST = Arrays.asList("jpg", "png", "jpeg", "gif", "bmp", "webp");
  }
  
  ```

  



### 请求封装类 (model.dto)

- src\main\java\com\oswin902\yututubackend\model\dto\file\UploadPictureResult.java

  ```java
  package com.oswin902.yututubackend.model.dto.file;
  
  import lombok.Data;
  
  /**
   * 上传图片的结果
   */
  @Data
  public class UploadPictureResult {
  
      /**
       * 图片地址
       */
      private String url;
  
      /**
       * 缩略图 url
       */
      private String thumbnailUrl;
  
      /**
       * 图片名称
       */
      private String picName;
  
      /**
       * 文件体积
       */
      private Long picSize;
  
      /**
       * 图片宽度
       */
      private int picWidth;
  
      /**
       * 图片高度
       */
      private int picHeight;
  
      /**
       * 图片宽高比
       */
      private Double picScale;
  
      /**
       * 图片格式
       */
      private String picFormat;
  
      /**
       * 图片主色调
       */
      private String picColor;
  }
  
  ```

  



### 通用处理：上传下载

- src\main\java\com\oswin902\yututubackend\manager\CosManager.java

  ```java
  package com.oswin902.yututubackend.manager;
  
  import com.oswin902.yututubackend.config.CosClientConfig;
  import com.qcloud.cos.COSClient;
  import com.qcloud.cos.model.COSObject;
  import com.qcloud.cos.model.GetObjectRequest;
  import com.qcloud.cos.model.PutObjectRequest;
  import com.qcloud.cos.model.PutObjectResult;
  import com.qcloud.cos.model.ciModel.persistence.PicOperations;
  import org.springframework.stereotype.Component;
  
  import javax.annotation.Resource;
  import java.io.File;
  
  /**
   * 文件上传和下载 【可复用】
   */
  @Component
  public class CosManager {
  
      @Resource
      private CosClientConfig cosClientConfig;
  
      @Resource
      private COSClient cosClient;
  
      /**
       * 上传对象
       *
       * @param key  唯一键 (唯一位置)
       * @param file 文件
       * @return PutObjectResult
       */
      public PutObjectResult putObject(String key, File file) {
          // https://cloud.tencent.com/document/product/436/65935#f1b2b774-d9cf-4645-8dea-b09a23045503
          PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
          return cosClient.putObject(putObjectRequest);
      }
  
      /**
       * 下载对象
       *
       * @param key 唯一键
       */
      public COSObject getObject(String key) {
          GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
          return cosClient.getObject(getObjectRequest);
      }
  
      /**
       * 上传对象（附带图片信息）
       *
       * @param key  唯一键
       * @param file 文件
       */
      public PutObjectResult putPictureObject(String key, File file) {
          PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
  
          // 对图片进行处理（获取基本信息也被视作为一种图片的处理）
          // https://cloud.tencent.com/document/product/436/55378
          // https://cloud.tencent.com/document/product/436/113308
  
          PicOperations picOperations = new PicOperations();
          picOperations.setIsPicInfo(1);  // 1 表示返回原图信息
  
          //// 图片处理规则列表
          //List<PicOperations.Rule> rules = new ArrayList<>();
          //// 1. 图片压缩（转成 webp 格式）
          //String webpKey = FileUtil.mainName(key) + ".webp";
          //PicOperations.Rule compressRule = new PicOperations.Rule();
          //compressRule.setFileId(webpKey);
          //compressRule.setBucket(cosClientConfig.getBucket());
          //compressRule.setRule("imageMogr2/format/webp");
          //rules.add(compressRule);
          //// 2. 缩略图处理，仅对 > 20 KB 的图片生成缩略图
          //if (file.length() > 2 * 1024) {
          //    PicOperations.Rule thumbnailRule = new PicOperations.Rule();
          //    // 拼接缩略图的路径
          //    String thumbnailKey = FileUtil.mainName(key) + "_thumbnail." + FileUtil.getSuffix(key);
          //    thumbnailRule.setFileId(thumbnailKey);
          //    thumbnailRule.setBucket(cosClientConfig.getBucket());
          //    // 缩放规则 /thumbnail/<Width>x<Height>>（如果大于原图宽高，则不处理）
          //    thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>", 256, 256));
          //    rules.add(thumbnailRule);
          //}
  
          // 构造处理参数
          //picOperations.setRules(rules);
          putObjectRequest.setPicOperations(picOperations);
          return cosClient.putObject(putObjectRequest);
      }
  
      /**
       * 删除对象
       *
       * @param key 唯一键
       */
      public void deleteObject(String key) {
          cosClient.deleteObject(cosClientConfig.getBucket(), key);
      }
  }
  
  ```
  
  



### 通用处理：附加信息

- \src\main\java\com\oswin902\yututubackend\manager\FileManager.java

  图片校验、图片上传路径、图片解析

  ```java
  package com.oswin902.yututubackend.manager;
  
  import cn.hutool.core.date.DateUtil;
  import cn.hutool.core.io.FileUtil;
  import cn.hutool.core.util.NumberUtil;
  import cn.hutool.core.util.RandomUtil;
  import com.oswin902.yututubackend.common.ResultUtils;
  import com.oswin902.yututubackend.config.CosClientConfig;
  import com.oswin902.yututubackend.constant.PictureConstant;
  import com.oswin902.yututubackend.exception.BusinessException;
  import com.oswin902.yututubackend.exception.ErrorCode;
  import com.oswin902.yututubackend.exception.ThrowUtils;
  import com.oswin902.yututubackend.model.dto.file.UploadPictureResult;
  import com.qcloud.cos.model.PutObjectResult;
  import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.stereotype.Service;
  import org.springframework.web.multipart.MultipartFile;
  
  import javax.annotation.Resource;
  import java.io.File;
  import java.util.Date;
  
  /**
   * 文件服务
   */
  @Slf4j
  @Service  // 偏业务
  public class FileManager {
  
      @Resource
      private CosClientConfig cosClientConfig;
  
      @Resource
      private CosManager cosManager;
  
      /**
       * 上传图片
       *
       * @param multipartFile    文件
       * @param uploadPathPrefix 上传路径前缀
       * @return 上传图片结果
       */
      public UploadPictureResult uploadPicture(MultipartFile multipartFile, String uploadPathPrefix) {
          // 校验图片
          validPicture(multipartFile);
  
          // 图片上传地址
          String uuid = RandomUtil.randomString(16);  // 文件名称相同 也不意味着是同一个文件
          String originalFilename = multipartFile.getOriginalFilename();  // 原始文件名
          String formatDate = DateUtil.formatDate(new Date());  // 时间戳
          // 自己约定文件上传路径 而不是使用原始文件名称 可增加安全性
          String uploadFilename = String.format("%s_%s.%s", formatDate, uuid, FileUtil.getSuffix(originalFilename));
          String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);
  
          // 上传文件
          File file = null;
          try {
              file = File.createTempFile(uploadPath, null);  // 创建临时文件
              multipartFile.transferTo(file);  // 将上传的文件写入临时文件
              PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
  
              // 解析结果并返回
              // https://cloud.tencent.com/product/ci 【需要开数据万象CI服务】
              ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();  // 图片信息对象
              String format = imageInfo.getFormat();
              int picWidth = imageInfo.getWidth();
              int picHeight = imageInfo.getHeight();
              double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();  // 计算宽高比
              // 封装返回结果
              UploadPictureResult uploadPictureResult = new UploadPictureResult();
              uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
              uploadPictureResult.setPicName(FileUtil.mainName(originalFilename));
              uploadPictureResult.setPicSize(FileUtil.size(file));
              uploadPictureResult.setPicFormat(format);
              uploadPictureResult.setPicWidth(picWidth);
              uploadPictureResult.setPicHeight(picHeight);
              uploadPictureResult.setPicScale(picScale);
              //uploadPictureResult.setThumbnailUrl();
              //uploadPictureResult.setPicColor();
              return uploadPictureResult;
  
          } catch (Exception e) {
              log.error("图片上传到对象存储失败 ", e);
              throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
          } finally {
              // 临时文件清理
              deleteTempFile(file);  // ctrl, alt, m
          }
      }
  
      /**
       * 校验图片 (若不符合要求 则抛出异常) 【工具类】
       *
       * @param multipartFile 文件
       */
      private void validPicture(MultipartFile multipartFile) {
          ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
  
          // 校验文件大小
          long fileSize = multipartFile.getSize();
          ThrowUtils.throwIf(fileSize > 2 * PictureConstant.ONE_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过2MB");
  
          // 校验文件格式
          String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());  // 文件后缀
          ThrowUtils.throwIf(!PictureConstant.ALLOWED_FORMAT_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件格式不正确");
      }
  
      /**
       * 清理临时文件 【工具类】
       *
       * @param file 文件
       */
      public static void deleteTempFile(File file) {
          if (file == null) return;
  
          boolean deleteResult = file.delete();  // 删除临时文件
          if (!deleteResult) log.error("file delete error, filepath = {}", file.getAbsoluteFile());
      }
  
  }
  
  ```
  
  



### 测试接口

- src\main\java\com\oswin902\yututubackend\controller\FileController.java

  ```java
  package com.oswin902.yututubackend.controller;
  
  import com.oswin902.yututubackend.annotation.AuthCheck;
  import com.oswin902.yututubackend.common.BaseResponse;
  import com.oswin902.yututubackend.common.ResultUtils;
  import com.oswin902.yututubackend.constant.UserConstant;
  import com.oswin902.yututubackend.exception.BusinessException;
  import com.oswin902.yututubackend.exception.ErrorCode;
  import com.oswin902.yututubackend.manager.CosManager;
  import com.qcloud.cos.model.COSObject;
  import com.qcloud.cos.model.COSObjectInputStream;
  import com.qcloud.cos.utils.IOUtils;
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.web.bind.annotation.*;
  import org.springframework.web.multipart.MultipartFile;
  
  import javax.annotation.Resource;
  import javax.servlet.http.HttpServletResponse;
  import java.io.File;
  
  @Slf4j
  @RestController
  @RequestMapping("/file")
  public class FileController {
  
      @Resource
      private CosManager cosManager;
  
  
      /**
       * 测试文件上传【admin】
       *
       * @param multipartFile 上传文件(form表单接收)
       * @return 返回上传成功后的文件地址
       */
      @PostMapping("/test/upload")
      @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
      public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile) {
  
          // 文件目录
          String filename = multipartFile.getOriginalFilename();
          String filepath = String.format("/test/%s", filename);
  
          File file = null;
          try {
              // 上传文件
              file = File.createTempFile(filepath, null);  // 创建临时文件
              multipartFile.transferTo(file);  // 将上传的文件写入临时文件
              cosManager.putObject(filepath, file);
              // 返回可访问地址
              return ResultUtils.success(filepath);
  
          } catch (Exception e) {
              log.error("file upload error, filepath = {}", filepath, e);
              throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
          } finally {
              if (file != null) {
                  boolean delete = file.delete();  // 删除临时文件
                  if (!delete) log.error("file delete error, filepath = {}", filepath);
              }
          }
      }
  
      /**
       * 测试文件下载【admin】
       * 实现方式：后端流转发
       *
       * @param filepath 文件路径
       * @param response 响应
       */
      @GetMapping("/test/download")
      @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
      public void testDownloadFile(String filepath, HttpServletResponse response) throws Exception {
  
          COSObjectInputStream cosObjectInputStream = null;
  
          try {
              COSObject cosObject = cosManager.getObject(filepath);
              cosObjectInputStream = cosObject.getObjectContent();
              byte[] byteArray = IOUtils.toByteArray(cosObjectInputStream);  // 转换为字节数组
  
              // 设置响应头
              response.setContentType("application/octet-stream;charset=UTF-8");
              response.setHeader("Content-Disposition", "attachment;filename=" + filepath);
  
              // 写入响应
              response.getOutputStream().write(byteArray);
              response.getOutputStream().flush();  // 刷新
  
          } catch (Exception e) {
              log.error("file download error, filepath = {}", filepath, e);
              throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件下载失败");
          } finally {
              if (cosObjectInputStream != null) cosObjectInputStream.close();
          }
      }
  
  }
  
  ```

  



## 后端接口 图片模块

### 代码生成 MyBatisX

- MyBatisX插件

  MyBatisX-Generator

  -> module path

  -> annotation (MyBatis-Plus3) options (comment lombok actualColumn Model) template (mybatis-plus3)

  



### 数据模型 (model.entity)

- src\main\java\com\oswin902\yututubackend\model\entity\Picture.java

  ```java
  package com.oswin902.yututubackend.model.entity;
  
  import com.baomidou.mybatisplus.annotation.*;
  
  import java.io.Serializable;
  import java.util.Date;
  
  import lombok.Data;
  
  /**
   * 图片
   *
   * @TableName picture
   */
  @TableName(value = "picture")
  @Data
  public class Picture implements Serializable {
      /**
       * id
       */
      @TableId(type = IdType.ASSIGN_ID)  // 防爬虫
      private Long id;
  
      /**
       * 图片 url
       */
      private String url;
  
      /**
       * 图片名称
       */
      private String name;
  
      /**
       * 简介
       */
      private String introduction;
  
      /**
       * 分类
       */
      private String category;
  
      /**
       * 标签（JSON 数组）
       */
      private String tags;
  
      /**
       * 创建用户 id
       */
      private Long userId;
  
      /**
       * 图片体积
       */
      private Long picSize;
  
      /**
       * 图片宽度
       */
      private Integer picWidth;
  
      /**
       * 图片高度
       */
      private Integer picHeight;
  
      /**
       * 图片宽高比例
       */
      private Double picScale;
  
      /**
       * 图片格式
       */
      private String picFormat;
  
      /**
       * 审核状态：0-待审核; 1-通过; 2-拒绝
       */
      private Integer reviewStatus;
  
      /**
       * 审核信息
       */
      private String reviewMessage;
  
      /**
       * 审核人 ID
       */
      private Long reviewerId;
  
      /**
       * 审核时间
       */
      private Date reviewTime;
  
      /**
       * 创建时间
       */
      private Date createTime;
  
      /**
       * 编辑时间
       */
      private Date editTime;
  
      /**
       * 更新时间
       */
      private Date updateTime;
  
      /**
       * 是否删除
       */
      @TableLogic  // 逻辑删除
      private Integer isDelete;
  
      @TableField(exist = false)
      private static final long serialVersionUID = 1L;
  }
  
  ```

  



### 请求封装类 (model.dto)

- src\main\java\com\oswin902\yututubackend\model\dto\picture\PictureUploadRequest.java

  ```java
  package com.oswin902.yututubackend.model.dto.picture;
  
  import lombok.Data;
  
  import java.io.Serializable;
  
  /**
   * 图片上传请求
   */
  @Data
  public class PictureUploadRequest implements Serializable {
  
      /**
       * 图片 id（用于修改）
       */
      private Long id;
  
      /**
       * 文件地址
       */
      private String fileUrl;
  
      /**
       * 图片名称
       */
      private String picName;
  
      /**
       * 空间 id
       */
      private Long spaceId;
  
      private static final long serialVersionUID = 1L;
  }
  
  ```

  



### 响应封装类 (model.vo)

- src\main\java\com\oswin902\yututubackend\model\vo\PictureVO.java

  ```java
  package com.oswin902.yututubackend.model.vo;
  
  import cn.hutool.json.JSONUtil;
  import com.oswin902.yututubackend.model.entity.Picture;
  import lombok.Data;
  import org.springframework.beans.BeanUtils;
  
  import java.io.Serializable;
  import java.util.Date;
  import java.util.List;
  
  @Data
  public class PictureVO implements Serializable {
  
      /**
       * id
       */
      private Long id;
  
      /**
       * 图片 url
       */
      private String url;
  
      /**
       * 缩略图 url
       */
      private String thumbnailUrl;
  
      /**
       * 图片名称
       */
      private String name;
  
      /**
       * 简介
       */
      private String introduction;
  
      /**
       * 标签
       */
      private List<String> tags;  // 需要转换 jsonStr -> List<String>
  
      /**
       * 分类
       */
      private String category;
  
      /**
       * 文件体积
       */
      private Long picSize;
  
      /**
       * 图片宽度
       */
      private Integer picWidth;
  
      /**
       * 图片高度
       */
      private Integer picHeight;
  
      /**
       * 图片比例
       */
      private Double picScale;
  
      /**
       * 图片格式
       */
      private String picFormat;
  
      /**
       * 图片主色调
       */
      private String picColor;
  
      /**
       * 用户 id
       */
      private Long userId;
  
      /**
       * 空间 id
       */
      private Long spaceId;
  
      /**
       * 创建时间
       */
      private Date createTime;
  
      /**
       * 编辑时间
       */
      private Date editTime;
  
      /**
       * 更新时间
       */
      private Date updateTime;
  
      /**
       * 创建用户信息
       */
      private UserVO user;  // 需要转换 userId -> UserVO
  
      private static final long serialVersionUID = 1L;
  
      /**
       * 封装类转对象
       */
      public static Picture voToObj(PictureVO pictureVO) {
          if (pictureVO == null) return null;
  
          Picture picture = new Picture();
          BeanUtils.copyProperties(pictureVO, picture);
          picture.setTags(JSONUtil.toJsonStr(pictureVO.getTags()));  // 类型不同，需要转换
  
          return picture;
      }
  
      /**
       * 对象转封装类
       */
      public static PictureVO objToVo(Picture picture) {
          if (picture == null) return null;
  
          PictureVO pictureVO = new PictureVO();
          BeanUtils.copyProperties(picture, pictureVO);
          pictureVO.setTags(JSONUtil.toList(picture.getTags(), String.class));  // 类型不同，需要转换
  
          return pictureVO;
      }
  }
  
  ```

  



### 数据库访问层 (mapper) ✔





### 业务逻辑层 (service) ✔

- src\main\java\com\oswin902\yututubackend\service\PictureService.java

  ```java
  package com.oswin902.yututubackend.service;
  
  import com.baomidou.mybatisplus.extension.service.IService;
  import com.oswin902.yututubackend.model.dto.picture.PictureUploadRequest;
  import com.oswin902.yututubackend.model.entity.Picture;
  import com.oswin902.yututubackend.model.entity.User;
  import com.oswin902.yututubackend.model.vo.PictureVO;
  import org.springframework.web.multipart.MultipartFile;
  
  /**
   * @author huangyingzhu
   * @description 针对表【picture(图片)】的数据库操作Service
   * @createDate 2024-12-26 16:06:44
   */
  public interface PictureService extends IService<Picture> {
  
      /**
       * 上传图片
       *
       * @param multipartFile        图片
       * @param pictureUploadRequest 图片上传请求
       * @param loginUser            用户 【鉴权】
       * @return 图片信息
       */
      PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser);
  
  }
  
  ```

- src\main\java\com\oswin902\yututubackend\service\impl\PictureServiceImpl.java

  ```java
  package com.oswin902.yututubackend.service.impl;
  
  import cn.hutool.core.bean.BeanUtil;
  import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
  import com.oswin902.yututubackend.exception.ErrorCode;
  import com.oswin902.yututubackend.exception.ThrowUtils;
  import com.oswin902.yututubackend.manager.FileManager;
  import com.oswin902.yututubackend.mapper.PictureMapper;
  import com.oswin902.yututubackend.model.dto.file.UploadPictureResult;
  import com.oswin902.yututubackend.model.dto.picture.PictureUploadRequest;
  import com.oswin902.yututubackend.model.entity.Picture;
  import com.oswin902.yututubackend.model.entity.User;
  import com.oswin902.yututubackend.model.vo.PictureVO;
  import com.oswin902.yututubackend.service.PictureService;
  import org.springframework.stereotype.Service;
  import org.springframework.web.multipart.MultipartFile;
  
  import javax.annotation.Resource;
  import java.util.Date;
  
  /**
   * @author huangyingzhu
   * @description 针对表【picture(图片)】的数据库操作Service实现
   * @createDate 2024-12-26 16:06:44
   */
  @Service
  public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
          implements PictureService {
  
      @Resource
      private FileManager fileManager;
  
      /**
       * 上传图片
       *
       * @param multipartFile        图片
       * @param pictureUploadRequest 图片上传请求
       * @param loginUser            用户 【鉴权】
       * @return 图片信息
       */
      @Override
      public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser) {
  
          // 校验参数
          ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
          ThrowUtils.throwIf(pictureUploadRequest == null, ErrorCode.PARAMS_ERROR, "图片上传请求参数为空");
  
          // 判断新增还是删除
          Long pictureId = pictureUploadRequest.getId();
          // 若为更新 则判断图片是否存在
          if (pictureId != null) {
              boolean exists = this.lambdaQuery().eq(Picture::getId, pictureId).exists(); // 数据库操作
              ThrowUtils.throwIf(!exists, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
          }
  
          // 上传图片
          String uploadPathPrefix = String.format("public/%s", loginUser.getId());  // 按照用户id划分目录
          UploadPictureResult uploadPictureResult = fileManager.uploadPicture(multipartFile, uploadPathPrefix);
  
          Picture picture = new Picture();
          picture.setUrl(uploadPictureResult.getUrl());
          picture.setName(uploadPictureResult.getPicName());  // 有些名字不太一样
          picture.setPicSize(uploadPictureResult.getPicSize());
          picture.setPicWidth(uploadPictureResult.getPicWidth());
          picture.setPicHeight(uploadPictureResult.getPicHeight());
          picture.setPicScale(uploadPictureResult.getPicScale());
          picture.setPicFormat(uploadPictureResult.getPicFormat());
          picture.setUserId(loginUser.getId());
          BeanUtil.copyProperties(pictureUploadRequest, picture);
  
          // 操作数据库
          // 若pictureId不为空 则为更新 更新需要补充id和编辑时间
          if (pictureId != null) {
              picture.setId(pictureId);
              picture.setUpdateTime(new Date());
          }
          // 若pictureId为空 则为新增
          boolean result = this.saveOrUpdate(picture);
          ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "图片上传失败，数据库操作失败");
  
          return PictureVO.objToVo(picture);
      }
  }
  
  ```
  
  



### 接口访问层 (controller) ✔

- src\main\java\com\oswin902\yututubackend\controller\PictureController.java

  ```java
  package com.oswin902.yututubackend.controller;
  
  import com.oswin902.yututubackend.annotation.AuthCheck;
  import com.oswin902.yututubackend.common.BaseResponse;
  import com.oswin902.yututubackend.common.ResultUtils;
  import com.oswin902.yututubackend.constant.UserConstant;
  import com.oswin902.yututubackend.model.dto.picture.PictureUploadRequest;
  import com.oswin902.yututubackend.model.entity.User;
  import com.oswin902.yututubackend.model.vo.PictureVO;
  import com.oswin902.yututubackend.service.PictureService;
  import com.oswin902.yututubackend.service.UserService;
  import org.springframework.web.bind.annotation.*;
  import org.springframework.web.multipart.MultipartFile;
  
  import javax.annotation.Resource;
  import javax.servlet.http.HttpServletRequest;
  
  @RestController
  @RequestMapping("/picture")
  public class PictureController {
  
      @Resource
      private UserService userService;
  
      @Resource
      private PictureService pictureService;
  
      /**
       * 上传图片 (可重新上传)
       *
       * @param multipartFile        上传文件(form表单接收)
       * @param pictureUploadRequest 上传图片请求(自定义上传业务信息)
       * @param request              http请求
       * @return 上传图片结果
       */
      @PostMapping("/upload")
      @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
      public BaseResponse<PictureVO> uploadPicture(
              @RequestPart("file") MultipartFile multipartFile,
              PictureUploadRequest pictureUploadRequest,
              HttpServletRequest request
      ) {
          User loginUser = userService.getLoginUser(request);
          PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
          return ResultUtils.success(pictureVO);
      }
  }
  
  ```

  



## 后端接口 图片管理

### 请求封装类 (model.dto)

- src\main\java\com\oswin902\yututubackend\model\dto\picture\PictureUpdateRequest.java 【更新=新增|管理员更新】

  ```java
  package com.oswin902.yututubackend.model.dto.picture;
  
  import lombok.Data;
  
  import java.io.Serializable;
  import java.util.List;
  
  /**
   * 图片更新请求
   */
  @Data
  public class PictureUpdateRequest implements Serializable {
  
      /**
       * id
       */
      private Long id;
  
      /**
       * 图片名称
       */
      private String name;
  
      /**
       * 简介
       */
      private String introduction;
  
      /**
       * 分类
       */
      private String category;
  
      /**
       * 标签
       */
      private List<String> tags;
  
      private static final long serialVersionUID = 1L;
  }
  
  ```

- src\main\java\com\oswin902\yututubackend\model\dto\picture\PictureEditRequest.java【编辑=用户修改】

  ```java
  package com.oswin902.yututubackend.model.dto.picture;
  
  import lombok.Data;
  
  import java.io.Serializable;
  import java.util.List;
  
  /**
   * 图片编辑请求
   */
  @Data
  public class PictureEditRequest implements Serializable {
  
      /**
       * id
       */
      private Long id;
  
      /**
       * 图片名称
       */
      private String name;
  
      /**
       * 简介
       */
      private String introduction;
  
      /**
       * 分类
       */
      private String category;
  
      /**
       * 标签
       */
      private List<String> tags;
  
      private static final long serialVersionUID = 1L;
  }
  
  ```

- src\main\java\com\oswin902\yututubackend\model\dto\picture\PictureQueryRequest.java【查询】

  ```java
  package com.oswin902.yututubackend.model.dto.picture;
  
  import com.oswin902.yututubackend.common.PageRequest;
  import lombok.Data;
  import lombok.EqualsAndHashCode;
  
  import java.io.Serializable;
  import java.util.Date;
  import java.util.List;
  
  /**
   * 图片查询请求
   */
  @EqualsAndHashCode(callSuper = true)
  @Data
  public class PictureQueryRequest extends PageRequest implements Serializable {
  
      /**
       * id
       */
      private Long id;
  
      /**
       * 图片名称
       */
      private String name;
  
      /**
       * 简介
       */
      private String introduction;
  
      /**
       * 分类
       */
      private String category;
  
      /**
       * 标签
       */
      private List<String> tags;
  
      /**
       * 文件体积
       */
      private Long picSize;
  
      /**
       * 图片宽度
       */
      private Integer picWidth;
  
      /**
       * 图片高度
       */
      private Integer picHeight;
  
      /**
       * 图片比例
       */
      private Double picScale;
  
      /**
       * 图片格式
       */
      private String picFormat;
  
      /**
       * 搜索词（同时搜名称、简介等）
       */
      private String searchText;
  
      /**
       * 用户 id
       */
      private Long userId;
  
      /**
       * 审核状态：0-待审核; 1-通过; 2-拒绝
       */
      private Integer reviewStatus;
  
      /**
       * 审核信息
       */
      private String reviewMessage;
  
      /**
       * 审核人 ID
       */
      private Long reviewerId;
  
      /**
       * 审核时间
       */
      private Date reviewTime;
  
      /**
       * 空间 id
       */
      private Long spaceId;
  
      /**
       * 是否只查询 spaceId 为 null 的数据
       */
      private boolean nullSpaceId;
  
      /*
       * 开始编辑时间
       */
      private Date startEditTime;
  
      /*
       * 结束编辑时间
       */
      private Date endEditTime;
  
      private static final long serialVersionUID = 1L;
  }
  
  ```

  



### 响应封装类 (model.vo)

- src\main\java\com\oswin902\yututubackend\model\vo\PictureTagCategory.java

  ```java
  package com.oswin902.yututubackend.model.vo;
  
  import lombok.Data;
  
  import java.util.List;
  
  /**
   * 图片标签分类列表视图
   */
  @Data
  public class PictureTagCategory {
  
      /**
       * 标签列表
       */
      private List<String> tagList;
  
      /**
       * 分类列表
       */
      private List<String> categoryList;
  }
  
  ```
  
  



### 数据库访问层 (mapper) ✔





### 业务逻辑层 (service) ✔

- src\main\java\com\oswin902\yututubackend\service\PictureService.java

  ```java
  package com.oswin902.yututubackend.service;
  
  import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
  import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
  import com.baomidou.mybatisplus.extension.service.IService;
  import com.oswin902.yututubackend.model.dto.picture.PictureEditRequest;
  import com.oswin902.yututubackend.model.dto.picture.PictureQueryRequest;
  import com.oswin902.yututubackend.model.dto.picture.PictureUploadRequest;
  import com.oswin902.yututubackend.model.entity.Picture;
  import com.oswin902.yututubackend.model.entity.User;
  import com.oswin902.yututubackend.model.vo.PictureVO;
  import org.springframework.web.multipart.MultipartFile;
  
  import javax.servlet.http.HttpServletRequest;
  
  /**
   * @author huangyingzhu
   * @description 针对表【picture(图片)】的数据库操作Service
   * @createDate 2024-12-26 16:06:44
   */
  public interface PictureService extends IService<Picture> {
  
      /**
       * 上传图片
       *
       * @param multipartFile        图片
       * @param pictureUploadRequest 图片上传请求
       * @param loginUser            用户 【鉴权】
       * @return 图片信息
       */
      PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser);
  
      /**
       * 获取图片信息(脱敏-单条) 【工具】
       *
       * @param picture 未脱敏图片
       * @param request http请求
       * @return 用户信息
       */
      PictureVO getPictureVO(Picture picture, HttpServletRequest request);
  
      /**
       * 获取图片信息(脱敏-多条|分页) 【工具】
       *
       * @param picturePage 一页图片(未脱敏)
       * @param request     http请求
       * @return 图片脱敏
       */
      Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);
  
      /**
       * 校验图片
       *
       * @param picture 图片
       */
      void validPicture(Picture picture);
  
      /**
       * 获取查询条件 【工具】
       *
       * @param pictureQueryRequest 查询条件
       * @return QueryWrapper
       */
      QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);
  }
  
  ```

- src\main\java\com\oswin902\yututubackend\service\impl\PictureServiceImpl.java

  ```java
  package com.oswin902.yututubackend.service.impl;
  
  import cn.hutool.core.bean.BeanUtil;
  import cn.hutool.core.collection.CollUtil;
  import cn.hutool.core.util.ObjUtil;
  import cn.hutool.core.util.StrUtil;
  import cn.hutool.json.JSONUtil;
  import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
  import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
  import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
  import com.oswin902.yututubackend.exception.ErrorCode;
  import com.oswin902.yututubackend.exception.ThrowUtils;
  import com.oswin902.yututubackend.manager.FileManager;
  import com.oswin902.yututubackend.mapper.PictureMapper;
  import com.oswin902.yututubackend.model.dto.file.UploadPictureResult;
  import com.oswin902.yututubackend.model.dto.picture.PictureEditRequest;
  import com.oswin902.yututubackend.model.dto.picture.PictureQueryRequest;
  import com.oswin902.yututubackend.model.dto.picture.PictureUploadRequest;
  import com.oswin902.yututubackend.model.entity.Picture;
  import com.oswin902.yututubackend.model.entity.User;
  import com.oswin902.yututubackend.model.vo.PictureVO;
  import com.oswin902.yututubackend.model.vo.UserVO;
  import com.oswin902.yututubackend.service.PictureService;
  import com.oswin902.yututubackend.service.UserService;
  import org.springframework.beans.BeanUtils;
  import org.springframework.stereotype.Service;
  import org.springframework.web.multipart.MultipartFile;
  
  import javax.annotation.Resource;
  import javax.servlet.http.HttpServletRequest;
  import java.util.Date;
  import java.util.List;
  import java.util.Map;
  import java.util.Set;
  import java.util.stream.Collectors;
  
  /**
   * @author huangyingzhu
   * @description 针对表【picture(图片)】的数据库操作Service实现
   * @createDate 2024-12-26 16:06:44
   */
  @Service
  public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
          implements PictureService {
  
      @Resource
      private FileManager fileManager;
  
      @Resource
      private UserService userService;
  
      /**
       * 上传图片
       *
       * @param multipartFile        图片
       * @param pictureUploadRequest 图片上传请求
       * @param loginUser            用户 【鉴权】
       * @return 图片信息
       */
      @Override
      public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser) {
  
          // 校验参数
          ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
          ThrowUtils.throwIf(pictureUploadRequest == null, ErrorCode.PARAMS_ERROR, "图片上传请求参数为空");
  
          // 判断新增还是删除
          Long pictureId = pictureUploadRequest.getId();
          // 若为更新 则判断图片是否存在
          if (pictureId != null) {
              boolean exists = this.lambdaQuery().eq(Picture::getId, pictureId).exists(); // 数据库操作
              ThrowUtils.throwIf(!exists, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
          }
  
          // 上传图片
          String uploadPathPrefix = String.format("public/%s", loginUser.getId());  // 按照用户id划分目录
          UploadPictureResult uploadPictureResult = fileManager.uploadPicture(multipartFile, uploadPathPrefix);
  
          Picture picture = new Picture();
          picture.setUrl(uploadPictureResult.getUrl());
          picture.setName(uploadPictureResult.getPicName());  // 有些名字不太一样
          picture.setPicSize(uploadPictureResult.getPicSize());
          picture.setPicWidth(uploadPictureResult.getPicWidth());
          picture.setPicHeight(uploadPictureResult.getPicHeight());
          picture.setPicScale(uploadPictureResult.getPicScale());
          picture.setPicFormat(uploadPictureResult.getPicFormat());
          picture.setUserId(loginUser.getId());
          BeanUtil.copyProperties(pictureUploadRequest, picture);
  
          // 操作数据库
          // 若pictureId不为空 则为更新 更新需要补充id和编辑时间
          if (pictureId != null) {
              picture.setId(pictureId);
              picture.setUpdateTime(new Date());
          }
          // 若pictureId为空 则为新增
          boolean result = this.saveOrUpdate(picture);
          ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "图片上传失败，数据库操作失败");
  
          return PictureVO.objToVo(picture);
      }
  
      /**
       * 获取图片信息(脱敏-单条) 【工具】
       *
       * @param picture 未脱敏图片
       * @param request http请求
       * @return 用户信息
       */
      @Override
      public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
  
          PictureVO pictureVO = PictureVO.objToVo(picture);  // 对象转封装类
  
          // 关联查询用户信息
          Long userId = picture.getUserId();
          if (userId != null && userId > 0) {
              User user = userService.getById(userId);
              UserVO userVO = userService.getUserVO(user);
              pictureVO.setUser(userVO);
          }
  
          return pictureVO;
      }
  
      /**
       * 获取图片信息(脱敏-多条|分页) 【工具】
       *
       * @param picturePage 一页图片(未脱敏)
       * @param request     http请求
       * @return 图片脱敏
       */
      @Override
      public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
  
          List<Picture> pictureList = picturePage.getRecords();  // 取出分页中的值
          Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
          if (CollUtil.isEmpty(pictureList)) return pictureVOPage;
  
          // 对象列表 => 封装对象列表 (stream -> map -> collect)
          List<PictureVO> pictureVOList = pictureList.stream()
                  .map(PictureVO::objToVo)
                  .collect(Collectors.toList());
  
          // 关联查询用户信息
          // 先把图片列表中userId去重 只查一次  // id in (1,2)
          // 防止每次的查询都经过数据库(性能消耗大)
          Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
          // 方便后续填充信息  // 1 => user1, 2 => user2
          Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                  .collect(Collectors.groupingBy(User::getId));
  
          // 填充信息
          pictureVOList.forEach(pictureVO -> {
              Long userId = pictureVO.getUserId();
              User user = null;
              if (userIdUserListMap.containsKey(userId)) {
                  user = userIdUserListMap.get(userId).get(0);
              }
              pictureVO.setUser(userService.getUserVO(user));
          });
          pictureVOPage.setRecords(pictureVOList);
          return pictureVOPage;
      }
  
      /**
       * 校验图片 【工具】
       *
       * @param picture 图片
       */
      @Override
      public void validPicture(Picture picture) {
          ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);
  
          Long id = picture.getId();
          String url = picture.getUrl();
          String introduction = picture.getIntroduction();
  
          // 修改数据时，id不能为空，有参数则校验
          ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR, "id 不能为空");
  
          // 如果传递了 url，才校验
          if (StrUtil.isNotBlank(url)) {
              ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR, "url 过长");
          }
          if (StrUtil.isNotBlank(introduction)) {
              ThrowUtils.throwIf(introduction.length() > 800, ErrorCode.PARAMS_ERROR, "简介过长");
          }
      }
  
      /**
       * 获取查询条件 【工具】
       *
       * @param pictureQueryRequest 查询条件
       * @return QueryWrapper
       */
      @Override
      public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
          QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
          if (pictureQueryRequest == null) return queryWrapper;
  
          // 从对象中取值
          Long id = pictureQueryRequest.getId();
          String name = pictureQueryRequest.getName();
          String introduction = pictureQueryRequest.getIntroduction();
          String category = pictureQueryRequest.getCategory();
          List<String> tags = pictureQueryRequest.getTags();
          Long picSize = pictureQueryRequest.getPicSize();
          Integer picWidth = pictureQueryRequest.getPicWidth();
          Integer picHeight = pictureQueryRequest.getPicHeight();
          Double picScale = pictureQueryRequest.getPicScale();
          String picFormat = pictureQueryRequest.getPicFormat();
          String searchText = pictureQueryRequest.getSearchText();
          Long userId = pictureQueryRequest.getUserId();
          Integer reviewStatus = pictureQueryRequest.getReviewStatus();
          String reviewMessage = pictureQueryRequest.getReviewMessage();
          Long reviewerId = pictureQueryRequest.getReviewerId();
          Long spaceId = pictureQueryRequest.getSpaceId();
          Date startEditTime = pictureQueryRequest.getStartEditTime();
          Date endEditTime = pictureQueryRequest.getEndEditTime();
          boolean nullSpaceId = pictureQueryRequest.isNullSpaceId();
          String sortField = pictureQueryRequest.getSortField();
          String sortOrder = pictureQueryRequest.getSortOrder();
  
          // 从多字段中搜索
          // 需要拼接查询条件  // and (name like "%xxx%" or introduction like "%xxx%")
          if (StrUtil.isNotBlank(searchText)) {
              queryWrapper.and(
                      qw -> qw.like("name", searchText).or()
                              .like("introduction", searchText)
              );
          }
          // JSON 数组查询  // and (tag like "%\"Java\"%" and like "%\"Python\"%")
          if (CollUtil.isNotEmpty(tags)) {
              for (String tag : tags) {
                  queryWrapper.like("tags", "\"" + tag + "\"");
              }
          }
          queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
          queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
          queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
          queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
          queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
          queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
          queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);
          queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
          queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId);
  
          queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "spaceId", spaceId);
          queryWrapper.isNull(nullSpaceId, "spaceId");
  
          queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
          queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
          queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
          queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
  
          queryWrapper.ge(ObjUtil.isNotEmpty(startEditTime), "editTime", startEditTime);  // >= startEditTime
          queryWrapper.lt(ObjUtil.isNotEmpty(endEditTime), "editTime", endEditTime);  // < endEditTime
  
          // 排序
          queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
          return queryWrapper;
      }
  }
  
  ```
  
  



### 接口访问层 (controller) ✔

- src\main\java\com\oswin902\yututubackend\controller\PictureController.java

  ```java
  package com.oswin902.yututubackend.controller;
  
  import cn.hutool.json.JSONUtil;
  import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
  import com.oswin902.yututubackend.annotation.AuthCheck;
  import com.oswin902.yututubackend.common.BaseResponse;
  import com.oswin902.yututubackend.common.DeleteRequest;
  import com.oswin902.yututubackend.common.ResultUtils;
  import com.oswin902.yututubackend.constant.UserConstant;
  import com.oswin902.yututubackend.exception.ErrorCode;
  import com.oswin902.yututubackend.exception.ThrowUtils;
  import com.oswin902.yututubackend.model.dto.picture.PictureEditRequest;
  import com.oswin902.yututubackend.model.dto.picture.PictureQueryRequest;
  import com.oswin902.yututubackend.model.dto.picture.PictureUpdateRequest;
  import com.oswin902.yututubackend.model.dto.picture.PictureUploadRequest;
  import com.oswin902.yututubackend.model.entity.Picture;
  import com.oswin902.yututubackend.model.entity.User;
  import com.oswin902.yututubackend.model.vo.PictureTagCategory;
  import com.oswin902.yututubackend.model.vo.PictureVO;
  import com.oswin902.yututubackend.service.PictureService;
  import com.oswin902.yututubackend.service.UserService;
  import org.springframework.beans.BeanUtils;
  import org.springframework.web.bind.annotation.*;
  import org.springframework.web.multipart.MultipartFile;
  
  import javax.annotation.Resource;
  import javax.servlet.http.HttpServletRequest;
  import java.util.Arrays;
  import java.util.Date;
  import java.util.List;
  
  @RestController
  @RequestMapping("/picture")
  public class PictureController {
  
      @Resource
      private UserService userService;
  
      @Resource
      private PictureService pictureService;
  
      /**
       * 上传图片 (可重新上传) 【admin】
       *
       * @param multipartFile        上传文件(form表单接收)
       * @param pictureUploadRequest 上传图片请求(自定义上传业务信息)
       * @param request              http请求
       * @return 上传图片结果
       */
      @PostMapping("/upload")
      @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
      public BaseResponse<PictureVO> uploadPicture(
              @RequestPart("file") MultipartFile multipartFile,
              PictureUploadRequest pictureUploadRequest,
              HttpServletRequest request
      ) {
          User loginUser = userService.getLoginUser(request);
          PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
          return ResultUtils.success(pictureVO);
      }
  
      /**
       * 删除图片 【CRUD】
       *
       * @param deleteRequest 删除请求
       * @param request       http请求
       * @return 删除结果
       */
      @PostMapping("/delete")
      //@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)  // 不能用 本人和管理员都能删除图片
      public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
  
          // 非空校验
          ThrowUtils.throwIf((deleteRequest == null) || (deleteRequest.getId() <= 0), ErrorCode.PARAMS_ERROR);
  
          // 判断图片是否存在
          Long pictureId = deleteRequest.getId();
          Picture oldPicture = pictureService.getById(pictureId);
          ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
  
          // 权限检验
          User loginUser = userService.getLoginUser(request);  // 获取当前用户信息
          ThrowUtils.throwIf(!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser),
                  ErrorCode.NO_AUTH_ERROR);  // 仅本人和管理员才能删除图片
  
          // 操作数据库
          boolean result = pictureService.removeById(pictureId);
          ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除图片失败，数据库异常");
          return ResultUtils.success(true);
      }
  
  
      /**
       * 更新图片 【CRUD-admin】
       *
       * @param pictureUpdateRequest 更新图片请求
       * @param request              http请求
       * @return 是否成功
       */
      @PostMapping("/update")
      @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
      public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest,
                                                 HttpServletRequest request) {
  
          ThrowUtils.throwIf((pictureUpdateRequest.getTags() == null) || (pictureUpdateRequest.getId() <= 0), ErrorCode.PARAMS_ERROR);
  
          // 将实体类和DTO进行转换
          Picture picture = new Picture();
          BeanUtils.copyProperties(pictureUpdateRequest, picture);
          picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));  // 注意将list转为string
  
          // 数据校验
          pictureService.validPicture(picture);
  
          // 判断是否存在
          long id = pictureUpdateRequest.getId();
          Picture oldPicture = pictureService.getById(id);
          ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
  
          // 操作数据库
          boolean result = pictureService.updateById(picture);
          ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
          return ResultUtils.success(true);
      }
  
      /**
       * 根据 id 获取图片 【CRUD-admin】
       */
      @GetMapping("/get")
      @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
      public BaseResponse<Picture> getPictureById(long id, HttpServletRequest request) {
  
          ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
  
          // 查询数据库
          Picture picture = pictureService.getById(id);
          ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
  
          return ResultUtils.success(picture);
      }
  
      /**
       * 根据 id 获取图片(脱敏) 【CRUD】
       */
      @GetMapping("/get/vo")
      public BaseResponse<PictureVO> getPictureVOById(long id, HttpServletRequest request) {
  
          ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
  
          // 查询数据库
          Picture picture = pictureService.getById(id);
          ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
  
          return ResultUtils.success(pictureService.getPictureVO(picture, request));  // 脱敏
      }
  
  
      /**
       * 分页获取图片列表 【CRUD-admin】
       */
      @PostMapping("/list/page")
      @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
      public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
  
          long current = pictureQueryRequest.getCurrent();
          long size = pictureQueryRequest.getPageSize();
  
          // 查询数据库
          Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                  pictureService.getQueryWrapper(pictureQueryRequest));
          return ResultUtils.success(picturePage);
      }
  
      /**
       * 分页获取图片列表(脱敏) 【CRUD】
       */
      @PostMapping("/list/page/vo")
      public BaseResponse<Page<PictureVO>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                               HttpServletRequest request) {
  
          long current = pictureQueryRequest.getCurrent();
          long size = pictureQueryRequest.getPageSize();
  
          ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);  // 限制爬虫
  
          // 查询数据库
          Page<Picture> picturePage = pictureService.page(new Page<>(current, size),
                  pictureService.getQueryWrapper(pictureQueryRequest));
  
          return ResultUtils.success(pictureService.getPictureVOPage(picturePage, request));  // 脱敏
      }
  
  
      /**
       * 编辑图片（给用户使用） 【CRUD】
       */
      @PostMapping("/edit")
      public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest, HttpServletRequest request) {
  
          ThrowUtils.throwIf((pictureEditRequest == null) || (pictureEditRequest.getId() <= 0), ErrorCode.PARAMS_ERROR);
  
          // 在此处将实体类和 DTO 进行转换
          Picture picture = new Picture();
          BeanUtils.copyProperties(pictureEditRequest, picture);
          picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));  // 注意将list转为string
          picture.setEditTime(new Date());  // 设置编辑时间
  
          // 判断是否存在
          long id = pictureEditRequest.getId();
          Picture oldPicture = pictureService.getById(id);
          ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
  
          // 数据校验
          pictureService.validPicture(picture);
          User loginUser = userService.getLoginUser(request);
          ThrowUtils.throwIf(!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser),
                  ErrorCode.NO_AUTH_ERROR);  // 仅本人和管理员才能删除图片
  
          // 操作数据库
          boolean result = pictureService.updateById(picture);
          ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
          return ResultUtils.success(true);
      }
  
      @GetMapping("/tag_category")
      public BaseResponse<PictureTagCategory> listPictureTagCategory() {
  
          // TODO 前期追求项目快速上线 先完成核心业务流程 后期再优化... (标签表 分类表 计算最优)
          // 不要在前端写死 需要前端请求后端【规范】
          List<String> tagList = Arrays.asList("热门", "搞笑", "生活", "高清", "艺术", "校园", "背景", "简历", "创意");
          List<String> categoryList = Arrays.asList("模板", "电商", "表情包", "素材", "海报");
  
          PictureTagCategory pictureTagCategory = new PictureTagCategory();
          pictureTagCategory.setTagList(tagList);
          pictureTagCategory.setCategoryList(categoryList);
  
          return ResultUtils.success(pictureTagCategory);
      }
  }
  
  ```
  
  



## 前端页面 图片模块

### 页面 图片创建页





### 组件 图片上传





### 页面 图片信息修改页





### 页面 图片管理页





### 页面 图片详情页













## 图片模块优化









## 后端接口 空间模块







## 前端页面 空间模块









## 图片功能扩展







## 项目部署





















































































































