package core

/**
  * Created by dan-s on 13/07/2016.
  */
class EngineClock {
  private var _currentTime = 0l
  def update(msElapsed: Int): Unit = {
    _currentTime += msElapsed
  }

  def time: Long = _currentTime
}
