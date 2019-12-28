# fileUpload

[![Author](https://img.shields.io/badge/Author-kid1999-lightgrey.svg)](https://github.com/kid1999/)
![license](https://img.shields.io/github/license/kid1999/fileUpload.svg)
![release](https://img.shields.io/github/release/kid1999/fileUpload.svg)
![last-commitor](https://img.shields.io/github/last-commit/kid1999/fileUpload.svg)
![java](https://img.shields.io/badge/language-java-orange.svg)

## 项目初衷
希望可以帮到和我一样在收集文件时头晕眼花的朋友。
本产品致力于规范命名提交文件，为收集者提供管理，批量打包下载等功能，为用户只见提供留言功能。

## 环境要求
* macOS or Linux or Windows
* JDK1.8
* Mysql
## 项目技术栈
* 后端 : SpringBoot SpringMVC MybatisPlus
* 前端 : Bootstrap jQuery LayUI
* 文件存储系统 : FastDFS
* 网络服务器 : Nginx
* 其他： 异步邮件通知，权限分离

## pom 主要依赖
* thymeleaf
* redis
* lombok
* fastdfs-client
* mybatisPlus

-----

## 等待完善的地方：
* 前端交互美化 使用layUI重构
* 留言，针对该项目的一些问题
* 公告设置，更新信息
* 个人项目存储容量感知(可用，已用，百分比)
* 支付系统，充钱买容量
* 权限分离（普通用户，user，admin）






## 已修复的地方：
* ~~项目创建的要求完善~~
* ~~使用redis减少IO~~
* ~~重构文件系统为FastDFS~~
* ~~日志系统规范~~
* ~~验证码~~
* ~~数据库非空规则设置~~
* ~~列表无数据时，显示暂无~~
* ~~删除提交记录和文件~~
* ~~按规则排序显示列表~~
* ~~加密显示接收页面~~
* ~~结束项目停止提交~~
* ~~网站流量分析~~
* ~~异常页面更新~~
* ~~搜索~~



