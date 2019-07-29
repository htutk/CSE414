import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Runs queries against a back-end database.
 * This class is responsible for searching for flights.
 */
public class QuerySearchOnly
{
  // `dbconn.properties` config file
  private String configFilename;

  // DB Connection
  protected Connection conn;

  // Canned queries
  private static final String CHECK_FLIGHT_CAPACITY = "SELECT capacity FROM Flights WHERE fid = ?";
  protected PreparedStatement checkFlightCapacityStatement;

  // make it public to give children access
  public Itinerary itArray[];

  class Flight
  {
    public int fid;
    public int dayOfMonth;
    public String carrierId;
    public String flightNum;
    public String originCity;
    public String destCity;
    public int time;
    public int capacity;
    public int price;

    @Override
    public String toString()
    {
      return "ID: " + fid + " Day: " + dayOfMonth + " Carrier: " + carrierId +
              " Number: " + flightNum + " Origin: " + originCity + " Dest: " + destCity + " Duration: " + time +
              " Capacity: " + capacity + " Price: " + price;
    }
  }

  public class Itinerary {
    public int iid;
    public int fid1;
    public int fid2;
    public int price;
    public int day;
  
    public Itinerary(int iid, int fid1, int fid2, int price, int day) {
      this.iid = iid;
      this.fid1 = fid1;
      this.fid2 = fid2;
      this.price = price;
      this.day = day;
    }

    @Override
    public String toString() {
      return "Iid: " + this.iid + " fid1: " + this.fid1 + " fid2: " + this.fid2 + " price: " + this.price
        + " day_of_month: " + this.day;
    }
  }

  public QuerySearchOnly(String configFilename)
  {
    this.configFilename = configFilename;
  }

  /** Open a connection to SQL Server in Microsoft Azure.  */
  public void openConnection() throws Exception
  {
    Properties configProps = new Properties();
    configProps.load(new FileInputStream(configFilename));

    String jSQLDriver = configProps.getProperty("flightservice.jdbc_driver");
    String jSQLUrl = configProps.getProperty("flightservice.url");
    String jSQLUser = configProps.getProperty("flightservice.sqlazure_username");
    String jSQLPassword = configProps.getProperty("flightservice.sqlazure_password");

    /* load jdbc drivers */
    Class.forName(jSQLDriver).newInstance();

    /* open connections to the flights database */
    conn = DriverManager.getConnection(jSQLUrl, // database
            jSQLUser, // user
            jSQLPassword); // password

    conn.setAutoCommit(true); //by default automatically commit after each statement
    /* In the full Query class, you will also want to appropriately set the transaction's isolation level:
          conn.setTransactionIsolation(...)
       See Connection class's JavaDoc for details.
    */
    conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
  }

  public void closeConnection() throws Exception
  {
    conn.close();
  }

  /**
   * prepare all the SQL statements in this method.
   * "preparing" a statement is almost like compiling it.
   * Note that the parameters (with ?) are still not filled in
   */
  public void prepareStatements() throws Exception
  {
    checkFlightCapacityStatement = conn.prepareStatement(CHECK_FLIGHT_CAPACITY);

    /* add here more prepare statements for all the other queries you need */
    /* . . . . . . */
  }



  /**
   * Implement the search function.
   *
   * Searches for flights from the given origin city to the given destination
   * city, on the given day of the month. If {@code directFlight} is true, it only
   * searches for direct flights, otherwise it searches for direct flights
   * and flights with two "hops." Only searches for up to the number of
   * itineraries given by {@code numberOfItineraries}.
   *
   * The results are sorted based on total flight time.
   *
   * @param originCity
   * @param destinationCity
   * @param directFlight if true, then only search for direct flights, otherwise include indirect flights as well
   * @param dayOfMonth
   * @param numberOfItineraries number of itineraries to return
   *
   * @return If no itineraries were found, return "No flights match your selection\n".
   * If an error occurs, then return "Failed to search\n".
   *
   * Otherwise, the sorted itineraries printed in the following format:
   *
   * Itinerary [itinerary number]: [number of flights] flight(s), [total flight time] minutes\n
   * [first flight in itinerary]\n
   * ...
   * [last flight in itinerary]\n
   *
   * Each flight should be printed using the same format as in the {@code Flight} class. Itinerary numbers
   * in each search should always start from 0 and increase by 1.
   *
   * @see Flight#toString()
   */
  public String transaction_search(String originCity, String destinationCity, boolean directFlight, int dayOfMonth,
                                   int numberOfItineraries)
  {
    // Please implement your own (safe) version that uses prepared statements rather than string concatenation.
    // You may use the `Flight` class (defined above).
    //return transaction_search_unsafe(originCity, destinationCity, directFlight, dayOfMonth, numberOfItineraries);

    StringBuffer sb = new StringBuffer();
    itArray = new Itinerary[numberOfItineraries];

    try {
      int count = 0;
      // df for direct flights
      String dfQuery = "SELECT TOP (?) fid,day_of_month,carrier_id,flight_num,origin_city,dest_city,actual_time,capacity,price "
      + "FROM Flights "
      + "WHERE origin_city = ? AND dest_city = ? AND day_of_month = ? AND canceled = 0 "
      + "ORDER BY actual_time ASC, fid ASC";
      PreparedStatement ps = conn.prepareStatement(dfQuery);

      ps.clearParameters();
      ps.setInt(1, numberOfItineraries);
      ps.setString(2, originCity);
      ps.setString(3, destinationCity);
      ps.setInt(4, dayOfMonth);
      
      ResultSet df = ps.executeQuery();

      while (df.next()) {
        int result_fid = df.getInt("fid");
        //System.out.println(result_fid);
        int result_dayOfMonth = df.getInt("day_of_month");
        String result_carrierId = df.getString("carrier_id");
        String result_flightNum = df.getString("flight_num");
        String result_originCity = df.getString("origin_city");
        String result_destCity = df.getString("dest_city");
        int result_time = df.getInt("actual_time");
        int result_capacity = df.getInt("capacity");
        int result_price = df.getInt("price");

        sb.append("Itinerary ").append(count)
          .append(": 1 flight(s), ").append(result_time)
          .append(" minutes\n")
          .append("ID: ").append(result_fid)
          .append(" Day: ").append(result_dayOfMonth)
          .append(" Carrier: ").append(result_carrierId)
          .append(" Number: ").append(result_flightNum)
          .append(" Origin: ").append(result_originCity)
          .append(" Dest: ").append(result_destCity)
          .append(" Duration: ").append(result_time)
          .append(" Capacity: ").append(result_capacity)
          .append(" Price: ").append(result_price)
          .append("\n");

        // set -1 to both fid2, cap2 since they are "null" in direct flights
        itArray[count] = new Itinerary(count, result_fid, -1, result_price, result_dayOfMonth);

        count++;
      }
      df.close();

      boolean executeIndf = count < numberOfItineraries & !directFlight;
      if (executeIndf) {
        int totalIndf = numberOfItineraries - count;
        String indfQuery = "SELECT TOP (?) F1.fid,F1.day_of_month,F1.carrier_id,F1.flight_num,F1.origin_city,F1.dest_city,F1.actual_time,F1.capacity,F1.price,"
        + "F2.fid,F2.carrier_id,F2.flight_num,F2.origin_city,F2.dest_city,F2.actual_time,F2.capacity,F2.price "
        + "FROM Flights AS F1, Flights AS F2 "
        + "WHERE F1.origin_city = ? AND F1.dest_city = F2.origin_city AND F2.dest_city = ? "
        + "AND F1.day_of_month = F2.day_of_month AND F1.month_id = F2.month_id AND F1.day_of_month = ? "
        + "AND F1.canceled = 0 AND F2.canceled = 0 "
        + "ORDER BY (F1.actual_time + F2.actual_time) ASC, F1.fid ASC, F2.fid ASC";
        
        ps = conn.prepareStatement(indfQuery);
        ps.clearParameters();
        ps.setInt(1, totalIndf);
        ps.setString(2, originCity);
        ps.setString(3, destinationCity);
        ps.setInt(4, dayOfMonth);
        
        ResultSet indf = ps.executeQuery();

        while (indf.next()) {
          int r_fid1 = indf.getInt(1);
          int r_fid2 = indf.getInt(10);
          int r_dayOfMonth = indf.getInt(2);
          String r_carrier1 = indf.getString(3);
          String r_carrier2 = indf.getString(11);
          String r_flightNum1 = indf.getString(4);
          String r_flightNum2 = indf.getString(12);
          String r_org1 = indf.getString(5);
          String r_org2 = indf.getString(13);
          String r_dest1 = indf.getString(6);
          String r_dest2 = indf.getString(14);
          int r_time1 = indf.getInt(7);
          int r_time2 = indf.getInt(15);
          int r_cap1 = indf.getInt(8);
          int r_cap2 = indf.getInt(16);
          int r_price1 = indf.getInt(9);
          int r_price2 = indf.getInt(17);

          sb.append("Itinerary ").append(count)
          .append(": 2 flight(s), ").append(r_time1 + r_time2)
          .append(" minutes\n")
          .append("ID: ").append(r_fid1)
          .append(" Day: ").append(r_dayOfMonth)
          .append(" Carrier: ").append(r_carrier1)
          .append(" Number: ").append(r_flightNum1)
          .append(" Origin: ").append(r_org1)
          .append(" Dest: ").append(r_dest1)
          .append(" Duration: ").append(r_time1)
          .append(" Capacity: ").append(r_cap1)
          .append(" Price: ").append(r_price1)
          .append("\n")
          .append("ID: ").append(r_fid2)
          .append(" Day: ").append(r_dayOfMonth)
          .append(" Carrier: ").append(r_carrier2)
          .append(" Number: ").append(r_flightNum2)
          .append(" Origin: ").append(r_org2)
          .append(" Dest: ").append(r_dest2)
          .append(" Duration: ").append(r_time2)
          .append(" Capacity: ").append(r_cap2)
          .append(" Price: ").append(r_price2)
          .append("\n");

          itArray[count] = new Itinerary(count, r_fid1, r_fid2, r_price1 + r_price2, r_dayOfMonth);

          count++;
        }
        indf.close();
      }

      if (count == 0) {
        sb.append("No flights match your selection\n");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  /**
   * Same as {@code transaction_search} except that it only performs single hop search and
   * do it in an unsafe manner.
   *
   * @param originCity
   * @param destinationCity
   * @param directFlight
   * @param dayOfMonth
   * @param numberOfItineraries
   *
   * @return The search results. Note that this implementation *does not conform* to the format required by
   * {@code transaction_search}.
   */
  private String transaction_search_unsafe(String originCity, String destinationCity, boolean directFlight,
                                          int dayOfMonth, int numberOfItineraries)
  {
    StringBuffer sb = new StringBuffer();

    try
    {
      // one hop itineraries
      String unsafeSearchSQL =
              "SELECT TOP (" + numberOfItineraries + ") day_of_month,carrier_id,flight_num,origin_city,dest_city,actual_time,capacity,price "
                      + "FROM Flights "
                      + "WHERE origin_city = \'" + originCity + "\' AND dest_city = \'" + destinationCity + "\' AND day_of_month =  " + dayOfMonth + " "
                      + "ORDER BY actual_time ASC";

      Statement searchStatement = conn.createStatement();
      ResultSet df = searchStatement.executeQuery(unsafeSearchSQL);

      while (df.next())
      {
        int result_dayOfMonth = df.getInt("day_of_month");
        String result_carrierId = df.getString("carrier_id");
        String result_flightNum = df.getString("flight_num");
        String result_originCity = df.getString("origin_city");
        String result_destCity = df.getString("dest_city");
        int result_time = df.getInt("actual_time");
        int result_capacity = df.getInt("capacity");
        int result_price = df.getInt("price");

        sb.append("Day: ").append(result_dayOfMonth)
                .append(" Carrier: ").append(result_carrierId)
                .append(" Number: ").append(result_flightNum)
                .append(" Origin: ").append(result_originCity)
                .append(" Destination: ").append(result_destCity)
                .append(" Duration: ").append(result_time)
                .append(" Capacity: ").append(result_capacity)
                .append(" Price: ").append(result_price)
                .append('\n');
      }
      df.close();
    } catch (SQLException e) { e.printStackTrace(); }

    return sb.toString();
  }



  /**
   * Shows an example of using PreparedStatements after setting arguments.
   * You don't need to use this method if you don't want to.
   */
  private int checkFlightCapacity(int fid) throws SQLException
  {
    checkFlightCapacityStatement.clearParameters();
    checkFlightCapacityStatement.setInt(1, fid);
    ResultSet results = checkFlightCapacityStatement.executeQuery();
    results.next();
    int capacity = results.getInt("capacity");
    results.close();

    return capacity;
  }
}
