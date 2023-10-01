package main.java.com.mywebsite.common;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.properties.PropertiesConfiguration;

import java.io.File;
import java.net.MalformedURLException;

import javax.naming.InitialContext;

import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class MyLogger {
    
    static Logger logger;
    static {init();}
    public static Logger getLogger(String name)
    {
        return LogManager.getLogger(name);
    }
    public static <t> Logger getLogger(Class <t> name)
    {
        return LogManager.getLogger(name);
    }
    /**
	 * method to init log4j configurations
	 */
	private static void init()
	{
		try {
	        System.setProperty(
	        		"log4j.configuration",
	        		new File(System.getProperty("user.dir")+"/src/main/java/com/mywebsite/props/log4j.properties")
	        		.toURI()
	        		.toURL()
	        		.toString()
	        		);
//	        BasicConfigurator.configure();
	        Configurator.initialize(new DefaultConfiguration());
	        Configurator.setRootLevel(Level.INFO);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}