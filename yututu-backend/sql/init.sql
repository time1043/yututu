create database if not exists yututu;
use yututu;

-- 用户表
-- 账号 密码 【登陆】
-- 用户名 头像 简介 角色 【基本信息】
-- 会员过期时间 会员兑换码 会员编号 【会员模块】
-- 分享码 被哪个用户邀请 【用户邀请】
-- 编辑时间(用户编辑) 创建时间(何时产生) 更新时间(管理员更新) 逻辑删除 【标配】
drop table if exists user;
create table if not exists user
(
    -- 编辑时间(用户编辑) 创建时间(何时产生) 更新时间(管理员更新) 逻辑删除 【标配】
    id            bigint auto_increment comment 'id' primary key,
    editTime      datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint      default 0                 not null comment '是否删除',
    -- 账号 密码 【登陆】
    userAccount   varchar(256)                           not null comment '账号',
    userPassword  varchar(512)                           not null comment '密码',
    -- 用户名 头像 简介 角色 【基本信息】
    userName      varchar(256)                           null comment '用户昵称',
    userAvatar    varchar(1024)                          null comment '用户头像',
    userProfile   varchar(512)                           null comment '用户简介',
    userRole      varchar(256) default 'user'            not null comment '用户角色：user/admin/vip/svip',
    -- 会员过期时间 会员兑换码 会员编号 【会员模块】
    vipExpireTime datetime                               null comment '会员过期时间',
    vipCode       varchar(256)                           null comment '会员兑换码',
    vipNumber     varchar(256)                           null comment '会员编号',
    -- 分享码 被哪个用户邀请 【用户邀请】
    shareCode     varchar(256) default null              null comment '分享码',
    inviteUser    bigint       default null              null comment '被哪个用户邀请 用户id',
    UNIQUE KEY uk_userAccount (userAccount),
    INDEX idx_userName (userName)
) comment '用户' collate = utf8mb4_unicode_ci;

insert into user (id,
                  editTime, createTime, updateTime, isDelete,
                  userAccount, userPassword,
                  userName, userAvatar, userProfile, userRole,
                  vipExpireTime, vipCode, vipNumber,
                  shareCode, inviteUser)
values (1000000000000001001,
        '2003-01-15 10:00:00', '2003-01-15 10:00:00', '2003-01-15 10:00:00', 0,
        'admin', 'ff4fbe7a62bd080c010c819c956483f6',
        '管理员', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQULltHiu2L6G0WU1-wngsEz6e99Gx02Ys19w&s',
        '超级管理员', 'admin',
        NULL, NULL, NULL,
        NULL, NULL),
       (1870828717986197507,
        '2014-02-20 15:30:00', '2014-02-20 15:30:00', '2014-02-20 15:30:00', 0,
        'oswin902', 'a977d46b99c6d0513282f284af3a57a7',
        'oswin', 'https://avatars.githubusercontent.com/u/132178516?v=4', '放逐自己', 'svip',
        '2025-03-15 23:59:59', '1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2b', '00001',
        '2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2b3', NULL),
       (1323443407856347508,
        '2024-03-05 09:15:00', '2024-03-05 09:15:00', '2024-03-05 09:15:00', 0,
        'zhangsan', 'bbdbb47c13c0354ad51da5979d02dff0',
        '张三', 'https://image-cdn-ak.spotifycdn.com/image/ab67706c0000da845f81e6432d3d15d4b033c8e1',
        '我喜欢编程', 'user',
        '2023-09-30 23:59:59', '3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2b3456', NULL,
        '4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2b34567', '1870828717986197507'),
       (7986871750919187082,
        '2024-04-10 14:45:00', '2024-04-10 14:45:00', '2024-04-10 14:45:00', 1,
        'lisi', '4856eec5b06e9ad52685ceecce944b9c',
        '李四', 'https://miro.medium.com/v2/resize:fit:640/format:webp/1*4j2A9niz0eq-mRaCPUffpg.png', '不爱', 'user',
        NULL, NULL, NULL,
        '6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2b3456789ab', NULL),
       (5101870828717986197,
        '2014-05-20 11:20:00', '2014-05-20 11:20:00', '2014-05-20 11:20:00', 0,
        'wangwu', 'b6b5dc860b4e3d15003331e2ed780e55',
        '王五', 'https://miro.medium.com/v2/resize:fit:640/format:webp/0*1Og_hmJWdlMiDWuB.png', '摄影爱好者', 'vip',
        '2023-11-30 23:59:59', '7a8b9c0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2b3456789abcde', '00004',
        'f3a4b5c6d7e8f9a0b1c2d3e4f2b3c4d5e6f7a8b9c0d1e25a6b7c8d9e0f1a2b3', '1870828717986197507'),
       (6197287151118708798,
        '2024-06-15 16:30:00', '2024-06-15 16:30:00', '2024-06-15 16:30:00', 0,
        'liuliu', '9de8d87157ee04f19ed0c45707c50f0c',
        '刘六',
        'https://pyxis.nymag.com/v1/imgs/085/1c3/809909c1e7db522b90666c5141325084ed-5-doctor-who.rsquare.w400.jpg',
        '音乐发烧友', 'svip',
        '2027-01-01 23:59:59', '9c0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2b3456789abcd01ef', '00005',
        '0d1e2f3a4b5c6d7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2b3456789abcd01ef234', NULL),
       (1234567890123456789,
        '2024-07-01 08:30:00', '2024-07-01 08:30:00', '2024-07-01 08:30:00', 0,
        'johndoe', '5dbf9e5a0566f38faa6a38874c1be06f',
        'John Doe', 'https://ih1.redbubble.net/image.3243947720.5567/st,small,507x507-pad,600x600,f8f8f8.jpg',
        '软件工程师', 'user',
        '2026-05-20 23:59:59', '0123456789abcdef0123456789abcdef', NULL,
        'abcdef0123456789abcdef0123456789abcdef', '1323443407856347508'),
       (1876543210987654321,
        '2024-08-15 13:45:00', '2024-08-15 13:45:00', '2024-08-15 13:45:00', 0,
        'janedoe', '5dbf9e5a0566f38faa6a38874c1be06f',
        'Jane Doe', 'https://atts.w3cschool.cn/attachments/image/20240227/1709000604678521.png',
        '数据分析师', 'vip',
        '2025-12-31 23:59:59', 'fedcba9876543210fedcba9876543210', '00006',
        '0123456789abcdef0123456789abcdef', '7986871750919187082'),
       (5647382915647382915,
        '2004-09-20 18:00:00', '2004-09-20 18:00:00', '2004-09-20 18:00:00', 0,
        'doctor12', '5dbf9e5a0566f38faa6a38874c1be06f',
        'Peter Capaldi', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRs28cJ5QqWHIVyq2e-1Hyq7IruvV8qHTtdAQ&s',
        'The Twelfth Doctor emerged from his predecessor''s explosive regeneration on Trenzalore, being the product of "regeneration number thirteen." He was the first incarnation of the Doctor''s second regeneration cycle, which had been bestowed upon him by the Time Lords. Clara Oswald, continuing her travels with him after he regenerated, served as his primary companion, and at times acted as his moral compass.',
        'svip',
        '2028-01-01 23:59:59', '0987654321fedcba0987654321fedcba', '00007',
        'fedcba0987654321fedcba0987654321fedcba', '5101870828717986197'),
       (1765432104987654321,
        '2024-10-25 20:00:00', '2024-10-25 20:00:00', '2024-10-25 20:00:00', 0,
        'bob456', '5dbf9e5a0566f38faa6a38874c1be06f',
        'Bob', 'https://i.redd.it/1sp77tnbjwub1.jpg', '健身教练', 'user',
        '2024-12-31 23:59:59', '1234567890abcdef1234567890abcdef', NULL,
        '0123456789abcdef1234567890abcdef', '6197287151118708798'),
       (2109876543210984765,
        '2024-11-30 10:00:00', '2024-11-30 10:00:00', '2024-11-30 10:00:00', 0,
        'charlie789', '5dbf9e5a0566f38faa6a38874c1be06f',
        'Charlie', 'https://www.hamleys.com/media/catalog/product/7/2/729509_main_caygykpbo7sqewzs.jpg', '摄影师',
        'svip',
        '2026-08-15 23:59:59', 'abcdef123456789abcdef123456789', '00008',
        '1234567890abcdef1234567890abcdef', '1117989750787082876'),
       (5647382915647382916,
        '2024-12-05 15:30:00', '2024-12-05 15:30:00', '2024-12-05 15:30:00', 0,
        'dave101', '5dbf9e5a0566f38faa6a38874c1be06f',
        'Dave', 'https://yinguobing.com/content/images/size/w1200/2024/08/8619bc9dd540459aa493eab297d36726_0.jpg',
        '全银河系最好的设计师', 'user',
        NULL, NULL, NULL,
        'fedcba0987654321fedcba0987654321fedcba', '1323443407856347508'),
       (1876543210987654322,
        '2024-12-10 11:45:00', '2024-12-10 11:45:00', '2024-12-10 11:45:00', 0,
        'eve202', '5dbf9e5a0566f38faa6a38874c1be06f',
        'Eve', 'https://www.pokemon.cn/play/resources/pokedex/img/pm/285395ca77d82861fd30cea64567021a50c1169c.png',
        '作家', 'vip',
        '2025-07-04 23:59:59', '0123456789abcdef0123456789abcdef', '00009',
        '0123456789abcdef0123456789abcdef', '1870828717986197507'),
       (5647382915647382917,
        '2024-12-15 17:00:00', '2024-12-15 17:00:00', '2024-12-15 17:00:00', 0,
        'frank303', '5dbf9e5a0566f38faa6a38874c1be06f',
        'Frank', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTrIlhqM38x5WQC5B8EfjwxjW6cmAsQWoWOrA&s',
        '音乐家', 'svip',
        '2027-03-30 23:59:59', 'fedcba9876543210fedcba9876543210', '00010',
        'fedcba9876543210fedcba9876543210', '7986871750919187082'),
       (5647382915647382918,
        '2024-12-20 12:00:00', '2024-12-20 12:00:00', '2024-12-20 12:00:00', 0,
        'grace404', '5dbf9e5a0566f38faa6a38874c1be06f',
        'Grace', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcScFmnxXQ6v8iqnITmd7B-fAYpDu79fOQw2MQ&s',
        '画家', 'user',
        NULL, NULL, NULL,
        '0123456789abcdef0123456789abcdef', '5101870828717986197'),
       (3564782738291991564,
        '2024-12-25 18:00:00', '2024-12-25 18:00:00', '2024-12-25 18:00:00', 0,
        'hana515', '5dbf9e5a0566f38faa6a38874c1be06f',
        'Hana', 'https://super142.wordpress.com/wp-content/uploads/2022/11/doctor-who-season-1.jpg',
        '演员', 'user',
        NULL, NULL, NULL,
        '0123456789abcdef0123456789abcdef', '5101870828717986197');

-- 图片表
-- url 名称 简介 分类 标签(json数组) 创建人 【基本信息】
-- 体积 宽度 高度 宽高比 格式 【基本信息】
-- 审核状态 审核信息 审核人 审核时间 【用户上传-管理员审核】
-- 编辑时间(用户编辑) 创建时间(何时产生) 更新时间(管理员更新) 逻辑删除 【标配】
drop table if exists picture;
create table if not exists picture
(
    -- 编辑时间(用户编辑) 创建时间(何时产生) 更新时间(管理员更新) 逻辑删除 【标配】
    id            bigint auto_increment comment 'id' primary key,
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    editTime      datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除',
    -- url 缩略图url 名称 简介 分类 标签(json数组) 创建人 【基本信息】
    url           varchar(512)                       not null comment '图片 url',
    thumbnailUrl  varchar(512)                       null comment '缩略图 url',
    name          varchar(128)                       not null comment '图片名称',
    introduction  varchar(512)                       null comment '简介',
    category      varchar(64)                        null comment '分类',
    tags          varchar(512)                       null comment '标签（JSON 数组）',
    userId        bigint                             not null comment '创建用户 id',
    -- 体积 宽度 高度 宽高比 格式 【基本信息】
    picSize       bigint                             null comment '图片体积',
    picWidth      int                                null comment '图片宽度',
    picHeight     int                                null comment '图片高度',
    picScale      double                             null comment '图片宽高比例',
    picFormat     varchar(32)                        null comment '图片格式',
    -- 审核状态 审核信息 审核人 审核时间 【用户上传-管理员审核】
    reviewStatus  int      default 0                 not null comment '审核状态：0-待审核; 1-通过; 2-拒绝',
    reviewMessage varchar(512)                       null comment '审核信息',
    reviewerId    bigint                             null comment '审核人 id',
    reviewTime    datetime                           null comment '审核时间',
    INDEX idx_name (name),                 -- 提升基于图片名称的查询性能
    INDEX idx_introduction (introduction), -- 用于模糊搜索图片简介
    INDEX idx_category (category),         -- 提升基于分类的查询性能
    INDEX idx_tags (tags),                 -- 提升基于标签的查询性能
    INDEX idx_userId (userId),             -- 提升基于用户 ID 的查询性能
    INDEX idx_reviewStatus (reviewStatus)  -- 会经常被查询
) comment '图片' collate = utf8mb4_unicode_ci;
