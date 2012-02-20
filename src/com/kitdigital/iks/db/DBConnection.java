package com.kitdigital.iks.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdom.*;
import org.jdom.output.XMLOutputter;

import com.mysql.jdbc.Statement;

public class DBConnection {
Connection mysqlCon = null;
	
	public DBConnection(String type)
	{
		if (type.equals("mysql"))
		{
			try {	
	               String userName = "";
	               String password = "";
	               String url = "";
	               Class.forName ("com.mysql.jdbc.Driver").newInstance ();
	               mysqlCon = DriverManager.getConnection (url, userName, password);
	               System.out.println ("Database connection established");

			      if(!mysqlCon.isClosed())
			        System.out.println("Successfully connected to " +
			          "MySQL server using TCP/IP...");

			    } catch(Exception e) {
			      System.err.println("Exception: " + e.getMessage());
			    } 
			  }
		else
			if (type.equals("postgre"))
			{
				
			}
	}
	
	public void CloseDb()
	{
	      try {
	        if(mysqlCon != null)
	          mysqlCon.close();
	      } catch(SQLException e) {}
	    }

	public String createVideo(String videoname, String videourl) throws SQLException {
	
		PreparedStatement preparedStatement = mysqlCon.prepareStatement("SELECT * FROM `IKSvideotag`.`video` WHERE `url`=?");
		preparedStatement.setString(1,videourl);
		ResultSet dataSet = preparedStatement.executeQuery();
		String videoid = "";
		if (dataSet.next())
		{
			videoid = dataSet.getString(1);
			preparedStatement.close();
			preparedStatement = mysqlCon.prepareStatement("UPDATE `IKSvideotag`.`video` SET `name`=? WHERE `url`=?");
			preparedStatement.setString(1,videoname);
			preparedStatement.setString(2,videourl);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		}
		else
		{
			preparedStatement.close();
			preparedStatement = mysqlCon.prepareStatement("INSERT INTO `IKSvideotag`.`video` (`name`,`url`) VALUES (?, ?)",Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1,videoid);
			preparedStatement.setString(2,videourl);
			preparedStatement.executeUpdate();
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if (rs.next()){
				videoid = String.valueOf(rs.getInt(1));
			}
			preparedStatement.close();
		}
		return videoid;
		
	}
	
	public String getpluginId(String name) throws SQLException {
		String id = "";
		PreparedStatement preparedStatement = mysqlCon.prepareStatement("SELECT idplugin FROM IKSvideotag.plugin WHERE name=?");
		preparedStatement.setString(1,name);
		ResultSet dataSet = preparedStatement.executeQuery();
		if (dataSet.next()){
			id = String.valueOf(dataSet.getString(1));
		}
		dataSet.close();
		preparedStatement.close();
		return id;
	}
	
	public String getMetadataMaxRow() throws SQLException {
		String row = "";
		PreparedStatement preparedStatement = mysqlCon.prepareStatement("SELECT MAX(row) FROM IKSvideotag.metadata");
		ResultSet dataSet = preparedStatement.executeQuery();
		if (dataSet.next()){
			row = String.valueOf(dataSet.getString(1));
		}
		dataSet.close();
		preparedStatement.close();
		int max = Integer.parseInt(row)+1;
		return String.valueOf(max);
	}

	public String deleteVideo(String id) throws SQLException {
		PreparedStatement preparedStatement = mysqlCon.prepareStatement("DELETE FROM `IKSvideotag`.`video` WHERE `id`=?");
		preparedStatement.setString(1,id);
		preparedStatement.executeUpdate();
		preparedStatement = mysqlCon.prepareStatement("DELETE FROM `IKSvideotag`.`metadata` WHERE `idvideo`=?");
		preparedStatement.setString(1,id);
		preparedStatement.executeUpdate();
		preparedStatement.close();
		return "ok";
	}

	public String addVideoTag(String videoid, String tagname, String tagvalue, int idplugin, String row) throws SQLException {
		PreparedStatement preparedStatement = mysqlCon.prepareStatement("INSERT INTO `IKSvideotag`.`metadata` (`idmetadata`,`idvideo`, `tagname`, `tagvalue`, `idplugin`, `row`) VALUES (default, ?, ?, ?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
		preparedStatement.setString(1,videoid);
		preparedStatement.setString(2,tagname);
		preparedStatement.setString(3,tagvalue);
		preparedStatement.setInt(4,idplugin);
		preparedStatement.setString(5,row);
		preparedStatement.executeUpdate();
		String toreturn = "";
		ResultSet rs = preparedStatement.getGeneratedKeys();
		if (rs.next()){
			toreturn = String.valueOf(rs.getInt(1));
		}
		rs.close();
		preparedStatement.close();
		return toreturn;
	}

	public String removeVideoTag(String videoid, String tagid) throws SQLException {
		PreparedStatement preparedStatement = mysqlCon.prepareStatement("DELETE FROM `IKSvideotag`.`metadata` WHERE (`idmetadata`=? AND `idvideo`=?)");
		preparedStatement.setString(1,videoid);
		preparedStatement.setString(2,tagid);
		preparedStatement.executeUpdate();
		preparedStatement.close();
		return "ok";
	}
	
	public String removeVideoTags(String videoid) throws SQLException {
		PreparedStatement preparedStatement = mysqlCon.prepareStatement("DELETE FROM `IKSvideotag`.`metadata` WHERE (`idvideo`=?)");
		preparedStatement.setString(1,videoid);
		preparedStatement.executeUpdate();
		preparedStatement.close();
		return "ok";
	}

	public String updateVideoTag(String videoid, String tagid, String tagname,String tagvalue, int idplugin) throws SQLException {
		PreparedStatement preparedStatement = mysqlCon.prepareStatement("UPDATE `IKSvideotag`.`metadata` SET `tagname`=?, `tagvalue`=?, `idplugin` = ? WHERE (`idmetadata`= ? AND `idvideo`= ?)");
		preparedStatement.setString(1,tagname);
		preparedStatement.setString(2,tagvalue);
		preparedStatement.setInt(3,idplugin);
		preparedStatement.setString(4,tagid);
		preparedStatement.setString(5,videoid);
		preparedStatement.executeUpdate();
		preparedStatement.close();
		return "ok";
	}

	public String createXml(String videoid) throws SQLException {
		// TODO Auto-generated method stub
		
		Element rootElement = new Element("popcorn"); 
//		Document document = new Document(rootElement);
		Element timeline = new Element("timeline"); 
		rootElement.addContent(timeline);
		Element resources = new Element("resources"); 
		timeline.addContent(resources);
		
		String row = "";
//		String xml = "";
		PreparedStatement preparedStatement = mysqlCon.prepareStatement("SELECT DISTINCT `row` FROM `IKSvideotag`.`metadata` WHERE `idvideo`=?");
		preparedStatement.setString(1,videoid);
		ResultSet resultSet = preparedStatement.executeQuery();
		//writeResultSet(resultSet);
		while (resultSet.next()) {
			row = resultSet.getString("row");
			PreparedStatement getdata = mysqlCon.prepareStatement("SELECT `IKSvideotag`.`metadata`.*, `IKSvideotag`.`plugin`.`name` FROM `IKSvideotag`.`metadata`, `IKSvideotag`.`plugin` WHERE (`IKSvideotag`.`metadata`.`row` = ? AND `IKSvideotag`.`plugin`.`idplugin` = `IKSvideotag`.`metadata`.`idplugin`)");
			getdata.setString(1,row);
			ResultSet dataSet = getdata.executeQuery();
			Element pluginElement = null;
			while (dataSet.next()){
				if (pluginElement==null)
					pluginElement = new Element(dataSet.getString("name"));
				pluginElement.setAttribute(dataSet.getString("tagname"), dataSet.getString("tagvalue"));
			}
			resources.addContent(pluginElement);
			pluginElement = null;
		}
		resultSet.close();
		preparedStatement.close();
		XMLOutputter outputter = new XMLOutputter();
		return outputter.outputString(rootElement);
	}
}