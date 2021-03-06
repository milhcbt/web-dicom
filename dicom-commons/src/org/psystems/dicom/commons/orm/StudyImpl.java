/*
    WEB-DICOM - preserving and providing information to the DICOM devices
	
    Copyright (C) 2009-2010 psystems.org
    Copyright (C) 2009-2010 Dmitry Derenok 

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
    
    Russian translation <http://code.google.com/p/gpl3rus/wiki/LatestRelease>
     
    The Original Code is part of WEB-DICOM, an implementation hosted at 
    <http://code.google.com/p/web-dicom/>
    
    In the project WEB-DICOM used the library open source project dcm4che
    The Original Code is part of dcm4che, an implementation of DICOM(TM) in
    Java(TM), hosted at http://sourceforge.net/projects/dcm4che.
    
 */
package org.psystems.dicom.commons.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author dima_d
 * 
 */
public class StudyImpl extends Study {

	public static Study getInstance(long id) {
		StudyImpl stub = new StudyImpl();
		stub.setId(id);
		return stub;
	}

	public static List<Study> getStudues(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	public static Study[] getStudues(Connection connection,
			String studyModality, String patientName, String patientShortName,
			String patientBirthDate, String patientSex, String beginStudyDate,
			String endStudyDate) throws DataException {

		PreparedStatement psSelect = null;

		// TODO Сделать ограничение на количество возвращаемых строк

		// "SELECT * FROM WEBDICOM.STUDY"
		// + " WHERE UPPER(PATIENT_NAME) like UPPER( ? || '%')"
		// + " order by PATIENT_NAME, STUDY_DATE "

		String sqlAddon = "";

		if (studyModality != null && studyModality.length() > 0) {
			if (sqlAddon.length() != 0)
				sqlAddon += " AND ";
			sqlAddon += " STUDY_MODALITY = ? ";
		}

		if (patientName != null && patientName.length() > 0) {
			if (sqlAddon.length() != 0)
				sqlAddon += " AND ";
			sqlAddon += " UPPER(PATIENT_NAME) like UPPER( ? || '%') ";
		}

		if (patientShortName != null && patientShortName.length() > 0) {
			if (sqlAddon.length() != 0)
				sqlAddon += " AND ";
			sqlAddon += " PATIENT_SHORTNAME = ? ";
		}

		if (patientBirthDate != null && patientBirthDate.length() > 0) {
			if (sqlAddon.length() != 0)
				sqlAddon += " AND ";
			sqlAddon += " PATIENT_BIRTH_DATE = ? ";
		}

		if (patientSex != null && patientSex.length() > 0) {
			if (sqlAddon.length() != 0)
				sqlAddon += " AND ";
			sqlAddon += " PATIENT_SEX = ? ";
		}

		if (beginStudyDate != null && beginStudyDate.length() > 0
				&& endStudyDate != null && endStudyDate.length() > 0) {

			if (java.sql.Date.valueOf(beginStudyDate).getTime() > java.sql.Date
					.valueOf(endStudyDate).getTime()) {
				throw new DataException(new IllegalArgumentException(
						"beginStudyDate > endStudyDate"));
			}

			if (sqlAddon.length() != 0)
				sqlAddon += " AND ";
			sqlAddon += " STUDY_DATE BETWEEN ? AND ? ";
		} else {

			if (beginStudyDate != null && beginStudyDate.length() > 0
					&& (endStudyDate == null || endStudyDate.length() > 0)) {
				if (sqlAddon.length() != 0)
					sqlAddon += " AND ";
				sqlAddon += " STUDY_DATE > ? ";
			}

			if (endStudyDate != null && endStudyDate.length() > 0
					&& (beginStudyDate == null || beginStudyDate.length() > 0)) {
				if (sqlAddon.length() != 0)
					sqlAddon += " AND ";
				sqlAddon += " STUDY_DATE < ?  ";
			}
		}

		String sql = "SELECT * FROM WEBDICOM.STUDY" + " WHERE " + sqlAddon
				+ " order by PATIENT_NAME, STUDY_DATE ";

		// System.err.println("SQL:"+sql);

		IllegalArgumentException ex = new IllegalArgumentException(
				"All query arguments empty! Set any argument's");
		if (sqlAddon.length() == 0)
			throw new DataException(ex);

		try {

			psSelect = connection.prepareStatement(sql);
			int index = 1;

			if (studyModality != null && studyModality.length() > 0) {
				psSelect.setString(index++, studyModality);
			}

			if (patientName != null && patientName.length() > 0) {
				psSelect.setString(index++, patientName);
			}

			if (patientShortName != null && patientShortName.length() > 0) {
				psSelect.setString(index++, patientShortName);
			}

			if (patientBirthDate != null && patientBirthDate.length() > 0) {
				psSelect.setDate(index++, java.sql.Date
						.valueOf(patientBirthDate));
			}

			if (patientSex != null && patientSex.length() > 0) {
				psSelect.setString(index++, patientSex);
			}

			if (beginStudyDate != null && beginStudyDate.length() > 0
					&& endStudyDate != null && endStudyDate.length() > 0) {
				psSelect
						.setDate(index++, java.sql.Date.valueOf(beginStudyDate));
				psSelect.setDate(index++, java.sql.Date.valueOf(endStudyDate));
			}else {
				
				if (beginStudyDate != null && beginStudyDate.length() > 0
						&& (endStudyDate == null || endStudyDate.length() > 0)) {
					psSelect
					.setDate(index++, java.sql.Date.valueOf(beginStudyDate));
				}

				if (endStudyDate != null && endStudyDate.length() > 0
						&& (beginStudyDate == null || beginStudyDate.length() > 0)) {
					psSelect
					.setDate(index++, java.sql.Date.valueOf(endStudyDate));
				}
			}

			ResultSet rs = psSelect.executeQuery();

			ArrayList<Study> data = new ArrayList<Study>();

			while (rs.next()) {

				Study study = new StudyImpl();
				study.setId(rs.getLong("ID"));
				study.setStudyInstanceUID(rs.getString("STUDY_UID"));
				study.setStudyModality(rs.getString("STUDY_MODALITY"));
				study.setStudyType(rs.getString("STUDY_TYPE"));
				study.setStudyDescription(rs.getString("STUDY_DESCRIPTION"));
				study.setStudyDate(rs.getDate("STUDY_DATE"));
				study.setManufacturerModelUID(rs
						.getString("STUDY_MANUFACTURER_UID"));
				study.setManufacturerModelName(rs
						.getString("STUDY_MANUFACTURER_MODEL_NAME"));
				study.setStudyDoctor(rs.getString("STUDY_DOCTOR"));
				study.setStudyOperator(rs.getString("STUDY_OPERATOR"));
				study.setStudyViewprotocol(rs.getString("STUDY_VIEW_PROTOCOL"));
				study.setStudyViewprotocolDate(rs.getDate("STUDY_VIEW_PROTOCOL_DATE"));
				study.setStudyId(rs.getString("STUDY_ID"));
				study.setPatientName(rs.getString("PATIENT_NAME"));
				study.setPatientShortName(rs.getString("PATIENT_SHORTNAME"));
				study.setPatientSex(rs.getString("PATIENT_SEX"));
				study.setPatientBirthDate(rs.getDate("PATIENT_BIRTH_DATE"));
				study.setPatientId(rs.getString("PATIENT_ID"));
				study.setStudyResult(rs.getString("STUDY_RESULT"));
				study.setStudyUrl("");// TODO сделать!!
				study.setDcmFiles(new Long[] { 1l, 2l, 3l });// TODO сделать!!
				data.add(study);

			}
			rs.close();

			Study[] result = new Study[data.size()];
			return data.toArray(result);

		} catch (SQLException e) {
			throw new DataException(e);
		} finally {

			try {
				if (psSelect != null)
					psSelect.close();

			} catch (SQLException e) {
				throw new DataException(e);
			}
		}

	}
}
