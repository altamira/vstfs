-- QUOTATION
INSERT INTO QUOTATION (QUOTATION_ID, QUOTATION_CREATOR, QUOTATION_DATE)
            VALUES(QUOTATION_SEQUENCE.NEXTVAL ,'PARTH',SYSDATE);
            
--------------------------------------------------------------------------------
-- QUOTATION_REQUEST
            
INSERT INTO QUOTATION_REQUEST (QUOTATION_ID, QUOTATION_REQUEST_ID, REQUEST_ID)
            VALUES('1', QUOTATION_REQUEST_SEQUENCE.nextval, '1');

INSERT INTO QUOTATION_REQUEST (QUOTATION_ID, QUOTATION_REQUEST_ID, REQUEST_ID)
            VALUES('1', QUOTATION_REQUEST_SEQUENCE.NEXTVAL, '2');
            
--------------------------------------------------------------------------------
-- QUOTATION_ITEM

insert into QUOTATION_ITEM (QUOTATION_ITEM_ID, QUOTATION_ID, MATERIAL_LAMINATION, MATERIAL_TREATMENT, MATERIAL_THICKNESS, MATERIAL_STANDARD, REQUEST_WEIGHT)
select quotation_item_sequence.nextval, quotation_id, material_lamination, material_treatment, material_thickness, standard_id, request_weight
from(
select
  QR.QUOTATION_ID,
  M.MATERIAL_LAMINATION,
  M.MATERIAL_TREATMENT,
  M.MATERIAL_THICKNESS,
  S.STANDARD_ID,
  SUM(RI.REQUEST_WEIGHT) AS REQUEST_WEIGHT
FROM QUOTATION_REQUEST QR INNER JOIN REQUEST R ON QR.REQUEST_ID = R.REQUEST_ID
                          INNER JOIN REQUEST_ITEM RI ON R.REQUEST_ID = RI.REQUEST_ID
                          INNER JOIN MATERIAL M ON RI.MATERIAL_ID = M.MATERIAL_ID
                          INNER JOIN MATERIAL_STANDARD MS ON M.MATERIAL_ID = MS.MATERIAL_ID
                          INNER JOIN STANDARD S ON MS.STANDARD_ID = S.STANDARD_ID
WHERE QR.QUOTATION_ID = '1'
  AND MS.STANDARD_ACCEPT = 'Y'
GROUP BY QR.QUOTATION_ID,
  M.MATERIAL_LAMINATION,
  M.MATERIAL_TREATMENT,
  m.material_thickness,
  s.standard_id);
  
--------------------------------------------------------------------------------
-- QUOTATION_ITEM_QUOTE

insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '1', '1', '780','1','8,6');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '2', '1', '820','1','8,2');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '3', '1', '790','1','9,6');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '4', '1', '1100','1','10');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '5', '1', '1000','1','5,6');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '6', '1', '985','1','8,4');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '7', '1', '987','1','7,6');
            
------------------

insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '1', '2', '790','LR','8,6');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '2', '2', '800','LR','8,2');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '3', '2', '800','LR','7,8');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '4', '2', '1050','LR','8');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '5', '2', '960','LR','6,5');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '6', '2', '1200','LR','7,5');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '7', '2', '1100','LR','9,6');
            
--------------------

insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '1', '3', '850','MULTI','6,8');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '2', '3', '900','MULTI','5,2');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '3', '3', '850','MULTI','8,7');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '4', '3', '1050','MULTI','9');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '5', '3', '900','MULTI','4,5');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '6', '3', '1000','MULTI','8,5');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '7', '3', '980','MULTI','10,6');
            
---------------------

insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '1', '4', '1000','MS','8,5');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '2', '4', '750','MS','5,9');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '3', '4', '750','MS','4,8');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '4', '4', '954','MS','5,6');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '5', '4', '1025','MS','7,8');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '6', '4', '950','MS','10,5');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '7', '4', '900','MS','10,6');
            
----------------------

insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '1', '5', '980','KL','9,5');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '2', '5', '1100','KL','8,9');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '3', '5', '850','KL','12,8');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '4', '5', '960','KL','10,6');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '5', '5', '1000','KL','4,8');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '6', '5', '860','KL','5,5');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '7', '5', '950','KL','7,6');
            
---------------------

insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '1', '6', '1080','ACO','5,5');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '2', '6', '1200','ACO','4,9');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '3', '6', '950','ACO','6,8');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '4', '6', '1060','ACO','5,6');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '5', '6', '800','ACO','3,8');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '6', '6', '1260','ACO','3,5');
            
insert into quotation_item_quote (quotation_item_quote_id, quotation_item_id, supplier_id, supplier_price, supplier_standard, supplier_weight)
            values(quotation_item_quote_sequence.nextval, '7', '6', '1050','ACO','2,6');