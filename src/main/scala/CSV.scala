import sttp.client3._
import play.api.libs.json._
import java.io._

case class VideoDetails(
                         videoId: String,
                         title: String,
                         description: String,
                         publishedAt: String,
                         channelTitle: String,
                         viewCount: String,
                         likeCount: String,
                         commentCount: String
                       )

object YouTubeVideoFetcher {

  val apiKey: String = "AIzaSyBMSXAQyw_4tgvZD4itI0cTO7FffgV7LJo" // Replace with your API key

  // Function to search for video IDs
  def searchVideos(query: String, maxResults: Int = 100): Seq[String] = {
    val baseUrl = "https://www.googleapis.com/youtube/v3/search"
    var videoIds: Seq[String] = Seq()
    var nextPageToken: Option[String] = None
    var resultsFetched = 0

    val backend = HttpClientSyncBackend()

    while (resultsFetched < maxResults) {
      val params = Map(
        "part" -> "id",
        "q" -> query,
        "type" -> "video",
        "maxResults" -> "50",
        "key" -> apiKey
      ) ++ nextPageToken.map("pageToken" -> _)

      val request = basicRequest
        .get(uri"$baseUrl?$params")
        .response(asString)

      val response = request.send(backend)

      if (response.code.isSuccess) {
        val json = Json.parse(response.body.getOrElse(""))
        val items = (json \ "items").as[JsArray].value

        videoIds ++= items.flatMap { item =>
          (item \ "id" \ "videoId").asOpt[String] // Safely extract video ID
        }
        resultsFetched += items.length
        nextPageToken = (json \ "nextPageToken").asOpt[String]

        if (nextPageToken.isEmpty) {
          // No more pages
          return videoIds.take(maxResults)
        }
      } else {
        println(s"Error in search: ${response.statusText}")
        backend.close()
        return videoIds.take(maxResults)
      }
    }

    backend.close()
    videoIds.take(maxResults)
  }

  // Function to fetch video details
  def fetchVideoDetails(videoIds: Seq[String]): Seq[VideoDetails] = {
    val baseUrl = "https://www.googleapis.com/youtube/v3/videos"
    val videoIdParam = videoIds.mkString(",")

    val params = Map(
      "part" -> "snippet,statistics",
      "id" -> videoIdParam,
      "key" -> apiKey
    )

    val request = basicRequest
      .get(uri"$baseUrl?$params")
      .response(asString)

    val backend = HttpClientSyncBackend()
    val response = request.send(backend)
    backend.close()

    if (response.code.isSuccess) {
      val json = Json.parse(response.body.getOrElse(""))
      val items = (json \ "items").as[JsArray].value

      items.flatMap { item =>
        try {
          val snippet = item("snippet")
          val statistics = item("statistics")

          Some(VideoDetails(
            videoId = (item \ "id").as[String],
            title = (snippet \ "title").as[String],
            description = (snippet \ "description").as[String],
            publishedAt = (snippet \ "publishedAt").as[String],
            channelTitle = (snippet \ "channelTitle").as[String],
            viewCount = (statistics \ "viewCount").asOpt[String].getOrElse("0"),
            likeCount = (statistics \ "likeCount").asOpt[String].getOrElse("0"),
            commentCount = (statistics \ "commentCount").asOpt[String].getOrElse("0")
          ))
        } catch {
          case e: Exception =>
            println(s"Skipping record due to missing or malformed data: ${e.getMessage}")
            None // Skip to the next record if data is invalid or missing
        }
      }
    } else {
      println(s"Error in fetching video details: ${response.statusText}")
      Seq.empty[VideoDetails]
    }
  }

  // Write video details to CSV
  def writeToCSV(videos: Seq[VideoDetails], fileName: String): Unit = {
    val file = new File(fileName)
    val writer = new BufferedWriter(new FileWriter(file))

    // Write header
    writer.write("Video ID,Title,Description,Published At,Channel Title,View Count,Like Count,Comment Count\n")

    // Write data
    videos.foreach { video =>
      writer.write(
        s""""${video.videoId}","${video.title.replace("\"", "\"\"")}","${video.description.replace("\"", "\"\"")}","${video.publishedAt}","${video.channelTitle.replace("\"", "\"\"")}","${video.viewCount}","${video.likeCount}","${video.commentCount}""" + "\n"
      )
    }

    writer.close()
    println(s"Data saved to $fileName")
  }

  def main(args: Array[String]): Unit = {
    val query = "Music" // Replace with your desired query

    // Step 1: Search for video IDs
    println("Searching for video IDs...")
    val videoIds = searchVideos(query)

    // Step 2: Fetch video details
    println(s"Fetching details for ${videoIds.length} videos...")
    val videos = fetchVideoDetails(videoIds)

    // Step 3: Save to CSV
    writeToCSV(videos, "video_details.csv")
  }
}
