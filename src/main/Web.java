package main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.GsonBuilder;
import com.vaadin.server.VaadinServlet;

import Vaadin.HelloWorldUI;
import database.DatabaseSQLite;

public class Web
{
    private Server server;
    private static String httpbase = System.getProperty("user.dir");
    private static int httpport = 4000;
    public Web () {}
    private void initHttpService(String httpbase, int port)
    {
        try
        {
        	System.out.println("webfolder: "+Web.httpbase);
        	System.out.println("port = "+httpport);
        	if(httpport < 0) {
        		return;
        	}
        	if (server != null) {
        		server = null;
        	}
        	server = new Server(port);
            ///////////////////////////////////////////////
            // manually handle all requests...
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            ///////////////////////////////////////////////
            // html
            ServletHolder sh1 = new ServletHolder(new Web_());
            sh1.setInitParameter("resourceBase", httpbase);
            sh1.setInitParameter("dirAllowed", "true");
            context.addServlet(sh1, httpbase);
            context.setResourceBase(httpbase);
            context.setAllowNullPathInfo(true);
            ///////////////////////////////////////////////
            // end html
            ///////////////////////////////////////////////
            // vaadin
//            ServletHolder vaadin = new ServletHolder(new VaadinServlet());
//            context.addServlet(vaadin, "test");
//            context.setInitParameter("ui", HelloWorldUI.class.getCanonicalName());
            ///////////////////////////////////////////////
            // end vaadin
            ServletHolder sh = new ServletHolder(new Web_());
            context.addServlet(sh, "/");
            server.setHandler(context);
            ///////////////////////////////////////////////
            // end: manually handle all requests...
            
//            // automatically handle all requests (not doget, no control)...
//            ResourceHandler resource_handler = new ResourceHandler();
//            resource_handler.setResourceBase(httpbase);
//            //resource_handler.setResourceBase(httpbase+File.separator+"index.html");
//            resource_handler.setDirectoriesListed(true);
//            resource_handler.setWelcomeFiles(new String[]{"index.html"});
//            Path userDir = Paths.get(httpbase);
//            PathResource pathResource = new PathResource(userDir);
//            resource_handler.setBaseResource(pathResource);
//            HandlerList handlers = new HandlerList();
//            handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
//            server.setHandler(handlers);
//            // end: automatically handle all requests (not doget, no control)...
            server.start();
            server.join();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String args[])
    {
        for(int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-h") || args[i].equals("-help") || args[i].equals("-?") || args[i].equals("?")) {
                showHelp();
            }
            if(args[i].toLowerCase().equals("--httpport"))
            {
                try {
					httpport = Integer.parseInt(args[i + 1]);
				} catch (NumberFormatException e) {}
            }
            if(args[i].toLowerCase().equals("--httpbase"))
            {
                if(new File(args[i + 1]).isDirectory()) {
                    httpbase = args[i + 1];
                }
                else if(new File(args[i + 1]).isFile()) {
                    httpbase = args[i + 1].substring(0, args[i + 1].lastIndexOf("/"));
                }
            }
        }
        if(new File(httpbase).isFile()) {
            httpbase = httpbase.substring(0, httpbase.lastIndexOf("/"));
        }
        new Web().initHttpService(httpbase, httpport);
    }
    private static void showHelp ()
    {
            System.out.println();
            System.out.println("### This program is a webserver with a custom backend that connects to the custom webfolder (by parameter) ###");
            System.out.println(" It will show you available database tables to select, shows nearly all content and can export a file (-> new iafisspy)");
            System.out.println("Syntax: [-help | -h | -? | ?] <--httpport{1025-65536}> <--httpbase{}>");
            System.out.println("\t Options");
            System.out.println("\t\t -h/-help/-?/?                  show this help and exit");
            System.out.println("\t\t --httpport                     the port on that the server opens a connection");
            System.out.println("\t\t --httpbase                     the folder where to find the website");
            System.out.println("\nBye");
            System.exit(0);
    }

    public class Web_ extends HttpServlet
	{
		private static final long serialVersionUID = 1L;
        private String request_stringToGetWebsite = "/";
        private String request_api_weather = "weather";
        private String request_api_example = "example";
        private String request_api_call_data_from_db = "data";
        private String request_api_insert_data_to_db = "insert";
        private String request_api_get_data_for_admin = "admin";
        private String requestByClient = "";
        public Web_() {}
        
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
        {
            DataUse website = new DataUse();
            website.sethttpbase(httpbase);
            String parameter = request.getParameter("get");
            if(parameter != null)
            {
            	System.out.println("Found parameter: "+parameter);
            }
            requestByClient = request.getRequestURI().toLowerCase();
            if (requestByClient.contains(request_stringToGetWebsite)
                    && parameter == null
                    )
            {
                website.clientRequest_Website(request, response);
            }
            else if (request_api_weather.equals(parameter))
            {
                website.clientRequest_Weather(request, response);
            }
//            else if (request_api_example.equals(parameter))
//            {
//                website.clientRequest_TableNames(request, response);
//            }
            else if (request_api_call_data_from_db.equals(parameter))
            {
                website.clientRequest_CallDataFromDb(request, response);
            }
            else if (request_api_insert_data_to_db.equals(parameter))
            {
                website.clientRequest_InsertDataToDb(request, response);
            }
            else if (request_api_get_data_for_admin.equals(parameter))
            {
                website.clientRequest_GetAllData(request, response);
//                website.clientRequest_askUserData(request, response);
            }
        }
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
        {
        	super.doPost(request, response);
            String parameter = request.getParameter("get");
            System.out.println("Found parameter: "+parameter);
            if(parameter != null || request.getParameter("remember") != null)
            {
            	requestByClient = request.getRequestURI().toLowerCase();
            }
        }
    }
}
