package com.kitdigital.iks.restws;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;


import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.json.JSONWithPadding;
import com.kitdigital.iks.db.DBConnection;

@Path("database")
@Produces("application/x-javascript")
public class PopcornRest {
	
	@Path("/createvideo")
	@GET
	//@Produces( { MediaType.APPLICATION_XML})
	 public JSONWithPadding CreateMetaVideo(@QueryParam("callback") String callback, @QueryParam("name") String name, @QueryParam("url") String url) throws Exception{
		JSONObject jsonNewVideo = new JSONObject();
		DBConnection dbconn = new DBConnection("mysql");
		String id = dbconn.createVideo(name,url);
		dbconn.CloseDb();
		jsonNewVideo.put("id", id);
		return (new JSONWithPadding(jsonNewVideo, callback));
	}
	
	@Path("/deletevideo")
	@GET
	//@Produces( { MediaType.APPLICATION_XML})
	 public JSONWithPadding DeleteMetaVideo(@QueryParam("callback") String callback, @QueryParam("id") String id) throws Exception{
		JSONObject jsonNewVideo = new JSONObject();
		DBConnection dbconn = new DBConnection("mysql");
		String msg = dbconn.deleteVideo(id);
		System.out.println("Cancello il video = " + id);
		dbconn.CloseDb();
		jsonNewVideo.put("message", msg);
		return (new JSONWithPadding(jsonNewVideo, callback));
	}
	
	@Path("/addtag")
	@GET
	//@Produces( { MediaType.APPLICATION_XML})
	 public JSONWithPadding AddVideoTag(@QueryParam("callback") String callback, @QueryParam("videoid") String videoid, @QueryParam("tagname") String tagname, @QueryParam("tagvalue") String tagvalue, @QueryParam("idplugin") Integer idplugin, @QueryParam("idplugin") String row) throws Exception{
		JSONObject jsonNewVideo = new JSONObject();
		DBConnection dbconn = new DBConnection("mysql");
		String id = dbconn.addVideoTag(videoid,tagname,tagvalue,idplugin,row);
		
		dbconn.CloseDb();
		jsonNewVideo.put("tagid", id);
		return (new JSONWithPadding(jsonNewVideo, callback));
	}
	
	@Path("/removetag")
	@GET
	//@Produces( { MediaType.APPLICATION_XML})
	 public JSONWithPadding RemoveVideoTag(@QueryParam("callback") String callback, @QueryParam("videoid") String videoid, @QueryParam("tagid") String tagid) throws Exception{
		JSONObject jsonNewVideo = new JSONObject();
		DBConnection dbconn = new DBConnection("mysql");
		String id = dbconn.removeVideoTag(videoid,tagid);
		System.out.println("Cancello il tag = " + tagid);
		dbconn.CloseDb();
		jsonNewVideo.put("tagid", 1);
		return (new JSONWithPadding(jsonNewVideo, callback));
	}
	
	@Path("/updatetag")
	@GET
	//@Produces( { MediaType.APPLICATION_XML})
	 public JSONWithPadding UpdateVideoTag(@QueryParam("callback") String callback, @QueryParam("videoid") String videoid, @QueryParam("tagid") String tagid, @QueryParam("tagname") String tagname, @QueryParam("tagvalue") String tagvalue, @QueryParam("idplugin") Integer idplugin) throws Exception{
		JSONObject jsonNewVideo = new JSONObject();
		DBConnection dbconn = new DBConnection("mysql");
		String id = dbconn.updateVideoTag(videoid,tagid,tagname,tagvalue,idplugin);
		System.out.println("Cancello il video = " + videoid);
		dbconn.CloseDb();
		jsonNewVideo.put("tagid", 1);
		return (new JSONWithPadding(jsonNewVideo, callback));
	}
	
	@Path("/getvideojsontag")
	@GET
	//@Produces( { MediaType.APPLICATION_XML})
	 public JSONWithPadding GetJVideoXMLTag(@QueryParam("callback") String callback, @QueryParam("videoid") String videoid) throws Exception{
		JSONObject jsonNewVideo = new JSONObject();
		DBConnection dbconn = new DBConnection("mysql");
		String xml = dbconn.createXml(videoid);
		dbconn.CloseDb();
		jsonNewVideo.put("xml", xml);
		return (new JSONWithPadding(jsonNewVideo, callback));
	}
	
	@Path("/getvideoxmltag")
	@GET
	@Produces({"application/json"})
	//@Produces( { MediaType.APPLICATION_XML})
	 public Response GetVideoXMLTag(@QueryParam("videoid") String videoid) throws Exception{
		JSONObject jsonNewVideo = new JSONObject();
		DBConnection dbconn = new DBConnection("mysql");
		String xml = dbconn.createXml(videoid);
		dbconn.CloseDb();
		jsonNewVideo.put("xml", xml);
		
		ResponseBuilder builder = Response.ok(xml);
        builder.header("Access-Control-Allow-Origin", "*");
        builder.header("Access-Control-Max-Age", "3600");
        builder.header("Access-Control-Allow-Methods", "GET");
        builder.header("Access-Control-Allow-Headers", "X-Requested-With,Host,User-Agent,Accept,Accept-Language,Accept-Encoding,Accept-Charset,Keep-Alive,Connection,Referer,Origin");
 
        return builder.build();
	}
}