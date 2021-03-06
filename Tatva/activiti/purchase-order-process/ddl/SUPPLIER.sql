--------------------------------------------------------
--  File created - Tuesday-February-11-2014   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table SUPPLIER
--------------------------------------------------------

  CREATE TABLE "PARTH1"."SUPPLIER" 
   (	"SUPPLIER_ID" NUMBER, 
	"SUPPLIER_NAME" VARCHAR2(50 BYTE), 
	"SUPPLIER_CONTACT_ID" NUMBER
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;
--------------------------------------------------------
--  DDL for Index SUPPLIER_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PARTH1"."SUPPLIER_PK" ON "PARTH1"."SUPPLIER" ("SUPPLIER_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;
--------------------------------------------------------
--  DDL for Index SUPPLIER_UK1
--------------------------------------------------------

  CREATE UNIQUE INDEX "PARTH1"."SUPPLIER_UK1" ON "PARTH1"."SUPPLIER" ("SUPPLIER_NAME") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;
--------------------------------------------------------
--  Constraints for Table SUPPLIER
--------------------------------------------------------

  ALTER TABLE "PARTH1"."SUPPLIER" ADD CONSTRAINT "SUPPLIER_UK1" UNIQUE ("SUPPLIER_NAME")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."SUPPLIER" ADD CONSTRAINT "SUPPLIER_PK" PRIMARY KEY ("SUPPLIER_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."SUPPLIER" MODIFY ("SUPPLIER_NAME" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."SUPPLIER" MODIFY ("SUPPLIER_ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Ref Constraints for Table SUPPLIER
--------------------------------------------------------

  ALTER TABLE "PARTH1"."SUPPLIER" ADD CONSTRAINT "SUPPLIER_FK1" FOREIGN KEY ("SUPPLIER_CONTACT_ID")
	  REFERENCES "PARTH1"."SUPPLIER_CONTACT" ("SUPPLIER_CONTACT_ID") ENABLE;
