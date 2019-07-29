import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.*;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Collections;

class TupleComparator implements Comparator<Tuple2<String, Integer>>, Serializable {
  private static final long serialVersionUID = 1L;
  @Override
  public int compare(Tuple2<String, Integer> t1, Tuple2<String, Integer> t2) {
    return t1._2().compareTo(t2._2());
  }
}

public class HW6 {

  // the full input data file is at s3://us-east-1.elasticmapreduce.samples/flightdata/input

  /*
    You are free to change the contents of main as much as you want. We will be running a separate main for
     grading purposes.
   */
  public static void main(String[] args) {

    if (args.length < 2)
      throw new RuntimeException("Usage: HW6 <datafile location> <output location>");

    String dataFile = args[0];
    String output = args[1];

    // turn off logging except for error messages
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR);
    Logger.getLogger("org.apache.spark.storage.BlockManager").setLevel(Level.ERROR);

    // use this for running locally
    SparkSession spark = SparkSession.builder().appName("HW6").config("spark.master", "local").getOrCreate();

    // use this for running on ec2
    //SparkSession spark = SparkSession.builder().appName("HW6").getOrCreate();

    //Dataset<Row> r = warmup(spark, dataFile);
    //r.javaRDD().repartition(1).saveAsTextFile(output);

    // uncomment each of the below to run your solutions

    /* Problem 1 */
    //Dataset<Row> r1 = Q1(spark, dataFile);

    // collect all outputs from different machines to a single partition, and write to the output
    // make sure the output location does not already exists (otherwise it will throw an error)
    //r1.javaRDD().repartition(1).saveAsTextFile(output);

    /* Problem 2 */
    //JavaRDD<Row> r2 = Q2(spark, dataFile);
    //r2.repartition(1).saveAsTextFile(output);

    /* Problem 3 */
    //JavaPairRDD<Tuple2<String, Integer>, Integer> r3 = Q3(spark, dataFile);
    //r3.repartition(1).saveAsTextFile(output);

    /* Problem 4 */
    //Tuple2<String, Integer> r4 = Q4(spark, dataFile);
    //spark.createDataset(Collections.singletonList(r4), Encoders.tuple(Encoders.STRING(), Encoders.INT()))
    //     .javaRDD().saveAsTextFile(output);

    /* Problem 5 */
    JavaPairRDD<String, Double> r5 = Q5(spark, dataFile);
    r5.repartition(1).saveAsTextFile(output);


    // this saves the results to an output file in parquet format
    // (useful if you want to generate a test dataset on an even smaller dataset)
    // r.repartition(1).write().parquet(output);

    // shut down
    spark.stop();
  }

  // offsets into each Row from the input data read
  public static final int MONTH = 2;
  public static final int ORIGIN_CITY_NAME = 15;
  public static final int DEST_CITY_NAME = 24;
  public static final int DEP_DELAY = 32;
  public static final int CANCELLED = 47;

  public static Dataset<Row> warmup (SparkSession spark, String dataFile) {

    Dataset<Row> df = spark.read().parquet(dataFile);

    // create a temporary table based on the data that we read
    df.createOrReplaceTempView("flights");

    // run a SQL query
    Dataset<Row> r = spark.sql("SELECT * FROM flights LIMIT 10");

    // this prints out the results
    r.show();

    // this uses the RDD API to project a column from the read data and print out the results
    r.javaRDD()
     .map(t -> t.get(DEST_CITY_NAME))
     .foreach(t -> System.out.println(t));

    return r;
  }

  public static Dataset<Row> Q1 (SparkSession spark, String dataFile) {

    Dataset<Row> df = spark.read().parquet(dataFile);

    // create a temporary table based on the data that we read
    df.createOrReplaceTempView("flights");

    // write SQL query to get all distinct dest cities that are reached from Seattle, WA
    Dataset<Row> r = spark.sql(
      "SELECT DISTINCT F.destcityname FROM flights F WHERE F.origincityname = 'Seattle, WA'");

    return r;
  }

  public static JavaRDD<Row> Q2 (SparkSession spark, String dataFile) {
    // your code here
    JavaRDD<Row> d = spark.read().parquet(dataFile).javaRDD();

    // filter all the flights from Seattle, WA
    JavaRDD<Row> Seattle = d.filter(r -> r.get(ORIGIN_CITY_NAME).toString().equals("Seattle, WA"));

    // get the dest cities
    Seattle = Seattle.map(r -> RowFactory.create(r.get(DEST_CITY_NAME)));
    
    // get the distinct dest cities
    return Seattle.distinct();
  }

  public static JavaPairRDD<Tuple2<String, Integer>, Integer> Q3 (SparkSession spark, String dataFile) {

    JavaRDD<Row> d = spark.read().parquet(dataFile).javaRDD();

    // filter out the cancelled flights
    JavaRDD<Row> uncancelled = d.filter(r -> r.getInt(CANCELLED) == 0);

    // create key, value JavaPairRDD where key is <org city, month>, and value is the count enum
    JavaPairRDD<Tuple2<String, Integer>, Integer> data = uncancelled.mapToPair(r -> new Tuple2<Tuple2<String, Integer>, Integer>(new Tuple2<String, Integer>(r.get(ORIGIN_CITY_NAME).toString(), r.getInt(MONTH)), 1));

    // reduce by key by counting the flights in each <org city, month>
    JavaPairRDD<Tuple2<String, Integer>, Integer> result = data.reduceByKey((v1, v2) -> v1 + v2);
    
    return result;
  }

  public static Tuple2<String, Integer> Q4 (SparkSession spark, String dataFile) {

    JavaRDD<Row> d = spark.read().parquet(dataFile).javaRDD();

    // get (org, dest) pair from each flights
    JavaRDD<Tuple2<String, String>> pairs = d.map(r -> new Tuple2<String, String>(r.get(ORIGIN_CITY_NAME).toString(), r.get(DEST_CITY_NAME).toString()));
    
    // keep only the unique pairs
    pairs = pairs.distinct();

    // enumerate each org city
    JavaPairRDD<String, Integer> ones = pairs.mapToPair(r -> new Tuple2<String, Integer>(r._1().toString(), 1));

    // reduce by key by adding(counting) the keys
    JavaPairRDD<String, Integer> counts = ones.reduceByKey((v1, v2) -> v1 + v2);

    // select city with most direct flights
    Tuple2<String, Integer> max = counts.max(new TupleComparator());

    // enumerate each flight to count all the flights from each org city
    JavaPairRDD<String, Integer> flights = d.mapToPair(r -> new Tuple2<String, Integer>(r.get(ORIGIN_CITY_NAME).toString(), 1));

    // reduce by key by counting the number of flights from each org city
    JavaPairRDD<String, Integer> flightsByCity = flights.reduceByKey((v1, v2) -> v1 + v2);

    // filter flightsByCity to get the max city and its number of flights
    // Note: filter() returns a JavaPairRDD, since there should only be one tuple,
    // use first() to retrieve the lone tuple from the filter().
    Tuple2<String, Integer> maxCity = flightsByCity.filter(t -> t._1().compareTo(max._1()) == 0).first();

    return maxCity;
  }

  public static JavaPairRDD<String, Double> Q5 (SparkSession spark, String dataFile) {

    JavaRDD<Row> d = spark.read().parquet(dataFile).javaRDD();

    // filter the flights with non-null depdelay
    JavaRDD<Row> flights = d.filter(r -> r.get(DEP_DELAY) != null);    

    // make key, value JavaPairRDD where key is the org city, and value is a tuple with depdelay and count enum
    JavaPairRDD<String, Tuple2<Integer, Integer>> delays = flights.mapToPair(r -> new Tuple2<String, Tuple2<Integer, Integer>>(r.get(ORIGIN_CITY_NAME).toString(), new Tuple2<Integer, Integer>(r.getInt(DEP_DELAY), 1)));

    // reduce by key by adding up the depdelay and counting the number of flights
    JavaPairRDD<String, Tuple2<Integer, Integer>> total_delays = delays.reduceByKey((v1, v2) -> new Tuple2<Integer, Integer>(v1._1() + v2._1(), v1._2() + v2._2()));

    // calculate average by dividing the total depdelay by the total number of flights
    JavaPairRDD<String, Double> avg_delay = total_delays.mapToPair(t -> new Tuple2<String, Double>(t._1(), t._2()._1() * 1.0 / t._2()._2()));

    return avg_delay;
  }

  /* We list all the fields in the input data file for your reference
  root
 |-- year: integer (nullable = true)   // index 0
 |-- quarter: integer (nullable = true)
 |-- month: integer (nullable = true)
 |-- dayofmonth: integer (nullable = true)
 |-- dayofweek: integer (nullable = true)
 |-- flightdate: string (nullable = true)
 |-- uniquecarrier: string (nullable = true)
 |-- airlineid: integer (nullable = true)
 |-- carrier: string (nullable = true)
 |-- tailnum: string (nullable = true)
 |-- flightnum: integer (nullable = true)
 |-- originairportid: integer (nullable = true)
 |-- originairportseqid: integer (nullable = true)
 |-- origincitymarketid: integer (nullable = true)
 |-- origin: string (nullable = true)   // airport short name
 |-- origincityname: string (nullable = true) // e.g., Seattle, WA
 |-- originstate: string (nullable = true)
 |-- originstatefips: integer (nullable = true)
 |-- originstatename: string (nullable = true)
 |-- originwac: integer (nullable = true)
 |-- destairportid: integer (nullable = true)
 |-- destairportseqid: integer (nullable = true)
 |-- destcitymarketid: integer (nullable = true)
 |-- dest: string (nullable = true)
 |-- destcityname: string (nullable = true)
 |-- deststate: string (nullable = true)
 |-- deststatefips: integer (nullable = true)
 |-- deststatename: string (nullable = true)
 |-- destwac: integer (nullable = true)
 |-- crsdeptime: integer (nullable = true)
 |-- deptime: integer (nullable = true)
 |-- depdelay: integer (nullable = true)
 |-- depdelayminutes: integer (nullable = true)
 |-- depdel15: integer (nullable = true)
 |-- departuredelaygroups: integer (nullable = true)
 |-- deptimeblk: integer (nullable = true)
 |-- taxiout: integer (nullable = true)
 |-- wheelsoff: integer (nullable = true)
 |-- wheelson: integer (nullable = true)
 |-- taxiin: integer (nullable = true)
 |-- crsarrtime: integer (nullable = true)
 |-- arrtime: integer (nullable = true)
 |-- arrdelay: integer (nullable = true)
 |-- arrdelayminutes: integer (nullable = true)
 |-- arrdel15: integer (nullable = true)
 |-- arrivaldelaygroups: integer (nullable = true)
 |-- arrtimeblk: string (nullable = true)
 |-- cancelled: integer (nullable = true)
 |-- cancellationcode: integer (nullable = true)
 |-- diverted: integer (nullable = true)
 |-- crselapsedtime: integer (nullable = true)
 |-- actualelapsedtime: integer (nullable = true)
 |-- airtime: integer (nullable = true)
 |-- flights: integer (nullable = true)
 |-- distance: integer (nullable = true)
 |-- distancegroup: integer (nullable = true)
 |-- carrierdelay: integer (nullable = true)
 |-- weatherdelay: integer (nullable = true)
 |-- nasdelay: integer (nullable = true)
 |-- securitydelay: integer (nullable = true)
 |-- lateaircraftdelay: integer (nullable = true)
 |-- firstdeptime: integer (nullable = true)
 |-- totaladdgtime: integer (nullable = true)
 |-- longestaddgtime: integer (nullable = true)
 |-- divairportlandings: integer (nullable = true)
 |-- divreacheddest: integer (nullable = true)
 |-- divactualelapsedtime: integer (nullable = true)
 |-- divarrdelay: integer (nullable = true)
 |-- divdistance: integer (nullable = true)
 |-- div1airport: integer (nullable = true)
 |-- div1airportid: integer (nullable = true)
 |-- div1airportseqid: integer (nullable = true)
 |-- div1wheelson: integer (nullable = true)
 |-- div1totalgtime: integer (nullable = true)
 |-- div1longestgtime: integer (nullable = true)
 |-- div1wheelsoff: integer (nullable = true)
 |-- div1tailnum: integer (nullable = true)
 |-- div2airport: integer (nullable = true)
 |-- div2airportid: integer (nullable = true)
 |-- div2airportseqid: integer (nullable = true)
 |-- div2wheelson: integer (nullable = true)
 |-- div2totalgtime: integer (nullable = true)
 |-- div2longestgtime: integer (nullable = true)
 |-- div2wheelsoff: integer (nullable = true)
 |-- div2tailnum: integer (nullable = true)
 |-- div3airport: integer (nullable = true)
 |-- div3airportid: integer (nullable = true)
 |-- div3airportseqid: integer (nullable = true)
 |-- div3wheelson: integer (nullable = true)
 |-- div3totalgtime: integer (nullable = true)
 |-- div3longestgtime: integer (nullable = true)
 |-- div3wheelsoff: integer (nullable = true)
 |-- div3tailnum: integer (nullable = true)
 |-- div4airport: integer (nullable = true)
 |-- div4airportid: integer (nullable = true)
 |-- div4airportseqid: integer (nullable = true)
 |-- div4wheelson: integer (nullable = true)
 |-- div4totalgtime: integer (nullable = true)
 |-- div4longestgtime: integer (nullable = true)
 |-- div4wheelsoff: integer (nullable = true)
 |-- div4tailnum: integer (nullable = true)
 |-- div5airport: integer (nullable = true)
 |-- div5airportid: integer (nullable = true)
 |-- div5airportseqid: integer (nullable = true)
 |-- div5wheelson: integer (nullable = true)
 |-- div5totalgtime: integer (nullable = true)
 |-- div5longestgtime: integer (nullable = true)
 |-- div5wheelsoff: integer (nullable = true)
 |-- div5tailnum: integer (nullable = true)
   */
}
