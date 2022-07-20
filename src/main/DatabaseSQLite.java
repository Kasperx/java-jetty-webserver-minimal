
package main;

import java.sql.Statement;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;  
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class DatabaseSQLite extends Tools
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
        	if(connection == null || connection.isClosed())
        	{
        		Class.forName("org.sqlite.JDBC");
        		connection = DriverManager.getConnection("jdbc:sqlite:"+path);
        		System.out.println("Connected to database '"+path+"'.");
        	}
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
//    public void close()
//    {
//        try
//        {
//            Class.forName("org.sqlite.JDBC");
//            if(connection != null && !connection.isClosed())
//            {
//                connection.close();
//                connection = null;
//            }
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
    public ArrayList<ArrayList<String>> getData()
    {
        String sql = ""
        		+ "SELECT "
        		+ "name, "
        		+ "lastname "
        		+ "FROM "
        		+ "person "
        		+ "where name != 'admin';";
//        String sql = ""
//        		+ "SELECT * "
//        		+ "FROM "
//        		+ "person "
//        		+ "where name != 'admin';";
        ArrayList<ArrayList<String>> data = getDataFromDBWithHeader(sql);
        return data;
    }
    public int getId(String name)
    {
        ResultSet resultSet = executeGet("SELECT id FROM person where name = '"+name+"';");
        try
        {
            if(resultSet.next())
            {
            	int id = resultSet.getInt("id");
            	close(resultSet);
                return id;
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
        String sql = "SELECT "
                + "person.id as id, "
                + "person.name as name, "
                + "person.lastname as lastname, "
                + "person.email as email, "
                + "person.address as address, "
                + "login.p_password as password, "
                + "login.p_admin as admin "
                + "FROM person "
                + "join login on person.id = login.p_id";
        ArrayList <ArrayList<String>> data = getDataFromDBWithHeader(sql);
//        try
//        {
//        	ResultSet resultSet = executeGet(sql);
//        	ResultSetMetaData rsmd = getMetaData(sql);
//        	ArrayList <String> temp = new ArrayList<String>();
//            for(int column=1; column <= rsmd.getColumnCount(); column++)
//            {
//        		temp.add(rsmd.getColumnName(column));
//            }
//            data.add(temp);
//            while(resultSet.next())
//            {
//            	temp = new ArrayList<String>();
//            	for(int column=1; column <= rsmd.getColumnCount(); column++)
//            	{
//        			temp.add(resultSet.getString(column));
//            	}
//            	data.add(temp);
//            }
//        }
//        catch(SQLException e)
//        {
//            e.printStackTrace();
//        }
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
                + "lastname text,"
                + "email text,"
                + "address text"
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
        HashMap <String[], Integer> result = getNewData();
        ///////////////////////////////////////////////////////////
        executeSet("insert into person (name, lastname) values ('admin', 'admin');");
        executeSet("insert into login (p_id, p_password, p_admin) values (1, 'secret', 'true');");
        for(Entry <String[], Integer> entry: result.entrySet())
        {
        	String name = entry.getKey()[0];
        	String lastname = entry.getKey()[1];
        	String email = entry.getKey()[2];
        	String address = entry.getKey()[3];
        	int pw = entry.getValue();
            executeSet(""
            		+ "insert into person ("
            		+ "name, "
            		+ "lastname, "
            		+ "email,"
            		+ "address"
            		+ ") "
            		+ "values "
            		+ "("
            		+ "'"+name.replace("'", "-")+"', "
    				+ "'"+lastname.replace("'", "-")+"', "
    				+ "'"+email.replace("'", "-")+"', "
    				+ "'"+address.replace("'", "-")+"'"
					+ ")"
					+ ";");
            executeSet("insert into "
            		+ "login ("
            		+ "p_id, "
            		+ "p_password"
            		+ ") values ("
            		+ ""+getId(name)+", "
    				+ "'"+pw+"'"
					+ ");");
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
            close(resultSet);
            return false;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    private void close(ResultSet resultSet)
    {
    	try {
    		if(resultSet != null && !resultSet.isClosed())
    		{
    			resultSet.close();
    		}
    		if(connection != null)
    		{
    			connection.close();
    		}
		}
    	catch (SQLException e)
    	{
			e.printStackTrace();
		}
	}
	ResultSet executeGet(String sql)
    {
        try
        {
            System.out.println(sql);
            connect();
//            PreparedStatement stmt = connection.prepareStatement(sql);
//            return stmt.executeQuery();
            Statement st = connection.createStatement();
            return st.executeQuery(sql);
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
//    		System.out.println(sql);
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
            connect();
            connection.prepareStatement(sql).executeUpdate();
            close(null);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }
    ArrayList <ArrayList<String>> getDataFromDBWithoutHeader(String sql)
    {
    	ArrayList <ArrayList<String>> data = new ArrayList<ArrayList<String>>();
    	try
    	{
        	ResultSet resultSet = executeGet(sql);
        	ResultSetMetaData rsmd = resultSet.getMetaData();
        	data = getDataFromDB(sql, resultSet, rsmd);
    	}
    	catch(SQLException e)
    	{
    		e.printStackTrace();
    	}
    	return data;
    }    
    ArrayList <ArrayList<String>> getDataFromDB(String sql, ResultSet resultSet, ResultSetMetaData rsmd)
    {
    	ArrayList <ArrayList<String>> data = new ArrayList<ArrayList<String>>();
        try
        {
        	ArrayList <String> temp = new ArrayList<String>();
        	if(resultSet.isClosed())
        	{
        		System.err.println("connection closed");
        	}
            while(resultSet.next())
            {
            	temp = new ArrayList<String>();
            	for(int column=1; column <= rsmd.getColumnCount(); column++)
            	{
        			temp.add(resultSet.getString(column));
            	}
            	data.add(temp);
            }
            close(resultSet);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return data;
    }    
    ArrayList <ArrayList<String>> getDataFromDBWithHeader(String sql)
    {
    	ArrayList <ArrayList<String>> data = new ArrayList<ArrayList<String>>();
    	ArrayList <ArrayList<String>> header = new ArrayList<ArrayList<String>>();
    	ArrayList <ArrayList<String>> content = new ArrayList<ArrayList<String>>();
    	try
    	{
    		connect();
    		ResultSet resultSet = executeGet(sql);
    		// get header
    		ResultSetMetaData rsmd = getMetaData(sql);
    		ArrayList <String> temp = new ArrayList<String>();
    		for(int column=1; column <= rsmd.getColumnCount(); column++)
    		{
    			if(headerInUppercaseCharacter)
    			{
    				temp.add(rsmd.getColumnName(column).toUpperCase());
    			}
    			else
    			{
    				temp.add(rsmd.getColumnName(column).toLowerCase());
    			}
    		}
    		header.add(temp);
    		// get content
    		content = getDataFromDB(sql, resultSet, rsmd);
    		close(resultSet);
    		// migrate
    		for(ArrayList<String> migrate: header)
    		{
    			data.add(migrate);
    		}
    		for(ArrayList<String> migrate: content)
    		{
    			data.add(migrate);
    		}
    	}
    	catch(SQLException e)
    	{
    		e.printStackTrace();
    	}
    	return data;
    }
//    public static void main(String[] args) {
//    	DatabaseSQLite obj = new DatabaseSQLite();
//    	obj.createDatabaseIfNotExists();
//    	obj.insertData();
//    	ArrayList<ArrayList<String>> data = obj.getData();
//    	for(ArrayList<String> temp: data)
//    	{
//    		for(String temp1: temp)
//    		{
//    			System.out.print(temp1+" : ");
//    		}
//    		System.out.println();
//    		
//    	}
//	}
}  

