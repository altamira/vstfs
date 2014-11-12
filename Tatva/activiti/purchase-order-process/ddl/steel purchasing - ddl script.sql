--------------------------------------------------------
--  DDL for Table MATERIAL
--------------------------------------------------------

  CREATE TABLE "PARTH1"."MATERIAL" 
   (	"MATERIAL_ID" NUMBER, 
	"MATERIAL_CODE" VARCHAR2(20 BYTE), 
	"MATERIAL_LAMINATION" CHAR(2 BYTE), 
	"MATERIAL_TREATMENT" CHAR(2 BYTE), 
	"MATERIAL_THICKNESS" NUMBER(10,2), 
	"MATERIAL_WIDTH" NUMBER(10,2), 
	"MATERIAL_LENGTH" NUMBER(10,2)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  DDL for Index MATERIAL_PK

  CREATE UNIQUE INDEX "PARTH1"."MATERIAL_PK" ON "PARTH1"."MATERIAL" ("MATERIAL_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  DDL for Index MATERIAL_UK1

  CREATE UNIQUE INDEX "PARTH1"."MATERIAL_UK1" ON "PARTH1"."MATERIAL" ("MATERIAL_CODE") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  Constraints for Table MATERIAL

  ALTER TABLE "PARTH1"."MATERIAL" ADD CONSTRAINT "MATERIAL_UK1" UNIQUE ("MATERIAL_CODE")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."MATERIAL" ADD CONSTRAINT "MATERIAL_PK" PRIMARY KEY ("MATERIAL_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."MATERIAL" MODIFY ("MATERIAL_WIDTH" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."MATERIAL" MODIFY ("MATERIAL_THICKNESS" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."MATERIAL" MODIFY ("MATERIAL_TREATMENT" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."MATERIAL" MODIFY ("MATERIAL_LAMINATION" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."MATERIAL" MODIFY ("MATERIAL_CODE" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."MATERIAL" MODIFY ("MATERIAL_ID" NOT NULL ENABLE);

 --------------------------------------------------------
--  DDL for Table SUPPLIER
--------------------------------------------------------

  CREATE TABLE "PARTH1"."SUPPLIER" 
   (	"SUPPLIER_ID" NUMBER, 
	"SUPPLIER_NAME" VARCHAR2(50 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  DDL for Index SUPPLIER_PK

  CREATE UNIQUE INDEX "PARTH1"."SUPPLIER_PK" ON "PARTH1"."SUPPLIER" ("SUPPLIER_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  DDL for Index SUPPLIER_UK1

  CREATE UNIQUE INDEX "PARTH1"."SUPPLIER_UK1" ON "PARTH1"."SUPPLIER" ("SUPPLIER_NAME") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  Constraints for Table SUPPLIER

  ALTER TABLE "PARTH1"."SUPPLIER" ADD CONSTRAINT "SUPPLIER_UK1" UNIQUE ("SUPPLIER_NAME")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."SUPPLIER" ADD CONSTRAINT "SUPPLIER_PK" PRIMARY KEY ("SUPPLIER_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."SUPPLIER" MODIFY ("SUPPLIER_NAME" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."SUPPLIER" MODIFY ("SUPPLIER_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  DDL for Table STANDARD
--------------------------------------------------------

  CREATE TABLE "PARTH1"."STANDARD" 
   (	"STANDARD_ID" NUMBER, 
	"STANDARD_NAME" VARCHAR2(20 BYTE), 
	"STANDARD_DESCRIPTION" CLOB
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" 
 LOB ("STANDARD_DESCRIPTION") STORE AS BASICFILE (
  TABLESPACE "SYSTEM" ENABLE STORAGE IN ROW CHUNK 8192 RETENTION 
  NOCACHE LOGGING 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)) ;
REM INSERTING into PARTH1.STANDARD

--  DDL for Index STARDARD_PK

  CREATE UNIQUE INDEX "PARTH1"."STARDARD_PK" ON "PARTH1"."STANDARD" ("STANDARD_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  DDL for Index STARDARD_UK1

  CREATE UNIQUE INDEX "PARTH1"."STARDARD_UK1" ON "PARTH1"."STANDARD" ("STANDARD_NAME") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  Constraints for Table STANDARD

  ALTER TABLE "PARTH1"."STANDARD" ADD CONSTRAINT "STANDARD_UK1" UNIQUE ("STANDARD_NAME")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."STANDARD" ADD CONSTRAINT "STANDARD_PK" PRIMARY KEY ("STANDARD_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."STANDARD" MODIFY ("STANDARD_DESCRIPTION" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."STANDARD" MODIFY ("STANDARD_NAME" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."STANDARD" MODIFY ("STANDARD_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  DDL for Table MATERIAL_STANDARD
--------------------------------------------------------

  CREATE TABLE "PARTH1"."MATERIAL_STANDARD" 
   (	"MATERIAL_ID" NUMBER, 
	"STANDARD_ID" NUMBER, 
	"STANDARD_ACCEPT" CHAR(1 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  DDL for Index MATERIAL_STANDARD_PK

  CREATE UNIQUE INDEX "PARTH1"."MATERIAL_STANDARD_PK" ON "PARTH1"."MATERIAL_STANDARD" ("MATERIAL_ID", "STANDARD_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  Constraints for Table MATERIAL_STANDARD

  ALTER TABLE "PARTH1"."MATERIAL_STANDARD" ADD CONSTRAINT "MATERIAL_STANDARD_PK" PRIMARY KEY ("MATERIAL_ID", "STANDARD_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."MATERIAL_STANDARD" MODIFY ("STANDARD_ACCEPT" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."MATERIAL_STANDARD" MODIFY ("STANDARD_ID" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."MATERIAL_STANDARD" MODIFY ("MATERIAL_ID" NOT NULL ENABLE);

--  Ref Constraints for Table MATERIAL_STANDARD

  ALTER TABLE "PARTH1"."MATERIAL_STANDARD" ADD CONSTRAINT "MATERIAL_STANDARD_FK1" FOREIGN KEY ("MATERIAL_ID")
	  REFERENCES "PARTH1"."MATERIAL" ("MATERIAL_ID") ENABLE;
  ALTER TABLE "PARTH1"."MATERIAL_STANDARD" ADD CONSTRAINT "MATERIAL_STANDARD_FK2" FOREIGN KEY ("STANDARD_ID")
	  REFERENCES "PARTH1"."STANDARD" ("STANDARD_ID") ENABLE;

--------------------------------------------------------
--  DDL for Table SUPPLIER_STANDARD
--------------------------------------------------------

  CREATE TABLE "PARTH1"."SUPPLIER_STANDARD" 
   (	"SUPPLIER_ID" NUMBER, 
	"STANDARD_ID" NUMBER, 
	"STANDARD_AVAILABLE" CHAR(1 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  DDL for Index SUPPLIER_STANDARD_PK

  CREATE UNIQUE INDEX "PARTH1"."SUPPLIER_STANDARD_PK" ON "PARTH1"."SUPPLIER_STANDARD" ("SUPPLIER_ID", "STANDARD_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  Constraints for Table SUPPLIER_STANDARD

  ALTER TABLE "PARTH1"."SUPPLIER_STANDARD" ADD CONSTRAINT "SUPPLIER_STANDARD_PK" PRIMARY KEY ("SUPPLIER_ID", "STANDARD_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."SUPPLIER_STANDARD" MODIFY ("STANDARD_AVAILABLE" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."SUPPLIER_STANDARD" MODIFY ("STANDARD_ID" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."SUPPLIER_STANDARD" MODIFY ("SUPPLIER_ID" NOT NULL ENABLE);

--  Ref Constraints for Table SUPPLIER_STANDARD

  ALTER TABLE "PARTH1"."SUPPLIER_STANDARD" ADD CONSTRAINT "SUPPLIER_STANDARD_FK1" FOREIGN KEY ("SUPPLIER_ID")
	  REFERENCES "PARTH1"."SUPPLIER" ("SUPPLIER_ID") ENABLE;
  ALTER TABLE "PARTH1"."SUPPLIER_STANDARD" ADD CONSTRAINT "SUPPLIER_STANDARD_FK2" FOREIGN KEY ("STANDARD_ID")
	  REFERENCES "PARTH1"."STANDARD" ("STANDARD_ID") ENABLE;

--------------------------------------------------------
--  DDL for Table REQUEST
--------------------------------------------------------

  CREATE TABLE "PARTH1"."REQUEST" 
   (	"REQUEST_ID" NUMBER, 
	"REQUEST_DATE" DATE, 
	"REQUEST_CREATOR" VARCHAR2(50 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  DDL for Index REQUEST_PK

  CREATE UNIQUE INDEX "PARTH1"."REQUEST_PK" ON "PARTH1"."REQUEST" ("REQUEST_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  Constraints for Table REQUEST

  ALTER TABLE "PARTH1"."REQUEST" ADD CONSTRAINT "REQUEST_PK" PRIMARY KEY ("REQUEST_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."REQUEST" MODIFY ("REQUEST_CREATOR" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."REQUEST" MODIFY ("REQUEST_DATE" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."REQUEST" MODIFY ("REQUEST_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  DDL for Table REQUEST_ITEM
--------------------------------------------------------

  CREATE TABLE "PARTH1"."REQUEST_ITEM" 
   (	"REQUEST_ITEM_ID" NUMBER, 
	"REQUEST_ID" NUMBER, 
	"MATERIAL_ID" NUMBER, 
	"REQUEST_DATE" DATE, 
	"REQUEST_WEIGHT" NUMBER(10,2)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  DDL for Index REQUEST_ITEM_PK

  CREATE UNIQUE INDEX "PARTH1"."REQUEST_ITEM_PK" ON "PARTH1"."REQUEST_ITEM" ("REQUEST_ITEM_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  Constraints for Table REQUEST_ITEM

  ALTER TABLE "PARTH1"."REQUEST_ITEM" ADD CONSTRAINT "REQUEST_ITEM_PK" PRIMARY KEY ("REQUEST_ITEM_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."REQUEST_ITEM" MODIFY ("REQUEST_WEIGHT" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."REQUEST_ITEM" MODIFY ("REQUEST_DATE" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."REQUEST_ITEM" MODIFY ("MATERIAL_ID" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."REQUEST_ITEM" MODIFY ("REQUEST_ID" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."REQUEST_ITEM" MODIFY ("REQUEST_ITEM_ID" NOT NULL ENABLE);

--  Ref Constraints for Table REQUEST_ITEM

  ALTER TABLE "PARTH1"."REQUEST_ITEM" ADD CONSTRAINT "REQUEST_ITEM_FK1" FOREIGN KEY ("REQUEST_ID")
	  REFERENCES "PARTH1"."REQUEST" ("REQUEST_ID") ENABLE;
  ALTER TABLE "PARTH1"."REQUEST_ITEM" ADD CONSTRAINT "REQUEST_ITEM_FK2" FOREIGN KEY ("MATERIAL_ID")
	  REFERENCES "PARTH1"."MATERIAL" ("MATERIAL_ID") ENABLE;

--------------------------------------------------------
--  DDL for Table QUOTATION
--------------------------------------------------------

  CREATE TABLE "PARTH1"."QUOTATION" 
   (	"QUOTATION_ID" NUMBER, 
	"QUOTATION_DATE" DATE, 
	"QUOTATION_CREATOR" VARCHAR2(50 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  DDL for Index QUOTATION_PK

  CREATE UNIQUE INDEX "PARTH1"."QUOTATION_PK" ON "PARTH1"."QUOTATION" ("QUOTATION_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  Constraints for Table QUOTATION

  ALTER TABLE "PARTH1"."QUOTATION" ADD CONSTRAINT "QUOTATION_PK" PRIMARY KEY ("QUOTATION_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."QUOTATION" MODIFY ("QUOTATION_CREATOR" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."QUOTATION" MODIFY ("QUOTATION_DATE" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."QUOTATION" MODIFY ("QUOTATION_ID" NOT NULL ENABLE);

--------------------------------------------------------
--  DDL for Table QUOTATION_ITEM
--------------------------------------------------------

  CREATE TABLE "PARTH1"."QUOTATION_ITEM" 
   (	"QUOTATION_ITEM_ID" NUMBER, 
	"QUOTATION_ID" NUMBER, 
	"MATERIAL_LAMINATION" VARCHAR2(2 BYTE), 
	"MATERIAL_TREATMENT" VARCHAR2(2 BYTE), 
	"MATERIAL_THICKNESS" NUMBER(10,2), 
	"MATERIAL_STANDARD" NUMBER, 
	"SUPPLIER_ID" NUMBER, 
	"SUPPLIER_PRICE" NUMBER(10,2), 
	"SUPPLIER_WEIGHT" NUMBER(10,2)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  DDL for Index QUOTATION_ITEM_PK

  CREATE UNIQUE INDEX "PARTH1"."QUOTATION_ITEM_PK" ON "PARTH1"."QUOTATION_ITEM" ("QUOTATION_ITEM_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  Constraints for Table QUOTATION_ITEM

  ALTER TABLE "PARTH1"."QUOTATION_ITEM" ADD CONSTRAINT "QUOTATION_ITEM_PK" PRIMARY KEY ("QUOTATION_ITEM_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."QUOTATION_ITEM" MODIFY ("SUPPLIER_WEIGHT" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."QUOTATION_ITEM" MODIFY ("SUPPLIER_PRICE" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."QUOTATION_ITEM" MODIFY ("SUPPLIER_ID" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."QUOTATION_ITEM" MODIFY ("MATERIAL_STANDARD" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."QUOTATION_ITEM" MODIFY ("MATERIAL_THICKNESS" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."QUOTATION_ITEM" MODIFY ("MATERIAL_TREATMENT" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."QUOTATION_ITEM" MODIFY ("MATERIAL_LAMINATION" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."QUOTATION_ITEM" MODIFY ("QUOTATION_ID" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."QUOTATION_ITEM" MODIFY ("QUOTATION_ITEM_ID" NOT NULL ENABLE);

--  Ref Constraints for Table QUOTATION_ITEM

  ALTER TABLE "PARTH1"."QUOTATION_ITEM" ADD CONSTRAINT "QUOTATION_ITEM_FK1" FOREIGN KEY ("QUOTATION_ID")
	  REFERENCES "PARTH1"."QUOTATION" ("QUOTATION_ID") ENABLE;
  ALTER TABLE "PARTH1"."QUOTATION_ITEM" ADD CONSTRAINT "QUOTATION_ITEM_FK2" FOREIGN KEY ("SUPPLIER_ID")
	  REFERENCES "PARTH1"."SUPPLIER" ("SUPPLIER_ID") ENABLE;

--------------------------------------------------------
--  DDL for Table QUOTATION_REQUEST
--------------------------------------------------------

  CREATE TABLE "PARTH1"."QUOTATION_REQUEST" 
   (	"QUOTATION_REQUEST_ID " NUMBER, 
	"QUOTATION_ID" NUMBER, 
	"REQUEST_ID" NUMBER
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  DDL for Index QUOTATION_REQUEST_PK

  CREATE UNIQUE INDEX "PARTH1"."QUOTATION_REQUEST_PK" ON "PARTH1"."QUOTATION_REQUEST" ("QUOTATION_REQUEST_ID ") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  Constraints for Table QUOTATION_REQUEST

  ALTER TABLE "PARTH1"."QUOTATION_REQUEST" ADD CONSTRAINT "QUOTATION_REQUEST_PK" PRIMARY KEY ("QUOTATION_REQUEST_ID ")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."QUOTATION_REQUEST" MODIFY ("REQUEST_ID" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."QUOTATION_REQUEST" MODIFY ("QUOTATION_ID" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."QUOTATION_REQUEST" MODIFY ("QUOTATION_REQUEST_ID " NOT NULL ENABLE);

--  Ref Constraints for Table QUOTATION_REQUEST

  ALTER TABLE "PARTH1"."QUOTATION_REQUEST" ADD CONSTRAINT "QUOTATION_REQUEST_FK1" FOREIGN KEY ("QUOTATION_ID")
	  REFERENCES "PARTH1"."QUOTATION" ("QUOTATION_ID") ENABLE;
  ALTER TABLE "PARTH1"."QUOTATION_REQUEST" ADD CONSTRAINT "QUOTATION_REQUEST_FK2" FOREIGN KEY ("REQUEST_ID")
	  REFERENCES "PARTH1"."REQUEST" ("REQUEST_ID") ENABLE;

--------------------------------------------------------
--  DDL for Table SUPPLIER_IN_STOCK
--------------------------------------------------------

  CREATE TABLE "PARTH1"."SUPPLIER_IN_STOCK" 
   (	"SUPPLIER_STOCK_ID" NUMBER, 
	"QUOTATION_ITEM_ID" NUMBER, 
	"MATERIAL_WIDTH" NUMBER(10,2), 
	"MATERIAL_LENGTH" NUMBER(10,2), 
	"SUPPLIER_WEIGHT" NUMBER(10,2)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  DDL for Index SUPPLIER_IN_STOCK_PK

  CREATE UNIQUE INDEX "PARTH1"."SUPPLIER_IN_STOCK_PK" ON "PARTH1"."SUPPLIER_IN_STOCK" ("SUPPLIER_STOCK_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  Constraints for Table SUPPLIER_IN_STOCK

  ALTER TABLE "PARTH1"."SUPPLIER_IN_STOCK" ADD CONSTRAINT "SUPPLIER_IN_STOCK_PK" PRIMARY KEY ("SUPPLIER_STOCK_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."SUPPLIER_IN_STOCK" MODIFY ("SUPPLIER_WEIGHT" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."SUPPLIER_IN_STOCK" MODIFY ("MATERIAL_WIDTH" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."SUPPLIER_IN_STOCK" MODIFY ("QUOTATION_ITEM_ID" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."SUPPLIER_IN_STOCK" MODIFY ("SUPPLIER_STOCK_ID" NOT NULL ENABLE);

--  Ref Constraints for Table SUPPLIER_IN_STOCK

  ALTER TABLE "PARTH1"."SUPPLIER_IN_STOCK" ADD CONSTRAINT "SUPPLIER_IN_STOCK_FK1" FOREIGN KEY ("QUOTATION_ITEM_ID")
	  REFERENCES "PARTH1"."QUOTATION_ITEM" ("QUOTATION_ITEM_ID") ENABLE;

--------------------------------------------------------
--  DDL for Table PURCHASE_PLANNING
--------------------------------------------------------

  CREATE TABLE "PARTH1"."PURCHASE_PLANNING" 
   (	"PLANNING_ID" NUMBER, 
	"QUOTATION_ID" NUMBER, 
	"PLANNING_DATE" DATE, 
	"PLANNING_OWNER" VARCHAR2(50 BYTE), 
	"PLANNING_APPROVE_DATE" DATE, 
	"PLANNING_APPROVE_USER" VARCHAR2(50 BYTE)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  DDL for Index PURCHASE_PLANNING_PK

  CREATE UNIQUE INDEX "PARTH1"."PURCHASE_PLANNING_PK" ON "PARTH1"."PURCHASE_PLANNING" ("PLANNING_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  Constraints for Table PURCHASE_PLANNING

  ALTER TABLE "PARTH1"."PURCHASE_PLANNING" ADD CONSTRAINT "PURCHASE_PLANNING_PK" PRIMARY KEY ("PLANNING_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."PURCHASE_PLANNING" MODIFY ("PLANNING_OWNER" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."PURCHASE_PLANNING" MODIFY ("PLANNING_DATE" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."PURCHASE_PLANNING" MODIFY ("QUOTATION_ID" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."PURCHASE_PLANNING" MODIFY ("PLANNING_ID" NOT NULL ENABLE);

--  Ref Constraints for Table PURCHASE_PLANNING

  ALTER TABLE "PARTH1"."PURCHASE_PLANNING" ADD CONSTRAINT "PURCHASE_PLANNING_FK1" FOREIGN KEY ("QUOTATION_ID")
	  REFERENCES "PARTH1"."QUOTATION" ("QUOTATION_ID") ENABLE;

--------------------------------------------------------
--  DDL for Table PURCHASE_PLANNING_ITEM
--------------------------------------------------------

  CREATE TABLE "PARTH1"."PURCHASE_PLANNING_ITEM" 
   (	"PLANNING_ITEM_ID" NUMBER, 
	"PLANNING_ID" NUMBER, 
	"MATERIAL_ID" NUMBER, 
	"SUPPLIER_ID" NUMBER, 
	"PLANNING_DATE" DATE, 
	"PLANNING_WEIGHT" NUMBER(10,2)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  DDL for Index PURCHASE_PLANNING_ITEM_PK

  CREATE UNIQUE INDEX "PARTH1"."PURCHASE_PLANNING_ITEM_PK" ON "PARTH1"."PURCHASE_PLANNING_ITEM" ("PLANNING_ITEM_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  Constraints for Table PURCHASE_PLANNING_ITEM

  ALTER TABLE "PARTH1"."PURCHASE_PLANNING_ITEM" ADD CONSTRAINT "PURCHASE_PLANNING_ITEM_PK" PRIMARY KEY ("PLANNING_ITEM_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."PURCHASE_PLANNING_ITEM" MODIFY ("PLANNING_WEIGHT" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."PURCHASE_PLANNING_ITEM" MODIFY ("PLANNING_DATE" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."PURCHASE_PLANNING_ITEM" MODIFY ("SUPPLIER_ID" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."PURCHASE_PLANNING_ITEM" MODIFY ("MATERIAL_ID" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."PURCHASE_PLANNING_ITEM" MODIFY ("PLANNING_ID" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."PURCHASE_PLANNING_ITEM" MODIFY ("PLANNING_ITEM_ID" NOT NULL ENABLE);

--  Ref Constraints for Table PURCHASE_PLANNING_ITEM

  ALTER TABLE "PARTH1"."PURCHASE_PLANNING_ITEM" ADD CONSTRAINT "PURCHASE_PLANNING_ITEM_FK1" FOREIGN KEY ("PLANNING_ID")
	  REFERENCES "PARTH1"."PURCHASE_PLANNING" ("PLANNING_ID") ENABLE;
  ALTER TABLE "PARTH1"."PURCHASE_PLANNING_ITEM" ADD CONSTRAINT "PURCHASE_PLANNING_ITEM_FK2" FOREIGN KEY ("MATERIAL_ID")
	  REFERENCES "PARTH1"."MATERIAL" ("MATERIAL_ID") ENABLE;
  ALTER TABLE "PARTH1"."PURCHASE_PLANNING_ITEM" ADD CONSTRAINT "PURCHASE_PLANNING_ITEM_FK3" FOREIGN KEY ("SUPPLIER_ID")
	  REFERENCES "PARTH1"."SUPPLIER" ("SUPPLIER_ID") ENABLE;

--------------------------------------------------------
--  DDL for Table PURCHASE_ORDER
--------------------------------------------------------

  CREATE TABLE "PARTH1"."PURCHASE_ORDER" 
   (	"PURCHASE_ORDER_ID" NUMBER, 
	"PURCHASE_PLANNING_ID" NUMBER, 
	"SUPPLIER_ID" NUMBER, 
	"PURCHASE_ORDER_DATE" DATE
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  DDL for Index PURCHASE_ORDER_PK

  CREATE UNIQUE INDEX "PARTH1"."PURCHASE_ORDER_PK" ON "PARTH1"."PURCHASE_ORDER" ("PURCHASE_ORDER_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  Constraints for Table PURCHASE_ORDER

  ALTER TABLE "PARTH1"."PURCHASE_ORDER" ADD CONSTRAINT "PURCHASE_ORDER_PK" PRIMARY KEY ("PURCHASE_ORDER_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."PURCHASE_ORDER" MODIFY ("PURCHASE_ORDER_DATE" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."PURCHASE_ORDER" MODIFY ("SUPPLIER_ID" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."PURCHASE_ORDER" MODIFY ("PURCHASE_PLANNING_ID" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."PURCHASE_ORDER" MODIFY ("PURCHASE_ORDER_ID" NOT NULL ENABLE);

--  Ref Constraints for Table PURCHASE_ORDER

  ALTER TABLE "PARTH1"."PURCHASE_ORDER" ADD CONSTRAINT "PURCHASE_ORDER_PURCHASE_FK1" FOREIGN KEY ("PURCHASE_PLANNING_ID")
	  REFERENCES "PARTH1"."PURCHASE_PLANNING" ("PLANNING_ID") ENABLE;
  ALTER TABLE "PARTH1"."PURCHASE_ORDER" ADD CONSTRAINT "PURCHASE_ORDER_PURCHASE_FK2" FOREIGN KEY ("SUPPLIER_ID")
	  REFERENCES "PARTH1"."SUPPLIER" ("SUPPLIER_ID") ENABLE;

--------------------------------------------------------
--  DDL for Table PURCHASE_ORDER_ITEM
--------------------------------------------------------

  CREATE TABLE "PARTH1"."PURCHASE_ORDER_ITEM" 
   (	"PURCHASE_ORDER_ITEM_ID" NUMBER, 
	"PURCHASE_ORDER_ID" NUMBER, 
	"MATERIAL_ID" NUMBER, 
	"PURCHASE_ORDER_ITEM_DATE" DATE, 
	"PURCHASE_ORDER_ITEM_WEIGHT" NUMBER(10,2), 
	"PURCHASE_ORDER_ITEM_UNIT_PRICE" NUMBER(10,2), 
	"PURCHASE_ORDER_ITEM_TAX" NUMBER(10,2)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  DDL for Index PURCHASE_ORDER_ITEM_PK

  CREATE UNIQUE INDEX "PARTH1"."PURCHASE_ORDER_ITEM_PK" ON "PARTH1"."PURCHASE_ORDER_ITEM" ("PURCHASE_ORDER_ITEM_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

--  Constraints for Table PURCHASE_ORDER_ITEM

  ALTER TABLE "PARTH1"."PURCHASE_ORDER_ITEM" ADD CONSTRAINT "PURCHASE_ORDER_ITEM_PK" PRIMARY KEY ("PURCHASE_ORDER_ITEM_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "PARTH1"."PURCHASE_ORDER_ITEM" MODIFY ("PURCHASE_ORDER_ITEM_WEIGHT" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."PURCHASE_ORDER_ITEM" MODIFY ("PURCHASE_ORDER_ITEM_DATE" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."PURCHASE_ORDER_ITEM" MODIFY ("MATERIAL_ID" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."PURCHASE_ORDER_ITEM" MODIFY ("PURCHASE_ORDER_ID" NOT NULL ENABLE);
  ALTER TABLE "PARTH1"."PURCHASE_ORDER_ITEM" MODIFY ("PURCHASE_ORDER_ITEM_ID" NOT NULL ENABLE);

--  Ref Constraints for Table PURCHASE_ORDER_ITEM

  ALTER TABLE "PARTH1"."PURCHASE_ORDER_ITEM" ADD CONSTRAINT "PURCHASE_ORDER_ITEM_FK1" FOREIGN KEY ("PURCHASE_ORDER_ID")
	  REFERENCES "PARTH1"."PURCHASE_ORDER" ("PURCHASE_ORDER_ID") ENABLE;
  ALTER TABLE "PARTH1"."PURCHASE_ORDER_ITEM" ADD CONSTRAINT "PURCHASE_ORDER_ITEM_FK2" FOREIGN KEY ("MATERIAL_ID")
	  REFERENCES "PARTH1"."MATERIAL" ("MATERIAL_ID") ENABLE;