delete from dictionary_data;
//日历
insert into dictionary_data VALUES ('mainCal','calendar','zh_CN','main','主日历',1);


//事件分类
insert into dictionary_data VALUES ('event_cate_1','event_cate','zh_CN','-1','无',-1);
insert into dictionary_data VALUES ('event_cate_2','event_cate','zh_CN','0','打电话',0);
insert into dictionary_data VALUES ('event_cate_3','event_cate','zh_CN','1','个人',1);
insert into dictionary_data VALUES ('event_cate_4','event_cate','zh_CN','2','客户',2);
insert into dictionary_data VALUES ('event_cate_5','event_cate','zh_CN','3','礼物',3);
insert into dictionary_data VALUES ('event_cate_6','event_cate','zh_CN','4','生日',4);
insert into dictionary_data VALUES ('event_cate_7','event_cate','zh_CN','5','问题',5);
insert into dictionary_data VALUES ('event_cate_8','event_cate','zh_CN','6','收藏',6);
insert into dictionary_data VALUES ('event_cate_9','event_cate','zh_CN','7','知识',7);
insert into dictionary_data VALUES ('event_cate_700','event_cate','zh_CN','700','其他',700);


//重复周期
insert into dictionary_data VALUES ('rep_int_1','repeat_interval','zh_CN','0','不重复',0);
insert into dictionary_data VALUES ('rep_int_2','repeat_interval','zh_CN','1','每天',1);
insert into dictionary_data VALUES ('rep_int_3','repeat_interval','zh_CN','2','每周',2);
insert into dictionary_data VALUES ('rep_int_4','repeat_interval','zh_CN','3','每周末',3);
insert into dictionary_data VALUES ('rep_int_5','repeat_interval','zh_CN','4','每月',4);
insert into dictionary_data VALUES ('rep_int_6','repeat_interval','zh_CN','5','每年',5);

//事件类型
insert into dictionary_data VALUES ('eve_t_1','e_type','zh_CN','PUBLIC','公开',0);
insert into dictionary_data VALUES ('eve_t_2','e_type','zh_CN','PRIVATE','隐私',1);



//提醒时间
insert into dictionary_data VALUES ('remind_time_1','remind_time','zh_CN','-1S','不提醒',-1);
insert into dictionary_data VALUES ('remind_time_2','remind_time','zh_CN','0M','0分钟',0);
insert into dictionary_data VALUES ('remind_time_3','remind_time','zh_CN','5M','5分钟',5);
insert into dictionary_data VALUES ('remind_time_4','remind_time','zh_CN','15M','15分钟',15);
insert into dictionary_data VALUES ('remind_time_5','remind_time','zh_CN','1H','1小时',60);
insert into dictionary_data VALUES ('remind_time_6','remind_time','zh_CN','2H','2小时',61);
insert into dictionary_data VALUES ('remind_time_7','remind_time','zh_CN','1D','1天',1440);
insert into dictionary_data VALUES ('remind_time_8','remind_time','zh_CN','2D','2天',1441);
insert into dictionary_data VALUES ('remind_time_9','remind_time','zh_CN','1W','1周',1450);


//任务状态
insert into dictionary_data VALUES ('ts1','task_status','zh_CN','notset','未指定',-1);
insert into dictionary_data VALUES ('ts2','task_status','zh_CN','process','处理中',0);
insert into dictionary_data VALUES ('ts3','task_status','zh_CN','unprocessed','未处理',1);
insert into dictionary_data VALUES ('ts4','task_status','zh_CN','complete','完成',2);
insert into dictionary_data VALUES ('ts5','task_status','zh_CN','cancel','取消',3);