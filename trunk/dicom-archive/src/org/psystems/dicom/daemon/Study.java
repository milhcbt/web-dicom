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
	private String StudyOperator;
	private String StudyDescription;// Tag.MedicalAlerts
	private String PatientShortName;
	protected String ManufacturerModelName;
	// Tag.Manufacturer
	// (0008,0070) LO #18 [JV HELPIC (MOSCOW)] Manufacturer
	protected String Manufacturer;
	protected String StudyType;
	protected String StudyResult;
	protected String StudyViewProtocol;
	
	// TODO Manufacturer в файлах не фигурирует...
	protected String ManufacturerUID = "empty";

	// TODO в файлах не фигурирует
	private java.sql.Date StudyViewProtocolDate = null;

	// TODO Тип файла (снимок, описание). пока не сделано.
	protected String DcmType = "empty";

	private static Logger LOG = LoggerFactory.getLogger(Study.class);
	
	public static Study getInstance(DicomObject dcmObj) {
		
		// LookInside
		// (0002,0002) UI #26 [1.2.826.0.1.3680043.2.706.5476834] Media Storage
		// SOP Class UID

		// KRT Electron
		// (0002,0002) UI #26 [1.2.840.10008.5.1.4.1.1.1] Media Storage SOP
		// Class UID
		// (0009,0010) LO #20 [KRT_ELECTRON_PRIVATE] Private Creator Data
		// Element

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


		DicomElement element = dcmObj.get(Tag.MediaStorageSOPClassUID);
		String MediaStorageSOPClassUID = element.getValueAsString(cs, element
				.length());

		if (MediaStorageSOPClassUID.equals("1.2.840.10008.5.1.4.1.1.1")) {
			int tag = 0x00090010;
			element = dcmObj.get(tag);
			if (element != null
					&& element.getValueAsString(cs, element.length()).equals(
							"KRT_ELECTRON_PRIVATE")) {
				
				return new StudyImplElektron(dcmObj);
				
			}
		}
		if (MediaStorageSOPClassUID.equals("1.2.826.0.1.3680043.2.706.5476834")) {
			return new StudyImpLookInside(dcmObj);
			
		}
		
		//Общий драйвер
		Study study = new Study();
		study.implCommon(dcmObj);
		return study;
	}
	
	/**
	 * @param dcmObj
	 */
	protected void implCommon(DicomObject dcmObj) {


		
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
		setCs(cs);

		// StudyInstanceUID
		DicomElement element = dcmObj.get(Tag.StudyInstanceUID);
		setStudyInstanceUID("empty");
		if (element == null) {
			LOG.warn("Study ID (tag: StudyUID) is empty!");
			
		} else {
			setStudyInstanceUID(element.getValueAsString(cs, element
					.length()));
		}
		
		// Manufacturer
		element = dcmObj.get(Tag.Manufacturer);
		setManufacturer("empty");
		if (element == null) {
			LOG.warn("Study ID (tag: Manufacturer) is empty!");
			
		} else {
			setManufacturer(element.getValueAsString(cs, element
					.length()));
		}

		// Modality
		setModality("empty");
		element = dcmObj.get(Tag.Modality);
		if (element == null) {
			LOG.warn("Study ID (tag: Modality) is empty!");
		} else {
			setModality(element.getValueAsString(cs, element.length()));
		}

		// StudyID
		element = dcmObj.get(Tag.StudyID);
		setStudyID("empty");
		if (element == null) {
			LOG.warn("Study ID (tag: StudyID) is empty!");
		} else {
			setStudyID(element.getValueAsString(cs, element.length()));
		}

		// PatientBirthDate
		if (dcmObj.get(Tag.PatientBirthDate) != null) {
			setPatientBirthDate(new java.sql.Date(dcmObj.get(
					Tag.PatientBirthDate).getDate(false).getTime()));
		} else {
			setPatientBirthDate( new java.sql.Date(0));
			LOG.warn("Patient Birth Date (tag: PatientBirthDate) is empty!");
		}

		// PatientName
		element = dcmObj.get(Tag.PatientName);
		setPatientName("empty");
		if (element == null) {
			LOG.warn("Patien Name (tag: PatientName) is empty!");
		} else {
			setPatientName(element
					.getValueAsString(cs, element.length()));
		}

		// PatientID
		element = dcmObj.get(Tag.PatientID);
		setPatientID("empty");
		if (element == null) {
			LOG.warn("Patien ID (tag: PatientID) is empty!");
		} else {
			setPatientID(element.getValueAsString(cs, element.length()));
		}

		// PatientSex
		element = dcmObj.get(Tag.PatientSex);
		setPatientSex("");
		if (element == null) {
			LOG.warn("Patient sex (tag: PatientSex) is empty!");
		} else {
			setPatientSex(element.getValueAsString(cs, element.length()));
			if (getPatientSex().length() > 1) {
				LOG.warn("PATIENT_SEX to long [" + getPatientSex() + "]");
				setPatientSex(getPatientSex().substring(0, 1));
			}
		}

		// StudyDate
		if (dcmObj.get(Tag.StudyDate) != null) {
			setStudyDate( new java.sql.Date(dcmObj.get(Tag.StudyDate)
					.getDate(false).getTime()));
		} else {
			setStudyDate (new java.sql.Date(0));
			LOG.warn("Patient Birth Date (tag: StudyDate) is empty!");
		}

		// StudyDoctor (Tag.ReferringPhysicianName)
		setStudyDoctor("empty");
		element = dcmObj.get(Tag.ReferringPhysicianName);
		if (element != null) {
			setStudyDoctor(element
					.getValueAsString(cs, element.length()));
			if (getStudyDoctor() == null || getStudyDoctor().length() == 0) {
				setStudyDoctor("not defined");
			}
		}

		// OperatorsName
		setStudyOperator("empty");
		element = dcmObj.get(Tag.OperatorsName);
		if (element != null) {
			setStudyOperator(element.getValueAsString(cs, element
					.length()));
			if (getStudyOperator() == null
					|| getStudyOperator().length() == 0) {
				setStudyOperator ("not defined");
			}
		}

		// StudyDescription (Tag.MedicalAlerts)
		setStudyDescription("empty");
		element = dcmObj.get(Tag.MedicalAlerts);
		if (element != null) {
			setStudyDescription(element.getValueAsString(cs, element
					.length()));
			if (getStudyDescription() == null
					|| getStudyDescription().length() == 0) {
				setStudyDescription( "not defined");
			}
		}

		// PatientShortName (это КБП)
		setPatientShortName(Extractor.makeShortName(getPatientName(),
				getPatientBirthDate()));
		if (getPatientShortName() == null
				|| getPatientShortName().length() == 0) {
			setPatientShortName("notmuch");
		}

		// Date STUDY_VIEW_PROTOCOL_DATE = null;// TODO Проверить Дата ли
		// // возвращается или строка
		// String STUDY_MANUFACTURER_UID = "empty";// TODO Реализовать!!!
		// String DCM_TYPE = "empty";// Тип файла (снимок,
		// // исследование) TODO
		// // Реализовать!!!

		
		
	}
	
	

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

	public void setStudyOperator(String operatorsName) {
		StudyOperator = operatorsName;
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

	public String getStudyOperator() {
		return StudyOperator;
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

	public String getManufacturer() {
		return Manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		Manufacturer = manufacturer;
	}
	
	

}
