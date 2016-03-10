package com.cnme.backend.redDevil.web;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.cnme.backend.redDevil.model.CryptoUtils;
import com.cnme.backend.redDevil.model.Log;
import com.cnme.backend.redDevil.model.Protocal;

public class Actions {
	public static void handle(Session session, JSONObject msg) {
		String status = msg.getString(Protocal.KEY_STATUS);
		String action = msg.getString(Protocal.KEY_ACTION);
		switch (status) {
		case Protocal.STATUS_NEW:
			receiveMsg(session, msg);
			break;
		case Protocal.STATUS_RECEIVED:
			switch (action) {
			case Protocal.ACTION_LOGIN:
				checkLogin(session, msg);
				break;
			case Protocal.ACTION_SYNC:
				syncMsgs(session, msg);
				break;
			case Protocal.ACTION_DEPLOY:
				deploy(session, msg);
				break;
			default:
				break;
			}
		case Protocal.STATUS_REPLIED:
			break;
		case Protocal.STATUS_CONFIRMED:
			switch (action) {
			case Protocal.ACTION_SYNC:
				syncMsgsConfirm(session, msg);
				break;
			default:
				confirmMsg(session, msg);
				break;
			}
		default:
			break;
		}
	}

	@SuppressWarnings("unchecked")
	public static boolean WS_send(Session session, String msgID,
			JSONObject inMsg, JSONObject outMsg, String username) {
		try {
			session.getBasicRemote().sendText(outMsg.toString());
			RedisHelper.putMsg(msgID, username, outMsg);
			Log.info(">session " + session.getId() + ":" + outMsg);
			return true;
		} catch (Exception e) {
			// TODO
			Log.exception(e);
			Log.info("Failed>session " + session.getId() + ":" + outMsg);
			inMsg.replace(Protocal.KEY_STATUS, Protocal.STATUS_ERROR);
			RedisHelper.putMsg(msgID, username, inMsg);
			return false;
		}
	}

	public static boolean HTTP_reply(HttpServletResponse response,
			JSONObject returnMsg) {
		try {
			String data = returnMsg.toString();
			Log.info("HTTP_reply>" + data);
			response.setCharacterEncoding("UTF8");
			response.getWriter().write(data);
			return true;
		} catch (IOException e) {
			Log.exception(e);
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public static void receiveMsg(Session session, JSONObject msg) {
		String msgID = msg.getString(Protocal.KEY_MSGID);
		JSONObject returnMsg = new JSONBuilder(msgID,
				msg.getString(Protocal.KEY_ACTION), Protocal.STATUS_RECEIVED,
				"0").getMsg();
		msg.replace(Protocal.KEY_STATUS, Protocal.STATUS_RECEIVED);
		if (!WS_send(session, msgID, msg, returnMsg,
				RedisHelper.getUsername(session))) {
			// TODO
		}
		handle(session, msg);
	}

	public static void confirmMsg(Session session, JSONObject msg) {
		String msgID = msg.getString(Protocal.KEY_MSGID);
		RedisHelper.removeMsg(msgID, RedisHelper.getUsername(session));
	}

	public static void syncMsgs(Session session, JSONObject msg) {
		String msgID = msg.getString(Protocal.KEY_MSGID);
		String username = RedisHelper.getUsername(session);
		JSONObject returnMsg = RedisHelper.getSyncReturnMsg(username, msgID);
		WS_send(session, msgID, msg, returnMsg,
				RedisHelper.getUsername(session));
	}

	public static void syncMsgsConfirm(Session session, JSONObject msg) {
		String msgID = msg.getString(Protocal.KEY_MSGID);
		RedisHelper.confirmSyncMsg(RedisHelper.getUsername(session), msgID);
		if (msg.getString("isKeepSync").toUpperCase().equals("Y")) {
			syncMsgs(session, msg);
		}
	}

	public static void checkLogin(Session session, JSONObject msg) {
		// error code, 1:login successfully,-1: login failed, incorrect
		// username/pwd
		// -2: get mysql statement failed
		String msgID = "", username = "";
		JSONObject returnMsg;
		String errorCode = "1";
		SQLTransaction trans = new SQLTransaction();
		try {
			msgID = msg.getString(Protocal.KEY_MSGID);
			JSONObject msgBody = msg.getJSONObject(Protocal.KEY_BODY);
			username = msgBody.getString("U1");
			String pwd = msgBody.getString("U2");
			String pwdHash = "", salt = "";
			if (trans.inital()) {
				ResultSet result = trans
						.query(ResultSet.TYPE_FORWARD_ONLY,
								ResultSet.CONCUR_READ_ONLY, msgBody,
								Protocal.DBS_Login);
				if (result != null && result.next()) {
					pwdHash = result.getString(1).trim();
					salt = result.getString(2).trim();
				}
				trans.releaseResource();
				if (!CryptoUtils.verify(pwdHash, pwd, salt)) {
					errorCode = "-1";
				}
			} else {
				// can't get DB connection
				errorCode = "-3";
			}
		} catch (SQLException e) {
			trans.releaseResource();
			errorCode = "-2";
			Log.info("login error!");
			Log.exception(e);
		} catch (JSONException e) {
			trans.releaseResource();
			errorCode = "-4";
			Log.info("login error!");
			Log.exception(e);
		}
		returnMsg = new JSONBuilder(msgID, Protocal.ACTION_LOGIN,
				Protocal.STATUS_REPLIED, errorCode).getMsg();
		if (errorCode.equals("1")) {
			RedisHelper.putSession(username, session);
			RedisHelper.loginSucceed(msgID, username, returnMsg);
		} else {
			RedisHelper.loginFailed(msgID);
		}
		WS_send(session, msgID, msg, returnMsg, username);
	}

	public static void register(HttpServletRequest request,
			HttpServletResponse response) {
		String errorCode = "1";
		String username = request.getParameter("U1");
		String pwd = request.getParameter("U3");
		if (pwd.equals("") || username.equals("")) {
			// doesn't input pwd or username
			errorCode = "-1";
		}
		SQLTransaction trans = new SQLTransaction();
		try {
			if (errorCode == "1") {
				if (trans.inital()) {
					ResultSet result = trans.query(ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY, request,
							Protocal.DBS_QueryUserCount);
					int count = -1;
					if (result != null && result.next()) {
						count = result.getInt(1);
					}
					if (count == 0) {
						String salt = CryptoUtils.getSalt();
						String pwdHash = CryptoUtils.getHash(pwd, salt);
						request.setAttribute("U4", pwdHash);
						request.setAttribute("U5", salt);
						request.setAttribute("U7", "N");
						request.setAttribute("U8", "-1");
						request.setAttribute("U9", "N");
						request.setAttribute("U10", "N");
						trans.addRecordBySQL(request, Protocal.DBS_UserFile);
					} else {
						// user exist
						errorCode = "-2";
					}
					trans.releaseResource();
				} else {
					// can't get DB connection
					errorCode = "-4";
				}
			}
		} catch (SQLException e) {
			// SQL error
			trans.releaseResource();
			errorCode = "-3";
			Log.info("register error!");
			Log.exception(e);

		}
		try {
			JSONObject result = new JSONObject();
			result.put("U11", errorCode);
			response.getWriter().write(result.toString());

		} catch (IOException e) {
			Log.exception(e);
		}
	}

	public static void loadClientList(HttpServletRequest request,
			HttpServletResponse response) {
		String errorCode = "1";
		JSONObject returnMsg = new JSONObject();
		SQLTransaction trans = new SQLTransaction();
		try {
			if (trans.inital()) {
				ResultSet result = trans.query(ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY, Protocal.DBS_QueryClients);
				StringBuilder clientList = new StringBuilder();
				while (result != null && result.next()) {
					clientList.append(result.getString(1).trim()).append(
							Protocal.SEPERATOR1);
				}
				trans.releaseResource();
				clientList.delete(clientList.length() - 1, clientList.length());
				returnMsg.put("D18", clientList.toString());
			} else {
				// can't get DB connection
				errorCode = "-1";
			}
		} catch (SQLException e) {
			System.out.println(e);
			trans.releaseResource();
			Log.info("load ClientList failed");
			Log.exception(e);
			errorCode = "-2";
		}
		returnMsg.put("D44", errorCode);
		HTTP_reply(response, returnMsg);
	}

	public static void loadClientServer(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject returnMsg = new JSONObject();
		String errorCode = "1";
		SQLTransaction trans = new SQLTransaction();
		try {
			if (trans.inital()) {
				ResultSet result = trans.query(ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY, request,
						Protocal.DBS_QueryClientServers);
				StringBuilder clientServers = new StringBuilder();
				while (result != null && result.next()) {
					clientServers.append(result.getString(1).trim()).append(
							Protocal.SEPERATOR1);
				}
				trans.releaseResource();
				if (clientServers.length() > 0) {
					clientServers.delete(clientServers.length() - 1,
							clientServers.length());
				}
				returnMsg.put("D19", clientServers.toString());
			} else {
				// can't get DB connection
				errorCode = "-1";
			}
		} catch (NullPointerException e) {
			// request input missed some require data
			trans.releaseResource();
			errorCode = "-2";
			Log.info("load ClientServers failed");
			Log.exception(e);
		} catch (SQLException e) {
			trans.releaseResource();
			errorCode = "-3";
			Log.info("load ClientServers failed");
			Log.exception(e);
		}
		returnMsg.put("D44", errorCode);
		HTTP_reply(response, returnMsg);
	}

	public static void loadClientContact(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject returnMsg = new JSONObject();
		String errorCode = "1";
		SQLTransaction trans = new SQLTransaction();
		try {
			if (trans.inital()) {
				ResultSet result = trans.query(ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY, request,
						Protocal.DBS_QueryClientContacts);
				StringBuilder clientContacts = new StringBuilder();
				while (result != null && result.next()) {
					clientContacts.append(result.getString(1).trim()).append(
							Protocal.SEPERATOR1);
				}
				trans.releaseResource();
				clientContacts.delete(clientContacts.length() - 1,
						clientContacts.length());
				returnMsg.put("D20", clientContacts.toString());
			} else {
				// can't get DB connection
				errorCode = "-1";
			}
		} catch (NullPointerException e) {
			// request input missed some require data
			trans.releaseResource();
			errorCode = "-2";
			Log.info("load ClientContacts failed");
			Log.exception(e);
		} catch (SQLException e) {
			trans.releaseResource();
			errorCode = "-3";
			Log.info("load ClientContacts failed");
			Log.exception(e);
		}
		returnMsg.put("D44", errorCode);
		HTTP_reply(response, returnMsg);
	}

	public static void loadProductList(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject returnMsg = new JSONObject();
		String errorCode = "1";
		SQLTransaction trans = new SQLTransaction();
		try {
			if (trans.inital()) {
				ResultSet result = trans.query(ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY,
						Protocal.DBS_QueryProductList);
				StringBuilder products = new StringBuilder();
				while (result != null && result.next()) {
					products.append(result.getString(1).trim()).append(
							Protocal.SEPERATOR1);
				}
				trans.releaseResource();
				products.delete(products.length() - 1, products.length());
				returnMsg.put("D21", products.toString());
			} else {
				// can't get DB connection
				errorCode = "-1";
			}
		} catch (NullPointerException e) {
			// request input missed some require data
			trans.releaseResource();
			errorCode = "-2";
			Log.info("load ProductList failed");
			Log.exception(e);
		} catch (SQLException e) {
			trans.releaseResource();
			errorCode = "-3";
			Log.info("load ProductList failed");
			Log.exception(e);
		}
		returnMsg.put("D44", errorCode);
		HTTP_reply(response, returnMsg);
	}

	public static void loadProductVersion(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject returnMsg = new JSONObject();
		String errorCode = "1";
		SQLTransaction trans = new SQLTransaction();
		try {
			if (trans.inital()) {
				ResultSet result = trans.query(ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY, request,
						Protocal.DBS_QueryProductVersion);
				if (result != null && result.next()) {
					returnMsg = JSONBodyBuilder.buildBody(result,
							Protocal.DBS_QueryProductVersion);
				} else {
					returnMsg = new JSONObject();
					returnMsg.put("D2", request.getParameter("D2"));
					returnMsg.put("D3", request.getParameter("D3"));
					returnMsg.put("D9", request.getParameter("D9"));
					returnMsg.put("D10", "undeployed");
				}
				trans.releaseResource();
			} else {
				// can't get DB connection
				errorCode = "-1";
			}
		} catch (NullPointerException e) {
			// request input missed some require data
			trans.releaseResource();
			errorCode = "-2";
			Log.info("load ProductVersion failed");
			Log.exception(e);
		} catch (SQLException e) {
			trans.releaseResource();
			errorCode = "-3";
			Log.info("load ProductVersion failed");
			Log.exception(e);
		}
		returnMsg.put("D44", errorCode);
		HTTP_reply(response, returnMsg);
	}

	public static void deploy(Session session, JSONObject msg) {
		JSONObject msgBody = new JSONObject();
		String msgID = "", errorCode = "1";
		SQLTransaction trans = new SQLTransaction();
		try {
			msgBody = msg.getJSONObject(Protocal.KEY_BODY);
			msgID = msg.getString(Protocal.KEY_MSGID);
			String details = msgBody.getString("D11");

			msgBody.put("D1", genDeployID());
			msgBody.put("D13", RedisHelper.getUsername(session));
			msgBody.put("D14", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date()));
			if (trans.inital()) {
				trans.addRecordBySQL(msgBody, Protocal.DBS_Deployment);
				String[] upgrades = details.trim().split(";");
				for (int i = 0; i < upgrades.length; i++) {
					if (upgrades[i].equals("")) {
						continue;
					}
					String[] temp = upgrades[i].trim().split("from");
					String product = temp[0];
					String toVersion = temp[1].trim().split("to")[1];
					msgBody.put("D9", product);
					msgBody.put("D10", toVersion);
					ResultSet rs = trans.query(ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY, msgBody,
							Protocal.DBS_QueryProductCount);
					if (rs != null && rs.next()) {
						// TODO
						msgBody.put("D12", "Server");
						msgBody.put("D15", "through web platform");
						if (rs.getInt(1) > 0) {
							trans.editRecordBySQL(msgBody,
									Protocal.DBS_ProductMaster);
						} else {
							trans.addRecordBySQL(msgBody,
									Protocal.DBS_ProductMaster);
						}
						try {
							trans.addRecordBySQL(msgBody,
									Protocal.DBS_ProductMasterUpdate);
						} catch (Exception e) {
						}
					}
				}
				trans.releaseResource();
			} else {
				// can't get DB connection
				errorCode = "-1";
			}
		} catch (NullPointerException | ArrayIndexOutOfBoundsException
				| JSONException e) {
			// request input missed some require data
			trans.releaseResource();
			errorCode = "-2";
			Log.info("deploy failed");
			Log.exception(e);
		} catch (SQLException e) {
			trans.releaseResource();
			errorCode = "-3";
			Log.info("deploy failed");
			Log.exception(e);
		}
		if (!msgID.equals("")) {
			JSONObject returnMsg = new JSONBuilder(msgID,
					Protocal.ACTION_DEPLOY, Protocal.STATUS_REPLIED, errorCode)
					.body(msgBody).getMsg();

			WS_send(session, msgID, msg, returnMsg,
					RedisHelper.getUsername(session));
		}
	}

	public static String genDeployID() {
		String createTime = new SimpleDateFormat("yyMMddHHmmssSSS")
				.format(new Date());
		String ID = "DP" + createTime;
		return ID;
	}

	public static JSONArray getDeploymentsByRange(int startIndex, int endIndex)
			throws SQLException {
		JSONArray deployments = null;
		SQLTransaction trans = new SQLTransaction();
		int count = endIndex - startIndex + 1;
		String cmd = "select top "
				+ count
				+ "A.DeployID,A.Client,A.DeployedBy,A.DeployTime,A.Description from Deployment as A left join (select top "
				+ startIndex
				+ " DeployID from Deployment) as B on A.DeployID = B.DeployID where B.DeployID is null order by A.DeployID DESC";
		try {
			if (trans.inital()) {
				deployments = new JSONArray();
				ResultSet rs = trans.query(ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY, cmd);
				while (rs.next()) {
					JSONObject deployment = JSONBodyBuilder.buildBody(rs,
							Protocal.DBS_QueryDeployments);
					deployments.add(deployment);
				}
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			trans.releaseResource();
		}
		return deployments;
	}

	public static JSONArray getDeploymentsByPage(int pageNo, int countPerPage)
			throws SQLException {
		int start = (pageNo - 1) * countPerPage;
		int end = pageNo * countPerPage - 1;
		return getDeploymentsByRange(start, end);
	}

	public static void loadDeployments(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject returnMsg = new JSONObject();
		String errorCode = "1";
		try {
			int pageNo = Integer.parseInt(request.getParameter("D17"));
			JSONArray deployments = getDeploymentsByPage(pageNo, 10);
			if (deployments == null) {
				// can't get DB connection
				errorCode = "-1";
			} else {
				returnMsg.put("D22", deployments);
			}
		} catch (NullPointerException e) {
			// request input missed some require data
			errorCode = "-2";
			Log.info("load Deployments failed");
			Log.exception(e);
		} catch (SQLException e) {
			errorCode = "-3";
			Log.info("load Deployments failed");
			Log.exception(e);
		}
		returnMsg.put("D44", errorCode);
		HTTP_reply(response, returnMsg);
	}

	public static void loadOneDeployment(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject returnMsg = new JSONObject();
		String errorCode = "1";
		try {
			SQLTransaction trans = new SQLTransaction();
			if (trans.inital()) {
				ResultSet rs = trans.query(ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY, request,
						Protocal.DBS_QueryOneDeployment);
				while (rs.next()) {
					returnMsg = JSONBodyBuilder.buildBody(rs,
							Protocal.DBS_QueryOneDeployment);
				}
				trans.releaseResource();
			} else {
				// can't get DB connection
				errorCode = "-1";
			}
		} catch (NullPointerException e) {
			// request input missed some require data
			errorCode = "-2";
			Log.info("load Deployment failed");
			Log.exception(e);
		} catch (SQLException e) {
			errorCode = "-3";
			Log.info("load Deployment failed");
			Log.exception(e);
		}
		returnMsg.put("D44", errorCode);
		HTTP_reply(response, returnMsg);
	}

	public static void searchDeployments(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject returnMsg = new JSONObject();
		String errorCode = "1";
		SQLTransaction trans = new SQLTransaction();
		try {
			JSONArray deployments = new JSONArray();
			if (trans.inital()) {
				ResultSet rs = trans.query(ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY, request,
						Protocal.DBS_SearchDeploymentByDeployID);
				if (!rs.next()) {
					rs = trans.query(ResultSet.TYPE_FORWARD_ONLY,
							ResultSet.CONCUR_READ_ONLY, request,
							Protocal.DBS_SearchDeploymentByClient);
					while (rs.next()) {
						JSONObject deployment = JSONBodyBuilder.buildBody(rs,
								Protocal.DBS_QueryDeployments);
						deployments.add(deployment);
					}
				} else {
					do {
						JSONObject deployment = JSONBodyBuilder.buildBody(rs,
								Protocal.DBS_QueryDeployments);
						deployments.add(deployment);
					} while (rs.next());
				}
				trans.releaseResource();
				returnMsg.put("D22", deployments);
			} else {
				// can't get DB connection
				errorCode = "-1";
			}
		} catch (NullPointerException e) {
			// request input missed some require data
			trans.releaseResource();
			errorCode = "-2";
			Log.info("search Deployments failed");
			Log.exception(e);
		} catch (SQLException e) {
			trans.releaseResource();
			errorCode = "-3";
			Log.info("search Deployments failed");
			Log.exception(e);
		}
		returnMsg.put("D44", errorCode);
		HTTP_reply(response, returnMsg);
	}

	public static void loadProductMaster(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject returnMsg = new JSONObject();
		String errorCode = "1";
		SQLTransaction trans = new SQLTransaction();
		try {
			JSONArray items = new JSONArray();
			if (trans.inital()) {
				ResultSet rs = trans.query(ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY, request,
						Protocal.DBS_loadClientServerProducts);
				while (rs.next()) {
					JSONObject item = JSONBodyBuilder.buildBody(rs,
							Protocal.DBS_loadClientServerProducts);
					items.add(item);
				}
				trans.releaseResource();
				returnMsg.put("D23", items);
			} else {
				// can't get DB connection
				errorCode = "-1";
			}
		} catch (NullPointerException e) {
			// request input missed some require data
			trans.releaseResource();
			errorCode = "-2";
			Log.info("load server depolyed detail failed");
			Log.exception(e);
		} catch (SQLException e) {
			trans.releaseResource();
			errorCode = "-3";
			Log.info("load server depolyed detail failed");
			Log.exception(e);
		}
		returnMsg.put("D44", errorCode);
		HTTP_reply(response, returnMsg);
	}
}
