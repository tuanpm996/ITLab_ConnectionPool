package collectionpool.and.java8;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnectionPool implements Runnable {
	private ConnectionPool conn;

	public TestConnectionPool(ConnectionPool conn) {
		this.conn = conn;
	}

	public void run() {
		// TODO Auto-generated method stub
		try {
			Connection con10 = null;
			for(int i=0; i<100;i++) {
				Thread.sleep(300);	
				con10 = conn.getConnection();
				if(con10 != null) {
					System.out.println("Get before time out!");
					break;
				}
			}
			if(con10==null) {
				System.out.println("Time out!");
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
