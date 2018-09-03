//drop table dictionary_data IF EXISTS;
CREATE table IF NOT EXISTS dictionary_data  (
  id varchar(35)   PRIMARY KEY ,
  dict_type varchar(35) NOT NULL ,
  local_str varchar(10) NOT NULL ,
  code varchar(30)  NOT NULL   ,
  dictdata_value varchar(60)   NOT NULL,
  dict_order NUMBER(18) default 10000,
);
CREATE  INDEX IF NOT EXISTS DICDATYINDEX ON dictionary_data (dict_type) ;
//drop table event_data IF EXISTS;
CREATE table IF NOT EXISTS event_data  (
  eventid varchar(35)   PRIMARY KEY ,
  all_day boolean default false,
  category varchar(30)   ,
  end_time NUMBER(18) NOT NULL,
  start_time NUMBER(18) NOT NULL,
  title varchar(300) NOT NULL,
  repeat NUMBER(10) default 0,
  repeat_end NUMBER(18),
  calendarid   varchar(25) NOT NULL default 'main',
  remind   NUMBER(10) default -1,
  event_desc clob
);
CREATE  INDEX IF NOT EXISTS EVENTTIMEINX ON event_data (start_time) ;
//drop table task_data IF EXISTS;
CREATE table IF NOT EXISTS task_data  (
  taskid varchar(35)   PRIMARY KEY ,
  category varchar(30)   ,
  end_time BIGINT ,
  start_time BIGINT,
  title varchar(300) NOT NULL,
  calendarid   varchar(25) NOT NULL default 'main',
  tstatus varchar(30)  NOT NULL default 'notset',
  percentage int,
  remind   int default -1,
  taskdesc   clob
  
);