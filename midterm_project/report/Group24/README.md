# README

## build 

使用Android Studio 打开项目

## setup

使用Android Studio 的虚拟机打开应用，需要给予应用互联网的权限（需要下载图片），第一次使用应用需要下载大量的图片，会有略微卡顿

## demovideo
  介绍视频为 AcedDemoVideo.mp4

### 项目简介
* 王者荣耀英雄人物的增删改查功能。属性包含头像、称号、名字、位置、生存能力值、攻击伤害值、技能效果值、上手难度值等，其中头像是图片
* App启动时初始化包含10个英雄信息（不要求数据库，可以代码定义或xml）
* 使用数据库来实现数据持久化。
* 使用网络访问来加载图片
* UI美化

### 主界面

* 首页：宣传图
* 英雄：列出所有英雄或可以根据tag选择显示的英雄种类比如：职业，位置。可以搜索英雄。
* 装备：列出所有装备或其他根据tag选择显示的装备种类比如：装备类型
* 铭文：列出所有铭文
* 我的：自己定义一个英雄

### 英雄详情界面

* 信息：英雄的攻击，防御，生命，难度，喜爱度属性
* 技能：每一个技能的详情，以及耗蓝
* 推荐装备：显示推荐的这批装备的理由，显示每一个装备以及其详情
* 推荐铭文：显示推荐这些铭文的理由，显示每一个铭文以及详情

### 背景音乐

打开应用自动播放音乐，关闭应用停止音乐

### 引用
* 图标：Pure图标 作者：pure轻雨
* 雷达图：MPAndroidChart  [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
