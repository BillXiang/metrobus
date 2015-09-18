package com.bill.metrobus.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.bill.metrobus.entity.BusUserManager;

/**
 * Servlet implementation class MyServlet
 */
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String content = request.getParameter("content");
		System.out.println(content);
		String user = request.getParameter("user");
		JSONObject jsonObject = null;
		try{
			jsonObject = JSONObject.fromObject(content);
		}catch(Exception e){
			e.printStackTrace();
		}
		if (jsonObject != null && jsonObject.containsKey("action")) {
			String busId = null;
			String action = jsonObject.getString("action");
			if("getOn".equals(action)){// 用户上车
				busId = jsonObject.getString("busId");
				BusUserManager.getInstance().getOn(busId, user);
			}else if("getOff".equals(action)){// 用户下车
				busId = jsonObject.getString("busId");
				BusUserManager.getInstance().getOff(busId, user);
			}else if("requestBusPosition".equals(action)){// 用户向服务器请求指定公交位置信息
				busId = jsonObject.getString("busId");
				BusUserManager.getInstance().requestBusPosition(busId, user);
			}else if("updateBusPosition".equals(action)){// 用户向服务器提交公交位置信息
				busId = jsonObject.getString("busId");
				int latitudeE6 = jsonObject.getInt("latitudeE6");
				int longitudeE6 = jsonObject.getInt("longitudeE6");
				String remoteUser = jsonObject.getString("user");
				BusUserManager.getInstance().updateBusPosition(busId,
						latitudeE6, longitudeE6, remoteUser);
			}else if("getOnLineUsers".equals(action)){// 请求所有在线用户
				JSONArray jsonArray = new JSONArray();
				List<String> users = BusUserManager.getInstance().getAllUser();
				if(users!=null){
					for(int i=0; i<users.size(); i++){
						JSONObject userObject = new JSONObject();
						userObject.put("alias", users.get(i));
						jsonArray.add(userObject);
					}
					PrintWriter out = response.getWriter();
					out.print(jsonArray.toString());
					out.flush();
					out.close();
				}
			}else{
				System.out.println(content);
			}
		} else {
			System.out.println(content);
		}
	}
}
