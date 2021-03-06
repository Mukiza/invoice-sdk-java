package com.sample.invoice;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.paypal.exception.ClientActionRequiredException;
import com.paypal.exception.HttpErrorException;
import com.paypal.exception.InvalidCredentialException;
import com.paypal.exception.InvalidResponseDataException;
import com.paypal.exception.MissingCredentialException;
import com.paypal.exception.SSLConfigurationException;
import com.paypal.sdk.exceptions.OAuthException;
import com.paypal.svcs.services.InvoiceService;
import com.paypal.svcs.types.common.RequestEnvelope;
import com.paypal.svcs.types.pt.GetInvoiceDetailsRequest;
import com.paypal.svcs.types.pt.GetInvoiceDetailsResponse;

/**
 * Servlet implementation class CreateInvoiceSerlvet
 */
public class GetInvoiceDetailsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetInvoiceDetailsServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		getServletConfig().getServletContext()
				.getRequestDispatcher("/GetInvoiceDetails.jsp")
				.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		session.setAttribute("url", request.getRequestURI());
		RequestEnvelope env = new RequestEnvelope();
		// The code for the language in which errors are returned, which must be en_US.
		env.setErrorLanguage("en_US");
		GetInvoiceDetailsRequest req = new GetInvoiceDetailsRequest();
		req.setRequestEnvelope(env);
		//ID of the invoice. 
		req.setInvoiceID(request.getParameter("invoiceId"));
		try {
			/* 
			 ## Creating service wrapper object
			 Creating service wrapper object to make API call and loading
			 configuration file for your credentials and endpoint
			*/ 
			InvoiceService invoiceSrvc = new InvoiceService(this.getClass().getResourceAsStream("/sdk_config.properties"));
			
			/* AccessToken and TokenSecret for third party authentication.
			   PayPal Permission api provides these tokens.Please refer Permission SDK 
			   at (https://github.com/paypal/permissions-sdk-java). 	
			*/
			if (request.getParameter("accessToken") != null
					&& request.getParameter("tokenSecret") != null) {
				invoiceSrvc.setAccessToken(request.getParameter("accessToken"));
				invoiceSrvc.setTokenSecret(request.getParameter("tokenSecret"));
			}
			response.setContentType("text/html");
			GetInvoiceDetailsResponse resp = invoiceSrvc.getInvoiceDetails(req);
			if (resp != null) {
				session.setAttribute("RESPONSE_OBJECT", resp);
				session.setAttribute("lastReq", invoiceSrvc.getLastRequest());
				session.setAttribute("lastResp", invoiceSrvc.getLastResponse());
				if (resp.getResponseEnvelope().getAck().toString()
						.equalsIgnoreCase("SUCCESS")) {
					Map<Object, Object> map = new LinkedHashMap<Object, Object>();
					/*
					 * common:AckCode Acknowledgement code. It is one of the following 
					 * values:
					    Success � The operation completed successfully.
					    Failure � The operation failed.
					    SuccessWithWarning � The operation completed successfully; however, there is a warning message.
					    FailureWithWarning � The operation failed with a warning message.
					 */
					map.put("Ack", resp.getResponseEnvelope().getAck());
					
					//Account that created the invoice. 
					map.put("Created By", resp.getInvoiceDetails().getCreatedBy());
					if(resp.getPaymentDetails() != null) {
						//Returns True if the invoice was paid by PayPal. 
						map.put("ViaPayPal", resp.getPaymentDetails().getViaPayPal());
					}
					
					//URL location where merchants view the invoice details. 
					map.put("Invoice URL", resp.getInvoiceURL());
					session.setAttribute("map", map);
					response.sendRedirect("Response.jsp");
				} else {
					session.setAttribute("Error", resp.getError());
					response.sendRedirect("Error.jsp");
				}
			}

		} catch (SSLConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidCredentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HttpErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResponseDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientActionRequiredException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MissingCredentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
