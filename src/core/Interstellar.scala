package core

case class Interstellar(from: StarSystem, to: StarSystem) {
  var progress = 0d

  def update(ms: Int): Unit = {
    progress += speed*ms/distance
  }

  def distance: Double = {
    Helpers.distance(from.x, to.x, from.y, to.y)
  }

  def speed: Double = 0.05d // distance travelled per millisecond
}