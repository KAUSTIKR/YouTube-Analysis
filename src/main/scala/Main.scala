import sttp.client3._
import sttp.client3.akkahttp.AkkaHttpBackend
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._

// Define case classes for video stats
case class VideoStatistics(viewCount: Long, likeCount: Long, dislikeCount: Long)

object YouTubeAPI {
  // Define your API key
  val apiKey = "AIzaSyBEMd66V_1iPJJlnI18EX-eRKjPgalZdvU"

  // Initialize the HTTP backend
  implicit val backend: SttpBackend[Future, Any] = AkkaHttpBackend()

  def fetchVideoStats(videoId: String): Future[VideoStatistics] = {
    val uri = uri"https://www.googleapis.com/youtube/v3/videos?part=statistics&id=$videoId&key=$apiKey"

    val request = basicRequest.get(uri)

    // Send the request asynchronously
    val response = request.send(backend)

    response.map(res => {
      val statsJson = res.body match {
        case Right(jsonStr) => Json.parse(jsonStr)
        case Left(error) => throw new Exception(s"Error fetching data: $error")
      }
      val items = (statsJson \ "items").as[JsArray].value
      if (items.nonEmpty) {
        val item = items.head
        val viewCount = (item \ "statistics" \ "viewCount").asOpt[String].map(_.toLong).getOrElse(0L)
        val likeCount = (item \ "statistics" \ "likeCount").asOpt[String].map(_.toLong).getOrElse(0L)
        val dislikeCount = (item \ "statistics" \ "dislikeCount").asOpt[String].map(_.toLong).getOrElse(0L)

        VideoStatistics(viewCount, likeCount, dislikeCount)
      }
      else {
        throw new Exception("No video statistics found.")
      }
    }
    )
  }
}

 object Main{
  def main(args: Array[String]) : Unit = {
    val videoId = "dQw4w9WgXcQ" // Replace with the actual video ID you want to fetch stats for

    // Fetch video statistics
    val statsFuture = YouTubeAPI.fetchVideoStats(videoId)

    // Wait for the result (blocking call for simplicity)
    val stats = Await.result(statsFuture, 10.seconds)

    // Print the statistics
    println(s"Video Statistics for ID $videoId:")
    println(s"Views: ${stats.viewCount}")
    println(s"Likes: ${stats.likeCount}")
    println(s"Dislikes: ${stats.dislikeCount}")
  }
}


