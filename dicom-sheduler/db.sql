CONNECT 'jdbc:derby://localhost:1527//DICOM/DB/WEBDICOM;create=true';

CREATE TABLE WEBDICOM.DCMFILE (
	ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	PATIENT_ID VARCHAR(512) NOT NULL,
	DCM_FILE_NAME VARCHAR(512) NOT NULL,
	DCM_FILE_SIZE BIGINT NOT NULL,
	PATIENT_NAME VARCHAR(512) NOT NULL,
	PATIENT_BIRTH_DATE DATE NOT NULL,
	PATIENT_SEX VARCHAR(1),
	STUDY_ID VARCHAR(512) NOT NULL, 
	STUDY_DATE DATE NOT NULL,
	STUDY_DOCTOR  VARCHAR(512) NOT NULL, -- Referring Physician's Name
	STUDY_OPERATOR  VARCHAR(512) NOT NULL, -- Operators' Name
	CONSTRAINT WEBDICOM.PK_DCMFILES PRIMARY KEY (ID),
	UNIQUE (DCM_FILE_NAME)
);


CREATE TABLE WEBDICOM.IMAGES (
	ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	FID_DCMFILE INTEGER NOT NULL,
	IMAGE_FILE_NAME VARCHAR(512) NOT NULL,
	IMAGE_FILE_SIZE BIGINT NOT NULL,
	CONTENT_TYPE VARCHAR(512) NOT NULL,
	WIDTH INTEGER NOT NULL, -- COLUMNS
	HEIGHT INTEGER NOT NULL, -- ROWS
	CONSTRAINT WEBDICOM.PK_IMAGES PRIMARY KEY (ID),
	CONSTRAINT WEBDICOM.FK_IMAGES_DCMFILE FOREIGN KEY (FID_DCMFILE) REFERENCES WEBDICOM.DCMFILE (ID),
	UNIQUE (IMAGE_FILE_NAME)
);


CREATE TABLE WEBDICOM.STAT (
	ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	METRIC_NAME VARCHAR(512) NOT NULL,
	METRIC_DATE DATE NOT NULL,
	METRIC_VALUE_LONG BIGINT,
	CONSTRAINT WEBDICOM.PK_STAT PRIMARY KEY (ID),
	UNIQUE (METRIC_NAME,METRIC_DATE)
	--INDEX WEBDICOM.PK_STAT1 (METRIC_VALUE_LONG)
);

CREATE TABLE WEBDICOM.DAYSTAT (
	ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	METRIC_NAME VARCHAR(512) NOT NULL,
	METRIC_DATE DATE NOT NULL,
	METRIC_VALUE_LONG BIGINT,
	CONSTRAINT WEBDICOM.PK_DAYSTAT PRIMARY KEY (ID),
	UNIQUE (METRIC_NAME,METRIC_DATE)
	--INDEX (METRIC_VALUE_LONG)
);



