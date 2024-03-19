import io.circe.Json
import requests._
import io.circe.parser.decode
import io.circe.generic.auto._
import scalaj.http.{Http, HttpResponse}



import scala.util.{Failure, Success}

object CSYE7200API{
  case class AccessTokenResponse(access_token: String, token_type: String, expires_in: Int)
  case class ArtistDetails( followers: Option[Map[String, Int]])
  case class ExternalUrl(spotify: String)

  case class Owner(
                    display_name: String,
                    external_urls: ExternalUrl,
                    href: String,
                    id: String,
                    `type`: String,
                    uri: String
                  )

  case class Image(url: String)

  case class Artist(
                     external_urls: ExternalUrl,
                     href: String,
                     id: String,
                     name: String,
                     `type`: String,
                     uri: String
                   )

  case class Album(
                    album_type: String,
                    artists: List[Artist],
                    available_markets: List[String],
                    external_urls: ExternalUrl,
                    href: String,
                    id: String,
                    images: List[Image],
                    name: String,
                    release_date: String,
                    release_date_precision: String,
                    total_tracks: Int,
                    `type`: String,
                    uri: String
                  )

  case class Track(
                    album: Album,
                    artists: List[Artist],
                    available_markets: List[String],
                    disc_number: Int,
                    duration_ms: Int,
                    explicit: Boolean,
                    external_ids: Map[String, String],
                    external_urls: ExternalUrl,
                    href: String,
                    id: String,
                    is_local: Boolean,
                    name: String,
                    popularity: Int,
                    preview_url: String,
                    track_number: Int,
                    `type`: String,
                    uri: String
                  )

  case class AddedBy(
                      external_urls: ExternalUrl,
                      href: String,
                      id: String,
                      `type`: String,
                      uri: String
                    )

  case class Item(
                   added_at: String,
                   added_by: AddedBy,
                   is_local: Boolean,
                   track: Track
                 )

  case class Tracks(
                     href: String,
                     items: List[Item]
                   )
  case class Followers(
                      href:String,
                      total:Int
                      )
  case class Playlist(
                       collaborative: Boolean,
                       description: String,
                       external_urls: ExternalUrl,
                       followers: Followers,
                       href: String,
                       id: String,
                       images: List[Image],
                       name: String,
                       owner: Owner,
                       primary_color: Option[String],
                       public: Boolean,
                       snapshot_id: String,
                       tracks: Tracks,
                       Ptype:String,
                       uri:String
                     )
  // Function to fetch playlist tracks from Spotify API
  def getPlaylistTracks(playlistId: String,token:String): List[(String,Int)] = {
    val url = s"https://api.spotify.com/v1/playlists/$playlistId"
    val headers = Map("Authorization" -> s"Bearer $token")
    val response = requests.get(url, headers = headers)
    val parsedJson = decode[Playlist](response.text())
    parsedJson match {
      case Right(playlist) =>
        val items = playlist.tracks.items
        val nameAndDurationList = items.map(item => (item.track.name, item.track.duration_ms))
        nameAndDurationList
      case Left(error) =>
        println(s"Error decoding JSON: $error")
        List.empty
    }

  }

  def getArtistDetails(artistId: String,token:String): Option[ArtistDetails] = {
    val url = s"https://api.spotify.com/v1/artists/$artistId"
    val headers = Map("Authorization" -> s"Bearer $token")
    val response = requests.get(url, headers = headers)
    decode[ArtistDetails](response.text).toOption
  }

  def main(args: Array[String]): Unit = {

    val clientID="3cc479a98d414870b92c23fa67578b14"
    val clientSecret="427dceefd35f42cca572108c783340d1"
    val redirectUri = "http://localhost:3000"
    val authEndpoint = "https://accounts.spotify.com/authorize"
    val tokenEndpoint = "https://accounts.spotify.com/api/token"
    val grantType="client_credentials"

    val tokenResponse = requests.post(
      tokenEndpoint,
      data = Map(
        "grant_type" -> "client_credentials",
        "redirect_uri" -> redirectUri,
        "client_id" -> clientID,
        "client_secret" -> clientSecret
      )
    )
    if(tokenResponse.statusCode==200){
  println("Access token:")
  println(tokenResponse.text)
}

    val result = decode[AccessTokenResponse](tokenResponse.text)
    val token: String =  result match {
      case Right(response) =>

        response.access_token
      case Left(error) =>
        println(s"Failed to decode JSON: $error")
        null
    }


    val playlistId = "5Rrf7mqN8uus2AaQQQNdc1/"
    val tracks = getPlaylistTracks(playlistId,token)
println(tracks.length)
    for(song<-tracks)println(song)


    //val top10LongestSongs = tracks.sortBy(-_.duration_ms).take(10)

    println("Part 1:")
    //top10LongestSongs.foreach(track => println(s"${track.name} , ${track.duration_ms}"))

    /*println("\nPart 2:")
    top10LongestSongs.flatMap(_.artists).distinct.foreach { artist =>
      val artistId = artist("id")
      val artistName = artist("name")
      val artistDetails = getArtistDetails(artistId,token)
      artistDetails.foreach { details =>
        val followerCount = details.followers.flatMap(_.get("total")).getOrElse(0)
        println(s"$artistName : $followerCount")
      }
    }*/
  }
}
