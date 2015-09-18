package com.bill.metrobus.listener;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Application Lifecycle Listener implementation class SysListener
 */
@WebListener
public class SysListener implements ServletContextListener,
		ServletContextAttributeListener, HttpSessionListener,
		HttpSessionAttributeListener, HttpSessionActivationListener,
		HttpSessionBindingListener, ServletRequestListener,
		ServletRequestAttributeListener {

	public static String DEVELOPING = "false";

	public static String IP = null;

	public static String VERSION = null;

	public static String REAL_ROOT_PATH = null;
	
	public static String COMPANY_NAME = "";

	/**
	 * Default constructor.
	 */
	public SysListener() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see ServletRequestListener#requestDestroyed(ServletRequestEvent)
	 */
	public void requestDestroyed(ServletRequestEvent arg0) {
		// TODO Auto-generated method stub
		//java.lang.IllegalStateException: Cannot create a session after the response has been committed
//		ServletContext servletContext = arg0.getServletContext();
//		
//		HttpServletRequest httpServletRequest = (HttpServletRequest)arg0.getServletRequest();
//		
//		String requestURI = httpServletRequest.getRequestURI();
//		HttpSession session = httpServletRequest.getSession();
//		StaffInfo staffInfo = (StaffInfo)session.getAttribute("staffInfo");
//		if(staffInfo!=null){
//			servletContext.log("科室:"+staffInfo.getOfficeInfo().getOfficeName());
//			servletContext.log(" 人员:"+staffInfo.getName());
//		}
//		servletContext.log("Access:"+requestURI+" Addr:"+httpServletRequest.getRemoteAddr()
//				+" Host:"+httpServletRequest.getRemoteHost()
//				+" port:"+httpServletRequest.getRemotePort());
		
//		servletContext.log("requestDestroyed");
//		Runtime lRuntime = Runtime.getRuntime();
//		servletContext.log("*** BEGIN MEMORY STATISTICS ***");
//		servletContext.log("CurrentTimeMillis RequestURI FreeMemory MaxMemory TotalMemory");
//		servletContext.log("performance "+System.currentTimeMillis()+" "+requestURI + " " +
//				lRuntime.freeMemory() + " " + lRuntime.maxMemory() + " "
//						+ lRuntime.totalMemory());
//		servletContext.log("*** END MEMORY STATISTICS ***");
	}

	/**
	 * @see HttpSessionAttributeListener#attributeAdded(HttpSessionBindingEvent)
	 */
	public void attributeAdded(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
		// System.out.println("attributeAdded");
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		ServletContext servletContext = arg0.getServletContext();
		REAL_ROOT_PATH = servletContext.getRealPath("/");
		servletContext.log(REAL_ROOT_PATH);
		DEVELOPING = servletContext.getInitParameter("developing");
		IP = servletContext.getInitParameter("ip");
		VERSION = servletContext.getInitParameter("version");
		COMPANY_NAME = servletContext.getInitParameter("companyName");
		servletContext.log("DEVELOPING:" + DEVELOPING);
		servletContext.log("IP:" + IP);
		servletContext.log("VERSION:" + VERSION);

	}

	/**
	 * @see HttpSessionActivationListener#sessionDidActivate(HttpSessionEvent)
	 */
	public void sessionDidActivate(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
		// System.out.println("sessionDidActivate");
	}

	/**
	 * @see HttpSessionBindingListener#valueBound(HttpSessionBindingEvent)
	 */
	public void valueBound(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
		// System.out.println("valueBound");
	}

	/**
	 * @see ServletContextAttributeListener#attributeAdded(ServletContextAttributeEvent)
	 */
	public void attributeAdded(ServletContextAttributeEvent arg0) {
		// TODO Auto-generated method stub
		// System.out.println("attributeAdded");
	}

	/**
	 * @see ServletContextAttributeListener#attributeRemoved(ServletContextAttributeEvent)
	 */
	public void attributeRemoved(ServletContextAttributeEvent arg0) {
		// TODO Auto-generated method stub
		// System.out.println("attributeRemoved");
	}

	/**
	 * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
		// System.out.println("sessionDestroyed");
	}

	/**
	 * @see HttpSessionAttributeListener#attributeRemoved(HttpSessionBindingEvent)
	 */
	public void attributeRemoved(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
		// System.out.println("attributeRemoved");
	}

	/**
	 * @see ServletRequestAttributeListener#attributeAdded(ServletRequestAttributeEvent)
	 */
	public void attributeAdded(ServletRequestAttributeEvent arg0) {
		// TODO Auto-generated method stub
		// System.out.println("attributeAdded");
	}

	/**
	 * @see HttpSessionBindingListener#valueUnbound(HttpSessionBindingEvent)
	 */
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
		// System.out.println("valueUnbound");
	}

	/**
	 * @see HttpSessionActivationListener#sessionWillPassivate(HttpSessionEvent)
	 */
	public void sessionWillPassivate(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
		// System.out.println("sessionWillPassivate");
	}

	/**
	 * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpSessionAttributeListener#attributeReplaced(HttpSessionBindingEvent)
	 */
	public void attributeReplaced(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
		// System.out.println("attributeReplaced");
	}

	/**
	 * @see ServletContextAttributeListener#attributeReplaced(ServletContextAttributeEvent)
	 */
	public void attributeReplaced(ServletContextAttributeEvent arg0) {
		// TODO Auto-generated method stub
		// System.out.println("attributeReplaced");
	}

	/**
	 * @see ServletRequestAttributeListener#attributeRemoved(ServletRequestAttributeEvent)
	 */
	public void attributeRemoved(ServletRequestAttributeEvent arg0) {
		// TODO Auto-generated method stub
		// System.out.println("attributeRemoved");
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		// System.out.println("contextDestroyed");
	}

	/**
	 * @see ServletRequestAttributeListener#attributeReplaced(ServletRequestAttributeEvent)
	 */
	public void attributeReplaced(ServletRequestAttributeEvent arg0) {
		// TODO Auto-generated method stub
		// System.out.println("attributeReplaced");
	}

	/**
	 * @see ServletRequestListener#requestInitialized(ServletRequestEvent)
	 */
	public void requestInitialized(ServletRequestEvent arg0) {
		
	}
}
