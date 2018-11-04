delete from dictionary_data#
//日历
insert into dictionary_data VALUES ('mainCal','calendar','zh_CN','main','主日历',1)#


//事件分类
insert into dictionary_data VALUES ('event_cate_1','event_cate','zh_CN','-1','无',-1)#
insert into dictionary_data VALUES ('event_cate_2','event_cate','zh_CN','0','打电话',0)#
insert into dictionary_data VALUES ('event_cate_3','event_cate','zh_CN','1','个人',1)#
insert into dictionary_data VALUES ('event_cate_4','event_cate','zh_CN','2','客户',2)#
insert into dictionary_data VALUES ('event_cate_5','event_cate','zh_CN','3','礼物',3)#
insert into dictionary_data VALUES ('event_cate_6','event_cate','zh_CN','4','生日',4)#
insert into dictionary_data VALUES ('event_cate_7','event_cate','zh_CN','5','问题',5)#
insert into dictionary_data VALUES ('event_cate_8','event_cate','zh_CN','6','收藏',6)#
insert into dictionary_data VALUES ('event_cate_9','event_cate','zh_CN','7','知识',7)#
insert into dictionary_data VALUES ('event_cate_700','event_cate','zh_CN','700','其他',700)#


//重复周期
insert into dictionary_data VALUES ('rep_int_1','repeat_interval','zh_CN','NONE','不重复',0)#
insert into dictionary_data VALUES ('rep_int_2','repeat_interval','zh_CN','DAILY','每天',1)#
insert into dictionary_data VALUES ('rep_int_3','repeat_interval','zh_CN','WEEKLY','每周',2)#
insert into dictionary_data VALUES ('rep_int_5','repeat_interval','zh_CN','MONTHLY','每月',4)#
insert into dictionary_data VALUES ('rep_int_6','repeat_interval','zh_CN','YEARLY','每年',5)#

//事件类型
insert into dictionary_data VALUES ('eve_t_1','e_type','zh_CN','PUBLIC','公开',0)#
insert into dictionary_data VALUES ('eve_t_2','e_type','zh_CN','PRIVATE','隐私',1)#



//提醒时间
insert into dictionary_data VALUES ('remind_time_1','remind_time','zh_CN','-1S','不提醒',-1)#
insert into dictionary_data VALUES ('remind_time_2','remind_time','zh_CN','0M','0分钟',0)#
insert into dictionary_data VALUES ('remind_time_3','remind_time','zh_CN','5M','5分钟',5)#
insert into dictionary_data VALUES ('remind_time_4','remind_time','zh_CN','15M','15分钟',15)#
insert into dictionary_data VALUES ('remind_time_5','remind_time','zh_CN','1H','1小时',60)#
insert into dictionary_data VALUES ('remind_time_6','remind_time','zh_CN','2H','2小时',61)#
insert into dictionary_data VALUES ('remind_time_7','remind_time','zh_CN','1D','1天',1440)#
insert into dictionary_data VALUES ('remind_time_8','remind_time','zh_CN','2D','2天',1441)#
insert into dictionary_data VALUES ('remind_time_9','remind_time','zh_CN','1W','1周',1450)#


//任务状态
insert into dictionary_data VALUES ('ts1','task_status','zh_CN','NOTSET','未指定',-1)#
insert into dictionary_data VALUES ('ts2','task_status','zh_CN','IN-PROCESS','处理中',0)#
insert into dictionary_data VALUES ('ts3','task_status','zh_CN','NEEDS-ACTION','将处理',1)#
insert into dictionary_data VALUES ('ts4','task_status','zh_CN','COMPLETED','完成',2)#
insert into dictionary_data VALUES ('ts5','task_status','zh_CN','CANCELLED','取消',3)#

//查询条件
insert into dictionary_data (id, dict_type,local_str,code,dictdata_value,dict_order) VALUES ('tcon1','task_cond','zh_CN',
'js: var comd="(tstatus in (''NOTSET'',''IN-PROCESS'', ''NEEDS-ACTION'') and (start_time is null or start_time>=";
var dt = Java.type("org.ycalendar.util.UtilDateTime");
var date = Java.type("java.util.Date");
var t1=dt.getDayStart(new date());
var t2=dt.getDayEnd(new date());
comd=comd+t1+") and (end_time is null or end_time<="+t2+"))";'
,'今天任务',1)#


insert into dictionary_data VALUES ('tcon2','task_cond','zh_CN',
'js: var comd="(tstatus in (''NOTSET'',''IN-PROCESS'', ''NEEDS-ACTION'') and (start_time is null or start_time>=";
var dt = Java.type("org.ycalendar.util.UtilDateTime");
var date = Java.type("java.util.Date");
var t1=dt.getDayStart(new date());
var t2=dt.getDayEnd(new date(),7);
comd=comd+t1+") and (end_time is null or end_time<="+t2+"))";'
,'七天任务',2)#

insert into dictionary_data VALUES ('tcon3','task_cond','zh_CN','(tstatus=''NOTSET'')','未开始任务',3)#

insert into dictionary_data VALUES ('tcon4','task_cond','zh_CN',
'js: var comd="(tstatus in (''NOTSET'',''IN-PROCESS'', ''NEEDS-ACTION'') and (end_time<=";
var sys = Java.type("java.lang.System");
var t2=sys.currentTimeMillis();
comd=comd+t2+"))";'
,'过期任务',4)#


insert into dictionary_data VALUES ('tcon5','task_cond','zh_CN','(tstatus=''COMPLETED'')','完成任务',5)#
insert into dictionary_data VALUES ('tcon6','task_cond','zh_CN','(tstatus=''NOTSET'' or tstatus=''IN-PROCESS'' or tstatus=''NEEDS-ACTION'')','未完成任务',6)#
insert into dictionary_data VALUES ('tcon7','task_cond','zh_CN','(tstatus=''CANCELLED'')','取消任务',7)#
insert into dictionary_data VALUES ('tcon8','task_cond','zh_CN','(1=1)','所有任务',8)#