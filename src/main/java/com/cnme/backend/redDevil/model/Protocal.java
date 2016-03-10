package com.cnme.backend.redDevil.model;

import java.util.HashMap;

import javax.servlet.ServletContext;

public class Protocal {
	private static ServletContext configuration;
	
	public static ServletContext getConfiguration() {
		return configuration;
	}

	public static final HashMap<String, String> KV_Data = new HashMap<String, String>();
	
	public static enum Redis_Key {FirstTenDeployments,ClientList,ClientServers,ClientContacts,ProductInfo,ProductVersion};

	public static DBSchema DBS_UserFile;
	public static DBSchema DBS_QueryUserCount;
	public static DBSchema DBS_Login;
	public static DBSchema DBS_SearchUser;
	public static DBSchema DBS_Deployment;
	public static DBSchema DBS_QueryClients;
	public static DBSchema DBS_QueryClientServers;
	public static DBSchema DBS_QueryClientContacts;
	public static DBSchema DBS_QueryProductList;
	public static DBSchema DBS_QueryProductVersion;
	public static DBSchema DBS_ProductMaster;
	public static DBSchema DBS_ProductMasterUpdate;
	public static DBSchema DBS_QueryProductCount;
	public static DBSchema DBS_QueryDeployments;
	public static DBSchema DBS_QueryOneDeployment;
	public static DBSchema DBS_SearchDeploymentByDeployID;
	public static DBSchema DBS_SearchDeploymentByClient;
	public static DBSchema DBS_loadClientServerProducts;

	public static final String ACTION_LOGIN = "1";
	public static final String ACTION_SYNC = "2";
	public static final String ACTION_DEPLOY = "3";
	public static final String ACTION_LOAD_CLIENTLIST = "4";
	public static final String ACTION_LOAD_CLIENTSERVER = "5";
	public static final String ACTION_LOAD_CLIENTCONTACT = "6";
	public static final String ACTION_LOAD_CLIENTSERVERPRODUCT = "7";
	public static final String ACTION_LOAD_PRODUCTLIST = "8";
	public static final String ACTION_QUERY_PRODUCTVERSION = "9";
	public static final String ACTION_LOAD_CLIENTSERVERPRODUCTS = "10";

	public static final String STATUS_NEW = "new";
	public static final String STATUS_RECEIVED = "received";
	public static final String STATUS_REPLIED = "replied";
	public static final String STATUS_CONFIRMED = "confirmed";
	public static final String STATUS_ERROR = "error";

	public static final String KEY_MSGID = "msgID";
	public static final String KEY_STATUS = "status";
	public static final String KEY_ACTION = "action";
	public static final String KEY_BODY = "body";

	public static final String SEPERATOR1 = ",";

	public static void initial(ServletContext conf) {
		configuration = conf;
		// userfile
		KV_Data.put("U1", "UserName");
		// KV_Data.put("U2", "ChineseName");
		KV_Data.put("U3", "Password");
		KV_Data.put("U4", "PasswordHash");
		KV_Data.put("U5", "Salt");
		KV_Data.put("U6", "Email");
		KV_Data.put("U7", "SuperUser");
		KV_Data.put("U8", "UserID");
		KV_Data.put("U9", "Active");
		KV_Data.put("U10", "IsSupport");
		KV_Data.put("U11", "RegisterResult");
		// deploy
		KV_Data.put("D1", "DeployID");
		KV_Data.put("D2", "Client");
		KV_Data.put("D3", "Server");
		KV_Data.put("D4", "Contact");
		KV_Data.put("D5", "Priority");
		KV_Data.put("D6", "RequestType");
		KV_Data.put("D7", "Environment");
		KV_Data.put("D8", "Description");
		KV_Data.put("D9", "Product");
		KV_Data.put("D10", "Version");
		KV_Data.put("D11", "Details");
		KV_Data.put("D12", "Category");
		KV_Data.put("D13", "DeployedBy");
		KV_Data.put("D14", "DeployTime");
		KV_Data.put("D15", "Remark");
		// http
//		KV_Data.put("D16", "SearchKeyword");
//		KV_Data.put("D17", "LoadPageNo");
//		KV_Data.put("D18", "ClientList");
//		KV_Data.put("D19", "ClientServers");
//		KV_Data.put("D20", "ClientContacts");
//		KV_Data.put("D21", "ProductList");
//		KV_Data.put("D22", "DeploymentList");
//		KV_Data.put("D23", "ServerDeployedDetails");
//		KV_Data.put("D44", "ErrorCode");

		// Count
		KV_Data.put("C", "count(*)");

		DBS_UserFile = new DBSchema.Builder("WebUserFile", new String[] { "U1",
				"U3", "U4", "U5", "U6", "U7", "U8", "U9", "U10" },
				new String[] { "U1" }, false).build();

		DBS_Login = new DBSchema.Builder("WebUserFile", null,
				new String[] { "U1" }, false).QuerySets(
				new String[] { "U4", "U5" }).build();

		DBS_QueryUserCount = new DBSchema.Builder("WebUserFile",
				new String[] { "U1" }, new String[] { "U1" }, false).QuerySets(
				new String[] { "C" }).build();

		DBS_Deployment = new DBSchema.Builder("Deployment", new String[] {
				"D1", "D2", "D3", "D4", "D5", "D6", "D7", "D11", "D8", "D13",
				"D14" }, new String[] { "D1" }, false).build();

		DBS_QueryClients = new DBSchema.Builder("ClientList", null, null, false)
				.QuerySets(new String[] { "D2" }).build();

		DBS_QueryClientServers = new DBSchema.Builder("ClientServer", null,
				new String[] { "D2" }, false).QuerySets(new String[] { "D3" })
				.build();

		DBS_QueryClientContacts = new DBSchema.Builder("ClientContact", null,
				new String[] { "D2" }, false).QuerySets(new String[] { "D4" })
				.build();

		DBS_QueryProductList = new DBSchema.Builder("Product", null, null,
				false).QuerySets(new String[] { "D9" }).build();

		DBS_QueryProductVersion = new DBSchema.Builder("ProductMaster", null,
				new String[] { "D2", "D3", "D9" }, false).QuerySets(
				new String[] { "D2", "D3", "D9", "D10" }).build();

		DBS_ProductMaster = new DBSchema.Builder("ProductMaster", new String[] {
				"D2", "D3", "D12", "D9", "D10" }, new String[] { "D2", "D3",
				"D9" }, false).build();

		DBS_ProductMasterUpdate = new DBSchema.Builder("ProductMaster_Update",
				new String[] { "D2", "D3", "D12", "D9", "D10", "D14", "D13",
						"D15" }, new String[] { "D2", "D3", "D9", "D10" },
				false).build();

		DBS_QueryProductCount = new DBSchema.Builder("ProductMaster", null,
				new String[] { "D2", "D3", "D9" }, false).QuerySets(
				new String[] { "C" }).build();

		DBS_QueryDeployments = new DBSchema.Builder("Deployment", null, null,
				false).QuerySets(
				new String[] { "D1", "D2", "D13", "D14", "D8" }).build();

		DBS_QueryOneDeployment = new DBSchema.Builder("Deployment", null,
				new String[] { "D1" }, false).QuerySets(
				new String[] { "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D11",
						"D8", "D13", "D14" }).build();

		DBS_SearchDeploymentByDeployID = new DBSchema.Builder("Deployment",
				null, new String[] { "D1" }, false).QuerySets(
				new String[] { "D1", "D2", "D13", "D14", "D8" }).build();

		DBS_SearchDeploymentByClient = new DBSchema.Builder("Deployment", null,
				new String[] { "D2" }, false).QuerySets(
				new String[] { "D1", "D2", "D13", "D14", "D8" }).build();

		DBS_loadClientServerProducts = new DBSchema.Builder("ProductMaster", null,
				new String[] { "D2", "D3" }, false).QuerySets(
				new String[] { "D9", "D10" }).build();
	}


}
