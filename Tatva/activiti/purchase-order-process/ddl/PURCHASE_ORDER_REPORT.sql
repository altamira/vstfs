--------------------------------------------------------
--  File created - Friday-February-14-2014   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table PURCHASE_ORDER_REPORT
--------------------------------------------------------

  CREATE TABLE "PARTH1"."PURCHASE_ORDER_REPORT" 
   (	"JASPER_FILE" BLOB, 
	"JRXML_FILE" BLOB, 
	"ALTAMIRA_LOGO" BLOB, 
	"REPORT_ID" NUMBER, 
	"GENERATED_DATETIME" DATE DEFAULT SYSDATE
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" 
 LOB ("JASPER_FILE") STORE AS BASICFILE (
  TABLESPACE "SYSTEM" ENABLE STORAGE IN ROW CHUNK 8192 RETENTION 
  NOCACHE LOGGING 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)) 
 LOB ("JRXML_FILE") STORE AS BASICFILE (
  TABLESPACE "SYSTEM" ENABLE STORAGE IN ROW CHUNK 8192 RETENTION 
  NOCACHE LOGGING 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)) 
 LOB ("ALTAMIRA_LOGO") STORE AS BASICFILE (
  TABLESPACE "SYSTEM" ENABLE STORAGE IN ROW CHUNK 8192 RETENTION 
  NOCACHE LOGGING 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)) ;
--------------------------------------------------------
--  Constraints for Table PURCHASE_ORDER_REPORT
--------------------------------------------------------

  ALTER TABLE "PARTH1"."PURCHASE_ORDER_REPORT" MODIFY ("REPORT_ID" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."PURCHASE_ORDER_REPORT" MODIFY ("ALTAMIRA_LOGO" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."PURCHASE_ORDER_REPORT" MODIFY ("JRXML_FILE" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."PURCHASE_ORDER_REPORT" MODIFY ("JASPER_FILE" NOT NULL ENABLE);
