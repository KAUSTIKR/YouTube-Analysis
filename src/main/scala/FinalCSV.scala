import java.io._
import scala.io.Source
import java.nio.file.{Files, Paths}

object MergeCSVFiles {
  def main(args: Array[String]): Unit = {
    val directoryPath = "D/GWU_World/Practice"  // Replace with your actual directory path
    val outputFile = "merged_output.csv"        // Output file name

    // Get all CSV files in the directory
    val csvFiles = new File(directoryPath).listFiles.filter(_.getName.endsWith(".csv"))

    if (csvFiles.isEmpty) {
      println(s"No CSV files found in the directory: $directoryPath")
      return
    }

    // Initialize the output file for writing
    val writer = new BufferedWriter(new FileWriter(outputFile))
    var headerWritten = false

    // Iterate through each CSV file
    csvFiles.foreach { file =>
      val source = Source.fromFile(file)

      // Read each line from the file
      val lines = source.getLines().toList
      source.close()

      // If the header hasn't been written yet, write it
      if (!headerWritten) {
        // The header is the first line of the first CSV file
        writer.write(lines.head + "\n")  // Write the header
        headerWritten = true
      }

      // Write the content (skip the header)
      lines.tail.foreach { line =>
        writer.write(line + "\n")
      }
    }

    // Close the writer after all files are processed
    writer.close()
    println(s"Merged CSV has been saved to: $outputFile")
  }
}
