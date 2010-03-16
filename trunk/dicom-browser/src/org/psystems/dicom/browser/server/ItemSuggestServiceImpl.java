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
    
    The Original Code is part of WEB-DICOM, an implementation hosted at 
    <http://code.google.com/p/web-dicom/>
    
    In the project WEB-DICOM used the library open source project dcm4che
    The Original Code is part of dcm4che, an implementation of DICOM(TM) in
    Java(TM), hosted at http://sourceforge.net/projects/dcm4che.
    
    =======================================================================
    
    WEB-DICOM - Сохранение и предоставление информации с DICOM устройств

    Copyright (C) 2009-2010 psystems.org 
    Copyright (C) 2009-2010 Dmitry Derenok 

    Это программа является свободным программным обеспечением. Вы можете 
    распространять и/или модифицировать её согласно условиям Стандартной 
    Общественной Лицензии GNU, опубликованной Фондом Свободного Программного 
    Обеспечения, версии 3 или, по Вашему желанию, любой более поздней версии. 
    Эта программа распространяется в надежде, что она будет полезной, но
    БЕЗ ВСЯКИХ ГАРАНТИЙ, в том числе подразумеваемых гарантий ТОВАРНОГО СОСТОЯНИЯ ПРИ 
    ПРОДАЖЕ и ГОДНОСТИ ДЛЯ ОПРЕДЕЛЁННОГО ПРИМЕНЕНИЯ. Смотрите Стандартную 
    Общественную Лицензию GNU для получения дополнительной информации. 
    Вы должны были получить копию Стандартной Общественной Лицензии GNU вместе 
    с программой. В случае её отсутствия, посмотрите <http://www.gnu.org/licenses/>
    Русский перевод <http://code.google.com/p/gpl3rus/wiki/LatestRelease>
    
    Оригинальный исходный код WEB-DICOM можно получить на
    <http://code.google.com/p/web-dicom/>
    
    В проекте WEB-DICOM использованы библиотеки открытого проекта dcm4che/
    Оригинальный исходный код проекта dcm4che, и его имплементация DICOM(TM) in
    Java(TM), находится здесь http://sourceforge.net/projects/dcm4che.
    
    
 */
package org.psystems.dicom.browser.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.psystems.dicom.browser.client.ItemSuggestion;
import org.psystems.dicom.browser.client.exception.DefaultGWTRPCException;
import org.psystems.dicom.browser.client.exception.VersionGWTRPCException;
import org.psystems.dicom.browser.client.proxy.DcmFileProxy;
import org.psystems.dicom.browser.client.proxy.SuggestTransactedResponse;
import org.psystems.dicom.browser.client.service.ItemSuggestService;

import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ItemSuggestServiceImpl extends RemoteServiceServlet implements
		ItemSuggestService {

	private static Logger logger = Logger
			.getLogger(ItemSuggestServiceImpl.class.getName());

	public SuggestTransactedResponse getSuggestions(long transactionId,
			String version, SuggestOracle.Request req) throws DefaultGWTRPCException {
		SuggestTransactedResponse resp = new SuggestTransactedResponse();
		resp.setTransactionId(transactionId);

//		System.out.println("BEGIN SLEEP");
//		try { //TODO Убрать!!!
//			Thread.sleep(5*1000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		System.out.println("END SLEEP");
		
		// проверка версии клиента
		if (!org.psystems.dicom.browser.server.Util.checkClentVersion(version)) {
			throw new VersionGWTRPCException(
					"Версия клиента не совпадает с версией сервера! " + version
							+ " != "
							+ org.psystems.dicom.browser.server.Util.version);
		}
		// Create a list to hold our suggestions (pre-set the lengthto the limit
		// specified by the request)

		int limit = req.getLimit();
		String queryStr = req.getQuery();

		// System.out.println("!!! queryStr=["+queryStr+"]");
		List<Suggestion> suggestions = new ArrayList<Suggestion>(req.getLimit());

		// Replace the code below with something to create and popular
		// ItemSuggestion objects
		// This is where all your magic should happen to find matches and create
		// the suggestion list, usually from a database
		// The dummy code below creates bogus suggestions "Suggestion1",
		// "Suggestion 2", etc..

		// System.out.println("!!!!! " + req.getQuery());

		// for (int i = 1; i < 11; i++) {
		// suggestions.add(new ItemSuggestion("Suggestion " + i, "Suggestion "
		// + i));
		// }
		//		

		PreparedStatement psSelect = null;

		try {

			Connection connection = org.psystems.dicom.browser.server.Util
					.getConnection(getServletContext());
			//

			psSelect = connection
					.prepareStatement("SELECT ID, DCM_FILE_NAME, PATIENT_NAME, PATIENT_BIRTH_DATE, "
							+ " STUDY_DATE FROM WEBDICOM.DCMFILE "
							+ "WHERE UPPER(PATIENT_NAME) like UPPER(? || '%')"
							+ " order by PATIENT_NAME ");

			psSelect.setString(1, queryStr);
			ResultSet rs = psSelect.executeQuery();
			ArrayList<DcmFileProxy> data = new ArrayList<DcmFileProxy>();
			int index = 0;
			suggestions
					.add(new ItemSuggestion(queryStr + "...", queryStr + "%"));
			while (rs.next()) {

				String name = rs.getString("PATIENT_NAME");
				String date = "" + rs.getDate("PATIENT_BIRTH_DATE");
				suggestions.add(new ItemSuggestion(name + " [" + date + "]",
						name));

				if (index++ > limit) {
					break;
				}

			}
			rs.close();

		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
			throw new DefaultGWTRPCException(e.getMessage());
		} finally {

			try {
				if (psSelect != null)
					psSelect.close();
			} catch (SQLException e) {
				logger.error(e);
				// throw new DefaultGWTRPCException(e.getMessage());
			}
		}

		// Now set the suggestions in the response
		resp.setSuggestions(suggestions);

		// Send the response back to the client
		return resp;
	}

}