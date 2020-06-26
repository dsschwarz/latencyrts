package metagame

import java.io.File

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import client.ui.{HomeScreenUserInterface, InGameUserInterface, PreGameUserInterface, UserInterface}
import client.{ClientGame, PlayerEngine}
import com.typesafe.config.ConfigFactory
import networking._
import networking.dto.{BeginResponse, PreGameResponse}
import org.asynchttpclient.AsyncHttpClientConfig
import org.newdawn.slick.{BasicGame, GameContainer, Graphics}
import play.api._
import play.api.libs.ws._
import play.api.libs.ws.ahc.{AhcConfigBuilder, AhcWSClient, AhcWSClientConfig}
/**
  * Connects to a remote server
  */
class RemoteGame() extends BasicGame("Latency RTS") with ClientGame {
  val wsClient = {
    val configuration = Configuration.reference ++ Configuration(ConfigFactory.parseString(
      """
        |ws.followRedirects = true
      """.stripMargin))

    // If running in Play, environment should be injected
    val environment = Environment(new File("."), this.getClass.getClassLoader, Mode.Prod)

    val parser = new WSConfigParser(configuration, environment)
    val config = new AhcWSClientConfig(wsClientConfig = parser.parse())
    val builder = new AhcConfigBuilder(config)
    val logging = new AsyncHttpClientConfig.AdditionalChannelInitializer() {
      override def initChannel(channel: io.netty.channel.Channel): Unit = {
        channel.pipeline.addFirst("log", new io.netty.handler.logging.LoggingHandler("debug"))
      }
    }
    val ahcBuilder = builder.configure()
    ahcBuilder.setHttpAdditionalChannelInitializer(logging)
    val ahcConfig = ahcBuilder.build()
    implicit val system = ActorSystem("MyActorSystem")
    implicit val materializer = ActorMaterializer()
    new AhcWSClient(ahcConfig)
  }

  override def update(gameContainer: GameContainer, i: Int): Unit = {}

  override protected def createHomeScreenComm(): HomeScreenCommunication = {
    new HomeScreenCommImpl(wsClient)
  }

  override def createPreGameComm(response: PreGameResponse): PreGameCommunication = {
    new PreGameCommImpl(wsClient, response.gameId, response.faction.id)
  }

  override def createInGameComm(response: BeginResponse): InGameCommunication = {
    val systems = response.systems.map(_.toStarSystem(response.factions))
    new InGameCommunicationImpl(
      response.gameId,
      response.factions.head.id,  // we know that the first faction is the one assigned to the current player
      systems,
      wsClient
    )
  }

  override def closeRequested(): Boolean = {
    wsClient.close()
    super.closeRequested()
  }
}
