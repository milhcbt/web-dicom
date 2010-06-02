package org.psystems.dicom.daemon;

import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.SpecificCharacterSet;
import org.dcm4che2.data.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Кастомный класс описания исследования
 * 
 * @author dima_d
 * 
 */
public class Study {

	private SpecificCharacterSet cs;
	private String StudyInstanceUID;
	private String Modality;
	private String StudyID;
	private java.sql.Date PatientBirthDate;
	private String PatientName;
	private String PatientID;
	private String PatientSex;
	private java.sql.Date StudyDate;
	private String StudyDoctor;// Tag.ReferringPhysicianName
	private String OperatorsName;
	private String StudyDescription;// Tag.MedicalAlerts
	private String PatientShortName;
	protected String ManufacturerModelName;
	protected String StudyType;
	protected String StudyResult;
	protected String StudyViewProtocol;

	private static Logger LOG = LoggerFactory.getLogger(Study.class);
	
	public Study getInstance(DicomObject dcmObj) {


		Study study = new Study();
		SpecificCharacterSet cs;

		// SpecificCharacterSet
		if (dcmObj.get(Tag.SpecificCharacterSet) != null
				&& dcmObj.get(Tag.SpecificCharacterSet).length() > 0) {
			cs = SpecificCharacterSet.valueOf(dcmObj.get(
					Tag.SpecificCharacterSet).getStrings(null, false));
		} else {
			cs = new CharacterSetCp1251();
			LOG.warn("Character Ser (tag: SpecificCharacterSet) is empty!");
		}
		study.setCs(cs);

		// StudyInstanceUID
		DicomElement element1 = dcmObj.get(Tag.StudyInstanceUID);
		study.setStudyInstanceUID("empty");
		if (element1 == null) {
			LOG.warn("Study ID (tag: StudyID) is empty!");
		} else {
			study.setStudyInstanceUID(element1.getValueAsString(cs, element1
					.length()));
		}

		// Modality
		study.setModality("empty");
		element1 = dcmObj.get(Tag.Modality);
		if (element1 == null) {
			LOG.warn("Study ID (tag: Modality) is empty!");
		} else {
			study.setModality(element1.getValueAsString(cs, element1.length()));
		}

		// StudyID
		element1 = dcmObj.get(Tag.StudyID);
		study.setStudyID("empty");
		if (element1 == null) {
			LOG.warn("Study ID (tag: StudyID) is empty!");
		} else {
			study.setStudyID(element1.getValueAsString(cs, element1.length()));
		}

		// PatientBirthDate
		if (dcmObj.get(Tag.PatientBirthDate) != null) {
			study.setPatientBirthDate(new java.sql.Date(dcmObj.get(
					Tag.PatientBirthDate).getDate(false).getTime()));
		} else {
			study.setPatientBirthDate( new java.sql.Date(0));
			LOG.warn("Patient Birth Date (tag: PatientBirthDate) is empty!");
		}

		// PatientName
		element1 = dcmObj.get(Tag.PatientName);
		study.setPatientName("empty");
		if (element1 == null) {
			LOG.warn("Patien Name (tag: PatientName) is empty!");
		} else {
			study.setPatientName(element1
					.getValueAsString(cs, element1.length()));
		}

		// PatientID
		element1 = dcmObj.get(Tag.PatientID);
		study.setPatientID("empty");
		if (element1 == null) {
			LOG.warn("Patien ID (tag: PatientID) is empty!");
		} else {
			study.setPatientID(element1.getValueAsString(cs, element1.length()));
		}

		// PatientSex
		element1 = dcmObj.get(Tag.PatientSex);
		study.setPatientSex("");
		if (element1 == null) {
			LOG.warn("Patient sex (tag: PatientSex) is empty!");
		} else {
			study.setPatientSex(element1.getValueAsString(cs, element1.length()));
			if (study.getPatientSex().length() > 1) {
				LOG.warn("PATIENT_SEX to long [" + study.getPatientSex() + "]");
				study.setPatientSex(study.getPatientSex().substring(0, 1));
			}
		}

		// StudyDate
		if (dcmObj.get(Tag.StudyDate) != null) {
			study.setStudyDate( new java.sql.Date(dcmObj.get(Tag.StudyDate)
					.getDate(false).getTime()));
		} else {
			study.setStudyDate (new java.sql.Date(0));
			LOG.warn("Patient Birth Date (tag: StudyDate) is empty!");
		}

		// StudyDoctor (Tag.ReferringPhysicianName)
		study.setStudyDoctor("empty");
		element1 = dcmObj.get(Tag.ReferringPhysicianName);
		if (element1 != null) {
			study.setStudyDoctor(element1
					.getValueAsString(cs, element1.length()));
			if (study.getStudyDoctor() == null || study.getStudyDoctor().length() == 0) {
				study.setStudyDoctor("not defined");
			}
		}

		// OperatorsName
		study.setOperatorsName("empty");
		element1 = dcmObj.get(Tag.OperatorsName);
		if (element1 != null) {
			study.setOperatorsName(element1.getValueAsString(cs, element1
					.length()));
			if (study.getOperatorsName() == null
					|| study.getOperatorsName().length() == 0) {
				study.setOperatorsName ("not defined");
			}
		}

		// StudyDescription (Tag.MedicalAlerts)
		study.setStudyDescription("empty");
		element1 = dcmObj.get(Tag.MedicalAlerts);
		if (element1 != null) {
			study.setStudyDescription(element1.getValueAsString(cs, element1
					.length()));
			if (study.getStudyDescription() == null
					|| study.getStudyDescription().length() == 0) {
				study.setStudyDescription( "not defined");
			}
		}

		// PatientShortName (это КБП)
		study.setPatientShortName(Extractor.makeShortName(study.getPatientName(),
				study.getPatientBirthDate()));
		if (study.getPatientShortName() == null
				|| study.getPatientShortName().length() == 0) {
			study.setPatientShortName("notmuch");
		}

		// Date STUDY_VIEW_PROTOCOL_DATE = null;// TODO Проверить Дата ли
		// // возвращается или строка
		// String STUDY_MANUFACTURER_UID = "empty";// TODO Реализовать!!!
		// String DCM_TYPE = "empty";// Тип файла (снимок,
		// // исследование) TODO
		// // Реализовать!!!

		return study;
		
	}
	
	// TODO Manufacturer в файлах не фигурирует...
	protected String ManufacturerUID = "empty";

	// TODO в файлах не фигурирует
	private java.sql.Date StudyViewProtocolDate = null;

	// TODO Тип файла (снимок, описание). пока не сделано.
	protected String DcmType = "empty";

	public void setCs(SpecificCharacterSet cs) {
		this.cs = cs;
	}

	public void setStudyInstanceUID(String studyInstanceUID) {
		StudyInstanceUID = studyInstanceUID;
	}

	public void setModality(String modality) {
		Modality = modality;
	}

	public void setStudyID(String studyID) {
		StudyID = studyID;
	}

	public void setPatientBirthDate(java.sql.Date patientBirthDate) {
		PatientBirthDate = patientBirthDate;
	}

	public void setPatientName(String patientName) {
		PatientName = patientName;
	}

	public void setPatientID(String patientID) {
		PatientID = patientID;
	}

	public void setPatientSex(String patientSex) {
		PatientSex = patientSex;
	}

	public void setStudyDate(java.sql.Date studyDate) {
		StudyDate = studyDate;
	}

	public void setStudyDoctor(String studyDoctor) {
		StudyDoctor = studyDoctor;
	}

	public void setOperatorsName(String operatorsName) {
		OperatorsName = operatorsName;
	}

	public void setStudyDescription(String studyDescription) {
		StudyDescription = studyDescription;
	}

	public void setPatientShortName(String patientShortName) {
		PatientShortName = patientShortName;
	}

	public void setManufacturerModelName(String manufacturerModelName) {
		ManufacturerModelName = manufacturerModelName;
	}

	public void setStudyType(String studyType) {
		StudyType = studyType;
	}

	public void setStudyResult(String studyResult) {
		StudyResult = studyResult;
	}

	public void setStudyViewProtocol(String studyViewProtocol) {
		StudyViewProtocol = studyViewProtocol;
	}

	public void setManufacturerUID(String manufacturerUID) {
		ManufacturerUID = manufacturerUID;
	}

	public void setStudyViewProtocolDate(java.sql.Date studyViewProtocolDate) {
		StudyViewProtocolDate = studyViewProtocolDate;
	}

	public void setDcmType(String dcmType) {
		DcmType = dcmType;
	}

	public SpecificCharacterSet getCs() {
		return cs;
	}

	public String getStudyInstanceUID() {
		return StudyInstanceUID;
	}

	public String getModality() {
		return Modality;
	}

	public String getStudyID() {
		return StudyID;
	}

	public java.sql.Date getPatientBirthDate() {
		return PatientBirthDate;
	}

	public String getPatientName() {
		return PatientName;
	}

	public String getPatientID() {
		return PatientID;
	}

	public String getPatientSex() {
		return PatientSex;
	}

	public java.sql.Date getStudyDate() {
		return StudyDate;
	}

	public String getStudyDoctor() {
		return StudyDoctor;
	}

	public String getOperatorsName() {
		return OperatorsName;
	}

	public String getStudyDescription() {
		return StudyDescription;
	}

	public String getPatientShortName() {
		return PatientShortName;
	}

	public String getManufacturerModelName() {
		return ManufacturerModelName;
	}

	public String getStudyType() {
		return StudyType;
	}

	public String getStudyResult() {
		return StudyResult;
	}

	public String getStudyViewProtocol() {
		return StudyViewProtocol;
	}

	public String getManufacturerUID() {
		return ManufacturerUID;
	}

	public java.sql.Date getStudyViewProtocolDate() {
		return StudyViewProtocolDate;
	}

	public String getDcmType() {
		return DcmType;
	}

}
