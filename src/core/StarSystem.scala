package core

import java.util.UUID

/**
  * Created by dan-s on 24/05/2016.
  */
class StarSystem(private var _owner: Faction,
                 val x: Int,
                 val y: Int,
                 private var _income: Int,
                 val name: String,
                 val id: UUID) {
  def owner: Faction = _owner
  def income: Int = _income

  def setOwner(faction: Faction): Unit = {
    _owner = faction
  }

  def setIncome(newIncome: Int): Unit = {
    _income = newIncome
  }

  override def toString = s"StarSystem $name"
}
