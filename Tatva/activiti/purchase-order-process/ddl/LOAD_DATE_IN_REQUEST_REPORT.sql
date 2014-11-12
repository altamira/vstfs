CREATE OR REPLACE DIRECTORY REQUEST AS 'D:\REQUEST';

DECLARE

G_ID_SEQ NUMBER;  

JASPER_BFILE BFILE := BFILENAME('REQUEST', 'Request.jasper');
JRXML_BFILE BFILE := BFILENAME('REQUEST', 'Request.jrxml');
LOGO_BFILE BFILE := BFILENAME('REQUEST', 'logo_altamira.png');

JASPER_LOB BLOB;
JRXML_LOB BLOB;
LOGO_LOB BLOB;
  
BEGIN

INSERT INTO REQUEST_REPORT (REPORT_ID, JASPER_FILE, JRXML_FILE, ALTAMIRA_LOGO)
VALUES (REQUEST_REPORT_SEQUENCE.NEXTVAL, EMPTY_BLOB(), EMPTY_BLOB(), EMPTY_BLOB())
RETURNING REPORT_ID INTO G_ID_SEQ;

SELECT JASPER_FILE, JRXML_FILE, ALTAMIRA_LOGO
INTO JASPER_LOB, JRXML_LOB, LOGO_LOB
FROM REQUEST_REPORT
WHERE REPORT_ID = G_ID_SEQ
FOR UPDATE;

-- Store .jasper file
DBMS_LOB.FILEOPEN(JASPER_BFILE, DBMS_LOB.FILE_READONLY);
DBMS_LOB.OPEN(JASPER_LOB, DBMS_LOB.LOB_READWRITE);
DBMS_LOB.LOADFROMFILE(JASPER_LOB, JASPER_BFILE, DBMS_LOB.GETLENGTH(JASPER_BFILE));
DBMS_LOB.FILECLOSE(JASPER_BFILE);
DBMS_LOB.CLOSE(JASPER_LOB);

-- Store .jrxml file
DBMS_LOB.FILEOPEN(JRXML_BFILE, DBMS_LOB.FILE_READONLY);
DBMS_LOB.OPEN(JRXML_LOB, DBMS_LOB.LOB_READWRITE);
DBMS_LOB.LOADFROMFILE(JRXML_LOB, JRXML_BFILE, DBMS_LOB.GETLENGTH(JRXML_BFILE));
DBMS_LOB.FILECLOSE(JRXML_BFILE);
DBMS_LOB.CLOSE(JRXML_LOB);

-- Store altamira logo
DBMS_LOB.FILEOPEN(LOGO_BFILE, DBMS_LOB.FILE_READONLY);
DBMS_LOB.OPEN(LOGO_LOB, DBMS_LOB.LOB_READWRITE);
DBMS_LOB.LOADFROMFILE(LOGO_LOB, LOGO_BFILE, DBMS_LOB.GETLENGTH(LOGO_BFILE));
DBMS_LOB.FILECLOSE(LOGO_BFILE);
DBMS_LOB.CLOSE(LOGO_LOB);

COMMIT;

END;
/