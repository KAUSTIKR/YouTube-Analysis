//import sttp.client3._
//import play.api.libs.json._
//import java.io._
//import scala.io.Source
//
//case class VideoDetails1(
//                          videoId: String,
//                          title: String,
//                          viewCount: String,
//                          likeCount: String,
//                          commentCount: String
//                        )
//
//object YouTubeVideoFetcher1 {
//
//  // Replace with your YouTube API key
//  val apiKey: String = "AIzaSyBMSXAQyw_4tgvZD4itI0cTO7FffgV7LJo"
//
//  // Function to fetch video details by video ID (one by one)
//  def fetchVideoDetails1(videoId: String): Option[VideoDetails1] = {
//    val baseUrl = "https://www.googleapis.com/youtube/v3/videos"
//
//    val params = Map(
//      "part" -> "snippet,statistics",
//      "id" -> videoId,
//      "key" -> apiKey
//    )
//
//    val request = basicRequest
//      .get(uri"$baseUrl?$params")
//      .response(asString)
//
//    val backend = HttpClientSyncBackend()
//
//    val response = try {
//      request.send(backend)
//    } catch {
//      case ex: Exception =>
//        println(s"Error fetching video details for $videoId: ${ex.getMessage}")
//        backend.close()
//        return None
//    }
//
//    backend.close()
//
//    if (response.code.isSuccess) {
//      val json = Json.parse(response.body.getOrElse(""))
//      val items = (json \ "items").asOpt[JsArray].getOrElse(JsArray())
//
//      if (items.value.isEmpty) {
//        println(s"No details found for video ID: $videoId")
//        None
//      } else {
//        val item = items(0)
//        val snippet = (item \ "snippet")
//        val statistics = (item \ "statistics")
//
//        Some(VideoDetails1(
//          videoId = (item \ "id").as[String],
//          title = (snippet \ "title").asOpt[String].getOrElse("No Title"),
//          viewCount = (statistics \ "viewCount").asOpt[String].getOrElse("0"),
//          likeCount = (statistics \ "likeCount").asOpt[String].getOrElse("0"),
//          commentCount = (statistics \ "commentCount").asOpt[String].getOrElse("0")
//        ))
//      }
//    } else {
//      println(s"Failed to fetch video details for $videoId: ${response.statusText}")
//      None
//    }
//  }
//
//  // Write video details to CSV
//  def writeToCSV(video: VideoDetails1, category: String, writer: BufferedWriter): Unit = {
//    writer.write(
//      s"""\n"${video.videoId}","${video.title.replace("\"", "\"\"")}","${video.viewCount}","${video.likeCount}","${video.commentCount}","$category""""
//    )
//    writer.flush()  // Ensure that data is written to the file immediately
//  }
//
//  // Function to read video IDs from a file
//  def readVideoIdsFromFile(filePath: String): Seq[String] = {
//    val source = Source.fromFile(filePath)
//    try {
//      source.getLines().toSeq.filter(_.nonEmpty) // Read lines and ignore empty ones
//    } finally {
//      source.close()
//    }
//  }
//
//  // Function to process and save video details for each category
//  def processCategoryAndSave(category: String, path: String, writer: BufferedWriter): Unit = {
//    try {
//      println(s"Processing category: $category, File: $path")
//      val videoIds = readVideoIdsFromFile(path)
//      videoIds.foreach { id =>
//        try {
//          fetchVideoDetails1(id) match {
//            case Some(video) =>
//              writeToCSV(video, category, writer)
//              println(s"Successfully written details for video ID: $id in category: $category")
//            case None =>
//              println(s"No details found for video ID: $id in category: $category")
//          }
//        } catch {
//          case e: Exception =>
//            println(s"Error processing video ID $id: ${e.getMessage}")
//        }
//      }
//    } catch {
//      case e: Exception =>
//        println(s"Error reading file $path for category $category: ${e.getMessage}")
//    }
//  }
//
//  // Main function to run the task every 15 seconds
//  def main(args: Array[String]): Unit = {
//    val filePaths = Map(
//      "Music" -> "D:/GWU_World/Practice/Music.txt",
//      "Gaming" -> "D:/GWU_World/Practice/Gaming.txt",
//      "Technology" -> "D:/GWU_World/Practice/Technology.txt",
//      "Entertainment" -> "D:/GWU_World/Practice/Entertainment.txt",
//      "Sports" -> "D:/GWU_World/Practice/Sports.txt",
//      "News" -> "D:/GWU_World/Practice/News.txt"
//    )
//
//    val fileName = "YouTubeData3.csv"
//    val file = new File(fileName)
//
//    // Run the task periodically every 15 seconds using do-while loop
//    do {
//      println("Starting a new cycle of fetching and saving video details...")
//
//      // Delete the file if it exists (start fresh)
////      if (file.exists()) {
////        if (file.delete()) {
////          println(s"Deleted existing file: $fileName")
////        } else {
////          println(s"Failed to delete existing file: $fileName")
////        }
////      }
//
//      // Open the file in overwrite mode (use 'false' to ensure we don't append)
//      val writer = new BufferedWriter(new FileWriter(file, false)) // Overwrite mode
//
//      try {
//        // Write the header only if the file is empty
//        if (file.length() == 0) {
//          writer.write("Video ID,Title,View Count,Like Count,Comment Count,Category") // Description, Published At, Channel Title
//        }
//
//        // Process each category and its corresponding video IDs file
//        filePaths.foreach { case (category, path) =>
//          processCategoryAndSave(category, path, writer)
//        }
//
//        // Sleep for 15 seconds before the next iteration
//        println("Sleeping for 15 seconds...")
//        Thread.sleep(15000) // Sleep for 15 seconds
//
//      } catch {
//        case e: Exception =>
//          println(s"Error during processing: ${e.getMessage}")
//      } finally {
//        // Ensure writer is flushed and closed after each cycle and eventually closed
//        writer.flush()
//        writer.close()
//        println(s"All video details saved to $fileName")
//      }
//
//    } while (true) // Infinite loop to keep repeating the task
//  }
//}
