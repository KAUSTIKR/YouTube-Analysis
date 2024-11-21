import org.apache.spark.sql.SparkSession

object CsvToMySQL {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("CSV to MySQL")
      .master("local[*]")
      .getOrCreate()

    val csvFilePath = "D:/GWU_World/Practice/YouTubeAnalysis.csv"

    val df = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(csvFilePath)

    println("Schema of the DataFrame:")
    df.printSchema()

    println("Data in the DataFrame:")
    df.show(5)

    val jdbcUrl = "jdbc:mysql://localhost:3306/YoutubeStatsDb"
    val connectionProperties = new java.util.Properties()
    connectionProperties.setProperty("user", "root")
    connectionProperties.setProperty("password", "Dako@1551")
    connectionProperties.setProperty("driver", "com.mysql.cj.jdbc.Driver")

    try {
      df.write
        .mode("append")
        .jdbc(jdbcUrl, "YoutubeStatsDb", connectionProperties)
      println("Data written successfully to the database!")
    } catch {
      case e: Exception =>
        println("Error occurred while writing to MySQL:")
        e.printStackTrace()
    }
  }
}
