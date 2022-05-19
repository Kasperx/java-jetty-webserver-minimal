
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import database.DatabaseSQLite;

public class Test {

	DatabaseSQLite sql = new DatabaseSQLite();
	
	@Before
	public void setUp() throws Exception {
		sql.connect();
		sql.createDatabaseIfNotExists();
		sql.insertData();
	}

	@After
	public void tearDown() throws Exception {
	}

	@org.junit.Test
	public void test() {
		ArrayList<ArrayList<String>> data =  sql.getData();
		System.out.println();
		Assert.assertEquals(2, data.size());
		fail("Not yet implemented");
	}
}
