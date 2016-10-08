package org.zhuboss.pc2server.pcproxy.jetty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpContent;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.servlet.DefaultServlet;

public class MyDefaultServlet extends DefaultServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7809633192310005583L;

	
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		 setServer(response);
		super.doGet(request, response);
	}



	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		setServer(response);
		super.doPost(request, response);
	}

	private void setServer(HttpServletResponse response){
		  Response r=(Response)response;
          r.setHeader(HttpHeader.SERVER.asString(), "pc5s");
	}

	
}
