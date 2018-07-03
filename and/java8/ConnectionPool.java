package collectionpool.and.java8;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {
	private List<Connection> availableConnections = new ArrayList<Connection>();
	private List<Connection> usedConnections = new ArrayList<Connection>();
	private List<Long> currentMilliseconds = new ArrayList<Long>();

	private final int MAX_CONNECTIONS = 5;
	private final int MIN_CONNECTIONS = 1;
	private String url;
	private String userID;
	private String password;

	public ConnectionPool(String url, String userId, String password) throws SQLException {
		this.url = url;
		this.userID = userId;
		this.password = password;

		for (int count = 0; count < MIN_CONNECTIONS; count++) {
			availableConnections.add(this.createConnection());
		}

	}

	private Connection createConnection() throws SQLException {
		return DriverManager.getConnection(this.url, this.userID, this.password);
	}

	public Connection getConnection() throws SQLException {
		if (availableConnections.size() == 0) {
			if ((availableConnections.size() + usedConnections.size()) < MAX_CONNECTIONS) {
				availableConnections.add(this.createConnection());
				Connection con = availableConnections.remove(availableConnections.size() - 1);
				usedConnections.add(con);
				this.currentMilliseconds.add(System.currentTimeMillis());
				return con;
			} else {
				System.out.println("All connections are used !!");
				return null;
			}
		} else {
			Connection con = availableConnections.remove(availableConnections.size() - 1);
			usedConnections.add(con);
			this.currentMilliseconds.add(System.currentTimeMillis());
			return con;
		}
	}

	public boolean releaseConnection(Connection con) {
		if (null != con) {
			usedConnections.remove(con);
			availableConnections.add(con);

			int index = availableConnections.indexOf(con);
			currentMilliseconds.remove(index);
			return true;
		}
		return false;
	}

	public int getFreeConnectionCount() {
		return availableConnections.size();
	}

	public static void main(String[] args) throws SQLException {
		ConnectionPool pool = new ConnectionPool("jdbc:mysql://localhost:3306/Chat", "root", "abcd1234");
		Connection con1 = pool.getConnection();
		Connection con2 = pool.getConnection();
		Connection con3 = pool.getConnection();
		Connection con4 = pool.getConnection();
		Connection con5 = pool.getConnection();
		Connection con6 = pool.getConnection();
		new Thread(new TestConnectionPool(pool)).start();
		try {
			Thread.sleep(2000);
			// if not use this line, time out will happen
			// else connection will be released
			pool.releaseConnection(con3);
		} catch (InterruptedException e) {
			System.out.println("got interrupted!");
		}

	}

}
