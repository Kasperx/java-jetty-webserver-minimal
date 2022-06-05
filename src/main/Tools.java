package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.github.javafaker.Faker;

public class Tools
{
	HashMap<String, String> mapFromFile;
    boolean headerInUppercaseCharacter = true;
    
    protected HashMap <String[], Integer> getNewData()
    {
        HashMap<String[], Integer> result = new HashMap<String[], Integer>();
        Faker faker;
        int temp = 0;
        while(temp < 10)
        {
            faker = new Faker();
//            String name = faker.name().fullName();
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String streetAddress = faker.address().streetAddress();
//            result.put(firstName, new Random().nextInt(10000000) + 1000000);
            String email = firstName.substring(0,1)+"."+lastName+"@web.de";
            result.put(new String[]{firstName, lastName, email.toLowerCase(), streetAddress},
            		new Random().nextInt(10000000) + 1000000);
            temp++;
        }
        return result;
    }
	public boolean isHeaderInUppercaseCharacter()
	{
		return headerInUppercaseCharacter;
	}
	public void setHeaderInUppercaseCharacter(boolean headerInUppercaseCharacter)
	{
		this.headerInUppercaseCharacter = headerInUppercaseCharacter;
	}
	public Map <String, String> getProperties(String filename)
    {
        mapFromFile = new HashMap<String, String>();
        if(!new File(filename).exists())
        {
            System.out.println("File '"+filename+"' does not exist");
            return mapFromFile;
        }
        try (BufferedReader br = new BufferedReader( new FileReader(filename));)
        {
                        String line = "";
                mapFromFile = new HashMap<String, String>();
                        while ((line = br.readLine()) != null)
                        {
                String[] parts = line.split("=");
                String name = parts[0].trim();
                String value = parts[1].trim();
                if(!name.equals("") && !value.equals(""))
                {
                    mapFromFile.put(name, value);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return mapFromFile;
        }
        return mapFromFile;
    }
    public String getProperty (String keyname)
    {
        return mapFromFile.get(keyname);
    }
}
