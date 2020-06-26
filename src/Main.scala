import metagame.{LocalGame, RemoteGame}
import org.newdawn.slick.AppGameContainer

object Main extends App {
  val app = new AppGameContainer(new RemoteGame())
  app.setDisplayMode(800, 600, false)
  app.setTargetFrameRate(60)
  app.start()
}