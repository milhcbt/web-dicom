package org.psystems.dicom.webservice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.log4j.Logger;
import org.psystems.dicom.commons.orm.ORMUtil;
import org.psystems.dicom.commons.orm.PersistentManagerDerby;
import org.psystems.dicom.commons.orm.entity.DataException;
import org.psystems.dicom.commons.orm.entity.Diagnosis;
import org.psystems.dicom.commons.orm.entity.Direction;
import org.psystems.dicom.commons.orm.entity.Employee;
import org.psystems.dicom.commons.orm.entity.ManufacturerDevice;
import org.psystems.dicom.commons.orm.entity.Patient;
import org.psystems.dicom.commons.orm.entity.QueryDirection;
import org.psystems.dicom.commons.orm.entity.QueryStudy;
import org.psystems.dicom.commons.orm.entity.Service;
import org.psystems.dicom.commons.orm.entity.Study;

/**
 * @author dima_d
 * 
 *         http://jax-ws.java.net/
 *         Типы данных:
 *         http://download.oracle.com/docs/cd/E12840_01/wls/docs103/webserv/data_types.html
 */
@WebService
public class DicomService {

	private static Logger logger = Logger.getLogger(DicomService.class);

	@Resource
	private WebServiceContext context;

	/**
	 * @param directionId
	 *            штрих код
	 * @param doctorDirect
	 *            Направивший врач
	 * @param diagnosisDirect
	 *            Диагнозы при направлении
	 * @param servicesDirect
	 *            Услуги при направлении
	 * @param dateDirection
	 *            Дата направления (YYYY-MM-DD)
	 * @param device
	 *            Аппарат
	 * @param datePlanned
	 *            Планируемая дата выполнения исследования (YYYY-MM-DD)
	 * @param directionCode
	 *            Идентификатор случая заболевания
	 * @param directionLocation
	 *            Кабинет
	 * @param patient
	 *            Пациент
	 * @throws DicomWebServiceException
	 * 
	 */
	public long makeDirection(String directionId, Employee doctorDirect,
			Diagnosis[] diagnosisDirect, Service[] servicesDirect,
			String dateDirection, ManufacturerDevice device,
			String dateTimePlanned, String directionCode,
			String directionLocation, Patient patient)
			throws DicomWebServiceException {

		// Проверки
		doctorDirect.chechEntity();
		patient.chechEntity();

		ServletContext servletContext = (ServletContext) context
				.getMessageContext().get(MessageContext.SERVLET_CONTEXT);

		Connection connection;
		
		try {
			
			connection = ORMUtil.getConnection(servletContext);
			PersistentManagerDerby pm = new PersistentManagerDerby(connection);
			Direction drn = new Direction();
			drn.setDirectionId(directionId);
			drn.setDoctorDirect(doctorDirect);
			drn.setDiagnosisDirect(diagnosisDirect);
			drn.setServicesDirect(servicesDirect);
			drn.setDateDirection(dateDirection);
			drn.setDevice(device);
			drn.setDateTimePlanned(dateTimePlanned);
			drn.setDirectionCode(directionCode);
			drn.setDirectionLocation(directionLocation);
			drn.setPatient(patient);
			return pm.makePesistentDirection(drn);

		} catch (SQLException e) {
			throwPortalException("make direction error:", e);
		} catch (DataException e) {
			throwPortalException("make direction error:", e);
		} catch (RuntimeException e) {
			throwPortalException("make direction error:", e);
		}
		return -1;

	}

	/**
	 * @param directionId
	 * @return
	 * @throws DicomWebServiceException
	 */
	public Direction getDirectionBydirectionId(String directionId)
			throws DicomWebServiceException {

		ServletContext servletContext = (ServletContext) context
				.getMessageContext().get(MessageContext.SERVLET_CONTEXT);

		Connection connection;
		try {
			connection = ORMUtil.getConnection(servletContext);
			PersistentManagerDerby pm = new PersistentManagerDerby(connection);
			return (Direction) pm.getDirectionByDirectionId(directionId);

		} catch (SQLException e) {
			throwPortalException("get direction error:", e);
		} catch (DataException e) {
			throwPortalException("get direction error:", e);
		}
		return null;
	}

	/**
	 * @param id
	 * @return
	 * @throws DicomWebServiceException
	 */
	public Direction getDirectionById(long id) throws DicomWebServiceException {

		ServletContext servletContext = (ServletContext) context
				.getMessageContext().get(MessageContext.SERVLET_CONTEXT);

		Connection connection;
		try {
			connection = ORMUtil.getConnection(servletContext);
			PersistentManagerDerby pm = new PersistentManagerDerby(connection);
			return (Direction) pm.getDirectionByID(id);

		} catch (SQLException e) {
			throwPortalException("get direction error:", e);
		} catch (DataException e) {
			throwPortalException("get direction error:", e);
		}
		return null;

	}

	/**
	 * Поиск Исследований
	 * 
	 * @param query
	 * @return
	 * @throws DicomWebServiceException
	 */
	@WebMethod(operationName = "queryStudy")
	@WebResult(targetNamespace = "")
	@RequestWrapper(localName = "queryStudyReq", targetNamespace = "http://webservice.dicom.psystems.org")
	@ResponseWrapper(localName = "queryStudyResp", targetNamespace = "http://webservice.dicom.psystems.org")
	public Study[] queryStudy(@WebParam QueryStudy query)
			throws DicomWebServiceException {

		ServletContext servletContext = (ServletContext) context
				.getMessageContext().get(MessageContext.SERVLET_CONTEXT);

		query.chechEntity();

		Connection connection;
		try {

			connection = ORMUtil.getConnection(servletContext);
			PersistentManagerDerby pm = new PersistentManagerDerby(connection);
			ArrayList<Study> studies = pm.queryStudies(query);

			// FIXME Сделать путь к ..
			// "http://127.0.0.1:8888/browser/study/"
			String url = servletContext.getContextPath();

			System.out.println("!!!!! url=" + url);

			ArrayList<Study> tmpData = new ArrayList<Study>();

			for (Study study : tmpData) {
				study.setStudyUrl(url + "/" + study.getId());

				// Фильтруем результаты в которых есть отклонения
				// (только для флюшек)
				if (study.getStudyModality() != null
						&& study.getStudyModality().equals("CR")) {
					if (study.getStudyResult() == null
							|| study.getStudyResult().length() == 0) {
						study.setStudyResult("норма");
						tmpData.add(study);
					}
				} else {
					tmpData.add(study);
				}
			}

			return studies.toArray(new Study[studies.size()]);

		} catch (SQLException e) {
			throwPortalException("query study error:", e);
		} catch (DataException e) {
			throwPortalException("query study error:", e);
		}
		return null;
	}

	/**
	 * @param query
	 * @return
	 * @throws DicomWebServiceException
	 */
	@WebMethod(operationName = "queryDirection")
	@WebResult(targetNamespace = "")
	@RequestWrapper(localName = "queryDirectionReq", targetNamespace = "http://webservice.dicom.psystems.org")
	@ResponseWrapper(localName = "queryDirectionResp", targetNamespace = "http://webservice.dicom.psystems.org")
	public Direction[] queryDirection(QueryDirection query)
			throws DicomWebServiceException {

		query.chechEntity();

		ServletContext servletContext = (ServletContext) context
				.getMessageContext().get(MessageContext.SERVLET_CONTEXT);

		Connection connection;
		try {
			connection = ORMUtil.getConnection(servletContext);
			PersistentManagerDerby pm = new PersistentManagerDerby(connection);
			ArrayList<Direction> directions = pm.queryDirections(query);
			return directions.toArray(new Direction[directions.size()]);

		} catch (SQLException e) {
			throwPortalException("query direction error:", e);
		} catch (DataException e) {
			throwPortalException("query direction error:", e);
		}
		return null;
	}

	/**
	 * Печать сообщения об ошибке
	 * 
	 * @param msg
	 * @param e
	 * @throws DicomWebServiceException
	 */
	private void throwPortalException(String msg, Throwable e)
			throws DicomWebServiceException {
		String marker = Thread.currentThread().getId() + "_" + Math.random()
				+ " - " + new Date();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String stack = sw.toString();
		logger.warn("WEBSERVICE ERROR [" + marker + "] " + msg + " " + e
				+ " stack trace:\n" + stack);
		throw new DicomWebServiceException("WEBSERVICE ERROR [" + marker + "] "
				+ msg + " " + e, e);
	}

}
