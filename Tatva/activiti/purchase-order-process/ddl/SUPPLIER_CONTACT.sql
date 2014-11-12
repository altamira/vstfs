--------------------------------------------------------
--  File created - Tuesday-February-11-2014   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table SUPPLIER_CONTACT
--------------------------------------------------------

  CREATE TABLE "PARTH1"."SUPPLIER_CONTACT" 
   (	"SUPPLIER_CONTACT_ID" NUMBER, 
	"CONTACT_NAME" VARCHAR2(100 BYTE), 
	"MAIL_ADDRESS" VARCHAR2(150 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;
--------------------------------------------------------
--  DDL for Index SUPPLIER_CONTACT_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PARTH1"."SUPPLIER_CONTACT_PK" ON "PARTH1"."SUPPLIER_CONTACT" ("SUPPLIER_CONTACT_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;
--------------------------------------------------------
--  Constraints for Table SUPPLIER_CONTACT
--------------------------------------------------------

  ALTER TABLE "PARTH1"."SUPPLIER_CONTACT" MODIFY ("MAIL_ADDRESS" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."SUPPLIER_CONTACT" ADD CONSTRAINT "SUPPLIER_CONTACT_PK" PRIMARY KEY ("SUPPLIER_CONTACT_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."SUPPLIER_CONTACT" MODIFY ("CONTACT_NAME" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."SUPPLIER_CONTACT" MODIFY ("SUPPLIER_CONTACT_ID" NOT NULL ENABLE);
