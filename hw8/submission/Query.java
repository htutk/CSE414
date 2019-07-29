import java.sql.*;

public class Query extends QuerySearchOnly {

	// Logged In User
	private String username; // customer username is unique

	// transactions
	private static final String BEGIN_TRANSACTION_SQL = "SET TRANSACTION ISOLATION LEVEL SERIALIZABLE; BEGIN TRANSACTION;";
	protected PreparedStatement beginTransactionStatement;

	private static final String COMMIT_SQL = "COMMIT TRANSACTION";
	protected PreparedStatement commitTransactionStatement;

	private static final String ROLLBACK_SQL = "ROLLBACK TRANSACTION";
	protected PreparedStatement rollbackTransactionStatement;

	public Query(String configFilename) {
		super(configFilename);
	}


	/**
	 * Clear the data in any custom tables created. Do not drop any tables and do not
	 * clear the flights table. You should clear any tables you use to store reservations
	 * and reset the next reservation ID to be 1.
	 */
	public void clearTables ()
	{
		try{
			// your code here
			conn.setAutoCommit(false);
			beginTransactionStatement.executeUpdate();

			String deleteTables = "DELETE FROM Reservations;\n" +
			"DELETE FROM ID;\n" + "DELETE FROM Users;\n";

			PreparedStatement ps = conn.prepareStatement(deleteTables);
			ps.clearParameters();
			ps.executeUpdate();

			// reset the next reservation ID to be 1.
			String updateIDQuery = "INSERT INTO ID VALUES(1);\n";
			ps = conn.prepareStatement(updateIDQuery);
			ps.clearParameters();
			ps.executeUpdate();

			// This update statement should be used theoretically.
			// However, it takes very long to complete. So an satisfactory alt may be used.
			String updateFlightsQuery = "UPDATE Flights SET temp_cap = capacity;\n";
			// String updateFlightsQuery = "UPDATE Flights SET temp_cap = capacity "
			// 	+ "WHERE origin_city = 'Seattle WA' " 
			// 	+ "OR origin_city = 'Kahului HI' "
			// 	+ "OR dest_city = 'Los Angeles CA' "
			// 	+ "OR dest_city = 'Boston MA';";
			ps = conn.prepareStatement(updateFlightsQuery);
			ps.clearParameters();
			ps.executeUpdate();

			ps.close();
			commitTransactionStatement.executeUpdate();
			conn.setAutoCommit(true);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	/**
	 * prepare all the SQL statements in this method.
	 * "preparing" a statement is almost like compiling it.
	 * Note that the parameters (with ?) are still not filled in
	 */
	@Override
	public void prepareStatements() throws Exception
	{
		super.prepareStatements();
		beginTransactionStatement = conn.prepareStatement(BEGIN_TRANSACTION_SQL);
		commitTransactionStatement = conn.prepareStatement(COMMIT_SQL);
		rollbackTransactionStatement = conn.prepareStatement(ROLLBACK_SQL);
		
		/* add here more prepare statements for all the other queries you need */
		/* . . . . . . */
		itArray = null;
	}


	/**
	 * Takes a user's username and password and attempts to log the user in.
	 *
	 * @return If someone has already logged in, then return "User already logged in\n"
	 * For all other errors, return "Login failed\n".
	 *
	 * Otherwise, return "Logged in as [username]\n".
	 */
	public String transaction_login(String username, String password)
	{
		if (this.username != null) {
			return "User already logged in\n";
		} else {
			try {
				// conn.setAutoCommit(false);
				// beginTransactionStatement.executeUpdate();

				String loginQuery = "SELECT username FROM Users WHERE username = ? AND pw = ?";
				PreparedStatement ps = conn.prepareStatement(loginQuery);
				ps.setString(1, username);
				ps.setString(2, password);

				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					this.username = rs.getString(1);  // only one col = username
				}
				rs.close();
				// commitTransactionStatement.executeUpdate();
				// conn.setAutoCommit(true);

				if (this.username == null) {
					return "Login failed\n";
				}

				return "Logged in as " + this.username + "\n";
			} catch (SQLException e) {
				// e.printStackTrace();
				return "Login failed\n";
			}
		}
	}

	/**
	 * Implement the create user function.
	 *
	 * @param username new user's username. User names are unique the system.
	 * @param password new user's password.
	 * @param initAmount initial amount to deposit into the user's account, should be >= 0 (failure otherwise).
	 *
	 * @return either "Created user {@code username}\n" or "Failed to create user\n" if failed.
	 */
	public String transaction_createCustomer (String username, String password, int initAmount)
	{
		if (initAmount < 0) {
			return "Failed to create user\n";
		}

		try {
			// conn.setAutoCommit(false);
			// beginTransactionStatement.executeUpdate();

			String loginQuery = "INSERT INTO Users VALUES(?, ?, ?);\n";
			PreparedStatement ps = conn.prepareStatement(loginQuery);
			
			ps.clearParameters();
			ps.setString(1, username);
			ps.setString(2, password);
			ps.setInt(3, initAmount);
			ps.executeUpdate();

			// commitTransactionStatement.executeUpdate();
			// conn.setAutoCommit(true);
			//conn.rollback();
			//conn.setAutoCommit(true);
			ps.close();
			return "Created user " + username + "\n"; 
		} catch (SQLException e) {
			// e.printStackTrace();
			return "Failed to create user\n";
		}
	}

	/**
	 * Implements the book itinerary function.
	 *
	 * @param itineraryId ID of the itinerary to book. This must be one that is returned by search in the current session.
	 *
	 * @return If the user is not logged in, then return "Cannot book reservations, not logged in\n".
	 * If try to book an itinerary with invalid ID, then return "No such itinerary {@code itineraryId}\n".
	 * If the user already has a reservation on the same day as the one that they are trying to book now, then return
	 * "You cannot book two flights in the same day\n".
	 * For all other errors, return "Booking failed\n".
	 *
	 * And if booking succeeded, return "Booked flight(s), reservation ID: [reservationId]\n" where
	 * reservationId is a unique number in the reservation system that starts from 1 and increments by 1 each time a
	 * successful reservation is made by any user in the system.
	 */
	public String transaction_book(int itineraryId)
	{
		if (this.username == null) {
			return "Cannot book reservations, not logged in\n";
		}
		int index = 0;
		boolean valid = false;
		if (itArray == null) {
			return "No such itinerary code " + itineraryId + "\n";
		}
		try {
			while (itArray[index] != null) {
				if (itArray[index].iid == itineraryId) {
					valid = true;
					break;
				}
				index++;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			return "No such itinerary code " + itineraryId + "\n";
		}
		if (!valid) {
			return "No such itinerary code " + itineraryId + "\n";
		}

		try {
			conn.setAutoCommit(false);
			beginTransactionStatement.executeUpdate();

			Itinerary book = itArray[itineraryId];

			String sameDayQuery = "SELECT day_of_month FROM Flights, Reservations " +
				"WHERE username = ? AND fid = fid1";
			PreparedStatement ps = conn.prepareStatement(sameDayQuery);
			ps.clearParameters();
			ps.setString(1, this.username);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				int r_day = rs.getInt(1);
				if (book.day == r_day) {
					rollbackTransactionStatement.executeUpdate();
					conn.setAutoCommit(true);
					rs.close();
					return "You cannot book two flights in the same day\n";
				}
			}

			// check capacity
			int cap1 = 0;
			int cap2 = 0;

			String capQuery = "SELECT temp_cap FROM Flights WHERE fid = ?;\n";
			ps = conn.prepareStatement(capQuery);
			ps.clearParameters();
			ps.setInt(1, book.fid1);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				cap1 = rs.getInt(1);
				if (cap1 < 1) {
					rollbackTransactionStatement.executeUpdate();
					conn.setAutoCommit(true);
					rs.close();
					return "Booking failed\n";
				}
			}

			if (book.fid2 != -1) {
				ps.clearParameters();
				ps.setInt(1, book.fid2);
				rs = ps.executeQuery();

				while (rs.next()) {
					cap2 = rs.getInt(1);
					if (cap2 < 1) {
						rollbackTransactionStatement.executeUpdate();
						conn.setAutoCommit(true);
						rs.close();
						return "Booking failed\n";
					}
				}
			}
			
			// update Flights table (subtract 1 from capacity of each fid)
			String updateFlightsQuery = "UPDATE Flights SET temp_cap = ? WHERE fid = ?;\n";
			ps = conn.prepareStatement(updateFlightsQuery);
			ps.clearParameters();
			if (cap1 != 0) {
				ps.setInt(1, cap1 - 1);
			}
			ps.setInt(2, book.fid1);
			ps.executeUpdate();

			if (book.fid2 != -1) {
				ps.clearParameters();
				if (cap2 != 0) {
					ps.setInt(1, cap2 - 1);
				}
				ps.setInt(2, book.fid2);
				ps.executeUpdate();
			}

			// update Reservation table (add rid and such)
			String ridQuery = "SELECT * FROM ID;\n";
			ps = conn.prepareStatement(ridQuery);
			ps.clearParameters();
			rs = ps.executeQuery();
			
			rs.next();
			int rid = rs.getInt(1);  // only one value

			String updateResQuery = "INSERT INTO Reservations VALUES(?, ?, ?, ?, ?, ?);\n";
			ps = conn.prepareStatement(updateResQuery);
			ps.clearParameters();
			ps.setInt(1, rid);
			ps.setInt(2, 0);  // paid value
			ps.setInt(3, book.fid1);
			ps.setInt(4, book.fid2);
			ps.setString(5, this.username);
			ps.setInt(6, book.price);
			ps.executeUpdate();
			
			String updateRidQuery = "UPDATE ID SET rid = ?";
			ps = conn.prepareStatement(updateRidQuery);
			ps.clearParameters();
			ps.setInt(1, rid + 1);
			ps.executeUpdate();

			commitTransactionStatement.executeUpdate();
			conn.setAutoCommit(true);
			rs.close();
			return "Booked flight(s), reservation ID: " + rid + "\n";
		} catch (SQLException e) {
			e.printStackTrace();
			return "Booking failed\n";
		}
	}

	/**
	 * Implements the pay function.
	 *
	 * @param reservationId the reservation to pay for.
	 *
	 * @return If no user has logged in, then return "Cannot pay, not logged in\n"
	 * If the reservation is not found / not under the logged in user's name, then return
	 * "Cannot find unpaid reservation [reservationId] under user: [username]\n"
	 * If the user does not have enough money in their account, then return
	 * "User has only [balance] in account but itinerary costs [cost]\n"
	 * For all other errors, return "Failed to pay for reservation [reservationId]\n"
	 *
	 * If successful, return "Paid reservation: [reservationId] remaining balance: [balance]\n"
	 * where [balance] is the remaining balance in the user's account.
	 */
	public String transaction_pay (int reservationId)
	{
		if (this.username == null) {
			return "Cannot pay, not logged in\n";
		}

		try {
			conn.setAutoCommit(false);
			beginTransactionStatement.executeUpdate();

			String checkResQuery = "SELECT rid FROM Reservations WHERE username = ? "
				+ "AND paid = 0;\n";

			PreparedStatement ps = conn.prepareStatement(checkResQuery);
			ps.clearParameters();
			ps.setString(1, this.username);
			ResultSet rs = ps.executeQuery();

			boolean foundUnpaid = false;

			while (rs.next()) {
				int r_rid = rs.getInt(1);
				if (r_rid == reservationId) {
					foundUnpaid = true;
					break;
				}
			}

			if (!foundUnpaid) {
				rollbackTransactionStatement.executeUpdate();
				conn.setAutoCommit(true);
				rs.close();
				return "Cannot find unpaid reservation " + reservationId + " under user: "
					+ this.username + "\n";
			}

			String costQuery = "SELECT price FROM Reservations WHERE rid = ?;\n";
			ps = conn.prepareStatement(costQuery);
			ps.clearParameters();
			ps.setInt(1, reservationId);
			rs = ps.executeQuery();
			rs.next();
			int cost = rs.getInt(1);

			String userQuery = "SELECT balance FROM Users WHERE username = ?;\n";
			ps = conn.prepareStatement(userQuery);
			ps.clearParameters();
			ps.setString(1, this.username);
			rs = ps.executeQuery();
			rs.next();
			int balance = rs.getInt(1);

			if (balance < cost) {
				rollbackTransactionStatement.executeUpdate();
				conn.setAutoCommit(true);
				rs.close();
				return "User has only " + balance + " in account but itinerary costs " + cost + "\n";
			}
			

			// update reservation table (unpaid -> paid)
			String updateResQuery = "UPDATE Reservations SET paid = 1 WHERE rid = ?;\n";
			ps = conn.prepareStatement(updateResQuery);
			ps.clearParameters();
			ps.setInt(1, reservationId);
			ps.executeUpdate();

			// update user table (balance decreases)
			int remaining_balance = balance - cost;
			String updateUserQuery = "UPDATE Users SET balance = ? WHERE username = ?;\n";
			ps = conn.prepareStatement(updateUserQuery);
			ps.clearParameters();
			ps.setInt(1, remaining_balance);
			ps.setString(2, this.username);
			ps.executeUpdate();

			commitTransactionStatement.executeUpdate();
			conn.setAutoCommit(true);
			rs.close();
			return "Paid reservation: " + reservationId + " remaining balance: " + remaining_balance + "\n";
		} catch (SQLException e) {
			e.printStackTrace();
			return "Failed to pay for reservation " + reservationId + "\n";
		}
	}

	/**
	 * Implements the reservations function.
	 *
	 * @return If no user has logged in, then return "Cannot view reservations, not logged in\n"
	 * If the user has no reservations, then return "No reservations found\n"
	 * For all other errors, return "Failed to retrieve reservations\n"
	 *
	 * Otherwise return the reservations in the following format:
	 *
	 * Reservation [reservation ID] paid: [true or false]:\n"
	 * [flight 1 under the reservation]
	 * [flight 2 under the reservation]
	 * Reservation [reservation ID] paid: [true or false]:\n"
	 * [flight 1 under the reservation]
	 * [flight 2 under the reservation]
	 * ...
	 *
	 * Each flight should be printed using the same format as in the {@code Flight} class.
	 *
	 * @see Flight#toString()
	 */
	public String transaction_reservations()
	{
		if (this.username == null) {
			return "Cannot view reservations, not logged in\n";
		}

		try {
			conn.setAutoCommit(false);
			beginTransactionStatement.executeUpdate();

			String resQuery = "SELECT rid, paid, fid1, fid2 FROM Reservations WHERE username = ?;\n";
			PreparedStatement ps = conn.prepareStatement(resQuery);
			ps.clearParameters();
			ps.setString(1, this.username);
			ResultSet rs = ps.executeQuery();

			StringBuffer sb = new StringBuffer();
			while (rs.next()) {
				int r_rid = rs.getInt(1);
				int r_paid = rs.getInt(2);
				int r_fid1 = rs.getInt(3);
				int r_fid2 = rs.getInt(4);
				String paid = (r_paid == 1) ? "true" : "false";

				sb.append("Reservation ").append(r_rid).append(" paid: ").append(paid).append(":\n");

				String flightQuery = "SELECT fid, day_of_month, carrier_id, flight_num, origin_city,"
					+ "dest_city, actual_time, capacity, price FROM Flights WHERE fid = ?;\n";
				ps = conn.prepareStatement(flightQuery);
				ps.clearParameters();
				ps.setInt(1, r_fid1);
				ResultSet frs = ps.executeQuery();  // flight resultset
				frs.next();

				int rf_fid = frs.getInt(1);  // rf for result [from] flight
				int rf_day_of_month = frs.getInt(2);
				String rf_cid = frs.getString(3);
				int rf_flight_num = frs.getInt(4);
				String rf_org_city = frs.getString(5);
				String rf_dest_city = frs.getString(6);
				int rf_actual_time = frs.getInt(7);
				int rf_capacity = frs.getInt(8);
				int rf_price = frs.getInt(9);

				sb.append("ID: ").append(rf_fid).append(" Day: ").append(rf_day_of_month)
					.append(" Carrier: ").append(rf_cid).append(" Number: ").append(rf_flight_num)
					.append(" Origin: ").append(rf_org_city).append(" Dest: ").append(rf_dest_city)
					.append(" Duration: ").append(rf_actual_time).append(" Capacity: ").append(rf_capacity)
					.append(" Price: ").append(rf_price).append("\n");

				if (r_fid2 != -1) {
					ps = conn.prepareStatement(flightQuery);
					ps.clearParameters();
					ps.setInt(1, r_fid2);
					frs = ps.executeQuery();
					frs.next();

					rf_fid = frs.getInt(1);
					rf_day_of_month = frs.getInt(2);
					rf_cid = frs.getString(3);
					rf_flight_num = frs.getInt(4);
					rf_org_city = frs.getString(5);
					rf_dest_city = frs.getString(6);
					rf_actual_time = frs.getInt(7);
					rf_capacity = frs.getInt(8);
					rf_price = frs.getInt(9);

					sb.append("ID: ").append(rf_fid).append(" Day: ").append(rf_day_of_month)
					.append(" Carrier: ").append(rf_cid).append(" Number: ").append(rf_flight_num)
					.append(" Origin: ").append(rf_org_city).append(" Dest: ").append(rf_dest_city)
					.append( "Duration: ").append(rf_actual_time).append(" Capacity: ").append(rf_actual_time)
					.append(" Price: ").append(rf_price).append("\n");

					frs.close();
				}
			}
			rs.close();
			if (sb.toString().equals("")) {
				rollbackTransactionStatement.executeUpdate();
				conn.setAutoCommit(true);
				return "No reservations found\n";
			} else {
				commitTransactionStatement.executeUpdate();
				conn.setAutoCommit(true);
				return sb.toString();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return "Failed to retrieve reservations\n";
		}
	}

	/**
	 * Implements the cancel operation.
	 *
	 * @param reservationId the reservation ID to cancel
	 *
	 * @return If no user has logged in, then return "Cannot cancel reservations, not logged in\n"
	 * For all other errors, return "Failed to cancel reservation [reservationId]"
	 *
	 * If successful, return "Canceled reservation [reservationId]"
	 *
	 * Even though a reservation has been canceled, its ID should not be reused by the system.
	 */
	public String transaction_cancel(int reservationId)
	{
		// only implement this if you are interested in earning extra credit for the HW!
		return "Failed to cancel reservation " + reservationId;
	}


	/* some utility functions below */

	public void beginTransaction() throws SQLException
	{
		conn.setAutoCommit(false);
		beginTransactionStatement.executeUpdate();
	}

	public void commitTransaction() throws SQLException
	{
		commitTransactionStatement.executeUpdate();
		conn.setAutoCommit(true);
	}

	public void rollbackTransaction() throws SQLException
	{
		rollbackTransactionStatement.executeUpdate();
		conn.setAutoCommit(true);
	}
}
