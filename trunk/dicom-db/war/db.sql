CONNECT 'jdbc:derby://localhost:1527//WORKDB/WEBDICOM;create=true';

CREATE TABLE WEBDICOM.DCMFILES (
	ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	DCM_FILE_NAME VARCHAR(512) NOT NULL,
	PATIENT_BIRTH_DATE DATE NOT NULL,
	PATIENT_NAME VARCHAR(512) NOT NULL,
	STUDY_DATE DATE NOT NULL,
	CONSTRAINT WEBDICOM.PK_DCMFILES PRIMARY KEY (ID),
	UNIQUE (DCM_FILE_NAME)
);

CREATE TABLE WEBDICOM.STUDIES (
	ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	FID INTEGER NOT NULL,	
	DCM_FILE_NAME VARCHAR(512) NOT NULL,
	PATIENT_BIRTH_DATE DATE NOT NULL,
	PATIENT_NAME VARCHAR(512) NOT NULL,
	STUDY_DATE DATE NOT NULL,
	CONSTRAINT WEBDICOM.PK_STUDIES PRIMARY KEY (ID),
	CONSTRAINT WEBDICOM.FK_STUDIES_DCMFILES FOREIGN KEY (FID) REFERENCES WEBDICOM.DCMFILES (ID),
	UNIQUE (DCM_FILE_NAME)
);

CREATE TABLE WEBDICOM.SERIES (
	ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	FID INTEGER NOT NULL,	
	DCM_FILE_NAME VARCHAR(512) NOT NULL,
	PATIENT_BIRTH_DATE DATE NOT NULL,
	PATIENT_NAME VARCHAR(512) NOT NULL,
	STUDY_DATE DATE NOT NULL,
	CONSTRAINT WEBDICOM.PK_SERIES PRIMARY KEY (ID),
	CONSTRAINT WEBDICOM.FK_SERIES_STUDIES FOREIGN KEY (FID) REFERENCES WEBDICOM.STUDIES (ID),
	UNIQUE (DCM_FILE_NAME)
);

CREATE TABLE WEBDICOM.IMAGES (
	ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	FID INTEGER NOT NULL,	
	DCM_FILE_NAME VARCHAR(512) NOT NULL,
	PATIENT_BIRTH_DATE DATE NOT NULL,
	PATIENT_NAME VARCHAR(512) NOT NULL,
	STUDY_DATE DATE NOT NULL,
	CONSTRAINT WEBDICOM.PK_IMAGES PRIMARY KEY (ID),
	CONSTRAINT WEBDICOM.FK_IMAGES_SERIES FOREIGN KEY (FID) REFERENCES WEBDICOM.SERIES (ID),
	UNIQUE (DCM_FILE_NAME)
); 
