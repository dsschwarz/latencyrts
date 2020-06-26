package core

/**
  * Created by dan-s on 28/05/2016.
  */
object Helpers {
  def distance(x1: Double, x2: Double, y1: Double, y2: Double): Double = {
    math.sqrt(math.pow(x1-x2, 2) + math.pow(y1-y2, 2)) // scale it up
  }
}
