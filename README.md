# YouTube-Analysis
This project analyzes YouTube video data by fetching details through the YouTube Data API v3 and processing it using Apache Spark.

Features
Fetches video metadata (title, view count, like count, comment count) using YouTube API.
Processes and analyzes data using Apache Spark.
Saves processed data to local storage for further use.
Prerequisites
Scala: Version 2.12.18
Apache Spark: Version 3.5
YouTube Data API Key: Obtain an API key from Google Cloud Console.
Dependencies: sttp.client3 
              play.api.libs.json
#Installation#
Clone this repository:
bash
Copy code
git clone <repository-url>
Install dependencies using your preferred build tool (e.g., sbt or Maven).
Usage
Replace the placeholder apiKey in the code with your actual YouTube Data API key.
Compile and run the project:
bash
Copy code
spark-submit --class YouTubeVideoFetcher2 target/scala-X.X/YouTubeAnalysis.jar
Provide video IDs as input to fetch details.
File Structure
YouTubeAnalysis.scala: Main script for fetching and analyzing video data.
License
This project is licensed under the MIT License.
