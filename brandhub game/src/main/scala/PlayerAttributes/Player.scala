package PlayerAttributes

import GamePiece.{GamePiece, PlayerRole}
import Magic.PowerUp
import scala.collection.mutable.ListBuffer

/**
 * Vertegenwoordigt een speler in het spel en beheert diens staat.
 * Deze klasse bevat alle data die bij een speler hoort, zoals de rol, de
 * spelstukken en de beschikbare power-ups.
 */

// om een player aan te maken, moet je zijn role, beginstukken en zijn begin-powerups meegeven
class Player(val role: PlayerRole, initialPieces: List[GamePiece], initialPowerUps: Map[PowerUp, Int]):
  
  // de interne lijst van stukken. De '_' wordt gebruikt om aan te wijzen dat t om een private veld gaat
  private var _pieces: List[GamePiece] = initialPieces
  
  // De interne map die de voorraad powerups bijhoudt
  private var _powerUps: Map[PowerUp, Int] = initialPowerUps

  // dit zijn publieke getters die mogelijkheid beiden om de lijst van pieces te LEZEN
  // door gebruik te maken van private velden in deze methodes, kan niemand ooit de werkelijke lijsten verandern
  def pieces: List[GamePiece] = _pieces
  def powerUps: Map[PowerUp, Int] = _powerUps
  
  
  // de volgende methode zijn de enige 'gecontroleerde' methodes die wel verandering  kunnen brengen aan de lijst van pieces
  //
  //
  
  // een piece toevoegen
  def addPiece(piece: GamePiece): Unit =
    _pieces = piece :: _pieces


  //verwijdert een piece uit de lijst van de speler
  def removePiece(piece: GamePiece): Unit =
  
    // maak een nieuwe, lege en veranderlijke lijst
    val newPieces = ListBuffer[GamePiece]()

    // loop door de oude lijst.
    for p <- _pieces do
      if !(p eq piece) then
        newPieces += p

    // vervang de oude lijst door de nieuwe.
    _pieces = newPieces.toList
  
  
  //vermindert de voorrad van een powerup met -1
  def usePowerup(powerUp: PowerUp): Boolean =
    _powerUps.get(powerUp) match
      case Some(count) if count > 0 =>
        _powerUps = _powerUps.updated(powerUp, count - 1)
        true // als het lukt
      case _ =>
        false // mislukt (power-up niet gevonden of op)
  
  
  // vraagt de voorraad op , of getal boven 0 dus ja er zijn er nog aner False
  def hasPowerUp(powerUp: PowerUp): Boolean =
    _powerUps.get(powerUp) match
      case Some(count) =>
        count > 0
        
      case None =>
        false
  
  // vervangt een oude piece door een nieuw (bv na toepassen van een powerup)
  def replacePiece(oldPiece: GamePiece, newPiece: GamePiece): Unit =
    val newPieces = ListBuffer[GamePiece]()
    for p <- _pieces do
      // voeg het stuk alleen toe als het NIET het oude stuk is
      if !(p eq oldPiece) then
        newPieces += p
    newPieces += newPiece

    // vervang hier ook de oude lijst door de nieuwe
    _pieces = newPieces.toList