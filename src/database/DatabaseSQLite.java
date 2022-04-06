
package database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;  
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class DatabaseSQLite extends Database
{  
    String path = System.getProperty("user.dir")+"/test.db";
    Connection connection = null;
    
    public DatabaseSQLite()
    {
        File dbFile = new File(path);
        try
        {
            if(!dbFile.exists())
            {
                dbFile.createNewFile();
            }
            connect();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    public void connect()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:"+path);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void close()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            if(connection.isClosed() || connection != null)
            {
                connection.close();
                connection = null;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public ArrayList<ArrayList<String>> getData()
    {
        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        String sql = "SELECT id, name, lastname FROM person where person.name != 'admin'";
//        sql = "SELECT "
//                + "person.id, "
//                + "person.name, "
//                + "login.p_password, "
//                + "login.p_admin "
//                + "FROM person "
//                + "join login on person.id = login.p_id "
//                + "where person.name != 'admin'";
        ResultSet resultSet = executeGet(sql);
        ResultSetMetaData rsmd = getMetaData(sql);
        try
        {
            ArrayList <String> temp;
            while(resultSet.next())
            {
            	temp = new ArrayList<String>();
//            	if(resultSet.getString("name").equals("admin"))
//            	{
//            		continue;
//            	}
            	for(int column=1; column <= rsmd.getColumnCount(); column++)
            	{
//	                temp.add(resultSet.getString("name"));
            		if(!rsmd.getColumnName(column).equals("p_password")
        				&& !rsmd.getColumnName(column).equals("p_admin")
            			)
            		{
            			temp.add(resultSet.getString(column));
            		}
            	}
            	data.add(temp);
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return data;
    }
    public int getId(String name)
    {
        ResultSet resultSet = executeGet("SELECT id FROM person where name = '"+name+"'");
        try
        {
            if(resultSet.next())
            {
                return resultSet.getInt("id");
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return -1;
    }
    public ArrayList <ArrayList<String>> getAllData()
    {
        ArrayList <ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        ResultSet resultSet = executeGet("SELECT "
                + "person.id, "
                + "person.name, "
                + "person.lastname, "
                + "login.p_password, "
                + "login.p_admin "
                + "FROM person "
                + "join login on person.id = login.p_id");
        try
        {
            ArrayList <String> temp;
            while(resultSet.next())
            {
                temp = new ArrayList<String>();
                temp.add(resultSet.getString("id"));
                temp.add(resultSet.getString("name"));
                temp.add(resultSet.getString("lastname"));
                temp.add(resultSet.getString("p_password"));
                temp.add(resultSet.getString("p_admin"));
                data.add(temp);
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return data;
    }
    public boolean createDatabaseIfNotExists()
    {
        executeSet("drop table if exists person");
        executeSet("drop table if exists login");
        //////////////////////////////
        executeSet("create table if not exists person ("
                + "id integer primary key autoincrement,"
                + "name text unique,"
                + "lastname text"
                + ")");
        executeSet("create table if not exists login ("
                + "id integer primary key autoincrement,"
                + "p_id integer,"
                + "p_name text,"
                + "p_lastname text,"
                + "p_password text unique,"
                + "p_admin default 'false',"
                + "foreign key (p_id) references person(id)"
                + ")");
        return true;
    }
    public void insertData()
    {
//        ///////////////////////////////////////////////////////////
//        // get names, but only once (-> set)
//        String [] names = {
//                "detlef",
//                "arnold",
//                "ulrike",
//                "emil",
//                "lena",
//                "laura",
//                "achim",
//                "mia",
//                "anna",
//                "jonas"
//        };
//        Map <String, Integer> result = new HashMap<String, Integer>();
//        Set unique = new HashSet();
//        int temp = 0;
//        while(temp<5)
//        {
//            String found = names[new Random().nextInt(10)];
//            if(unique.add(found))
//            {
//                result.put(found, new Random().nextInt(10000000) + 1000000);
//                temp++;
//            }
//        }
        HashMap <String, Integer> result = getNewData();
        ///////////////////////////////////////////////////////////
        executeSet("insert into person (name, lastname) values ('admin', 'admin')");
        executeSet("insert into login (p_id, p_password, p_admin) values (1, 'secret', 'true')");
        for(Entry <String, Integer> entry: result.entrySet())
        {
            executeSet(""
            		+ "insert into person ("
            		+ "name, "
            		+ "lastname"
            		+ ") "
            		+ "values "
            		+ "("
            		+ "'"+entry.getKey().split(":")[0]+"', "
    				+ "'"+entry.getKey().split(":")[1]+"'"
					+ ")"
					+ "");
            executeSet("insert into login (p_id, p_password) values ("+getId(entry.getKey())+", '"+entry.getValue()+"')");
        }
    }
    public boolean isPermitted(String name, String password)
    {
        try
        {
//            ResultSet resultSet = executeGet("SELECT p.name, login.p_password, login.p_admin FROM person p inner join login on p.id = login.p_id where login.p_admin = 'true'");
            ResultSet resultSet = executeGet("SELECT p.id, p.name, login.p_password, login.p_admin FROM person p inner join login on p.id = login.p_id where login.p_admin = 'true'");
            if(resultSet.next())
            {
                String tempname = resultSet.getString("name");
                String temppw = resultSet.getString("p_password");
                if(name != null && password != null & name.equals(tempname) && password.equals(temppw))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            return false;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    ResultSet executeGet(String sql)
    {
        try
        {
            System.out.println(sql);
            PreparedStatement stmt = connection.prepareStatement(sql);
            return stmt.executeQuery();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    ResultSetMetaData getMetaData(String sql)
    {
    	try
    	{
    		System.out.println(sql);
    		PreparedStatement stmt = connection.prepareStatement(sql);
    		return stmt.getMetaData();
    	}
    	catch(SQLException e)
    	{
    		e.printStackTrace();
    		return null;
    	}
    }
    void executeSet(String sql)
    {
        try
        {
            System.out.println(sql);
            connection.prepareStatement(sql).executeUpdate();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
}  

