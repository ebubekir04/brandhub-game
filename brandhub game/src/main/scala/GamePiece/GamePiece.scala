package GamePiece

import Grid.Grid
import Magic.PowerUp
import GameBasis.GameConstants
import scala.collection.mutable.ListBuffer

/**
 * Companion object voor de GamePiece klasse.
 * Wordt gebruikt om statische functionaliteit te beheren die gerelateerd is aan
 * alle GamePieces, maar niet aan een specifieke instantie. 
 * In dit geval wordt het gebruikt voor het genereren van unieke, oplopende ID's.
 * Een voorbeeld die we ook in de wpos hebben behandeld (vraag over Studenten)
 */

object GamePiece:
  
  // private teller die de volgende beschikbare ID bijhoudt
  private var nextId: Int = 0

  // methode die de teller verhoogt en de nieuwe ID geeft
  def generateId(): Int =
    val id = nextId
    nextId += 1
    id


// deze klasse definieert de gemeenschappelijke eigenschappen en het gedrag van elk stuk
abstract class GamePiece(val role: PlayerRole, val id: Int = GamePiece.generateId()):
  
  // een lijst die alle actieve powerups voor dit stuk bijhoudt
  var activePowerUps: List[PowerUp] = List.empty
  
  // deze methode activeert een power-up op voor dit specifiek stuk
  def activatePowerUp(powerUp: PowerUp): Unit =
  
    // voeg alleen toe als de power-up nog niet in de lijst staat om duplicaten te vermijden
    if !activePowerUps.contains(powerUp) then
      activePowerUps = powerUp :: activePowerUps
  
  // berekent de geldige zetten voor dit piece vanaf een gegeven positie
  // door final te gebruiken garanderen we dat geen enkel subklasse deze methode kan veranderen
  final def getValidMoves(startRow: Int, startCol: Int, grid: Grid[GamePiece]): List[(Int, Int)] =
  
    //roept getPotentialMoves aan om de basiszetten op te halen en vervolgens filtratie van ongeldige zetten zoals KoningTroon
    val potentialMoves = getPotentialMoves(startRow, startCol, grid)
    val centralThrone = (GameConstants.NUM_ROWS - 2) / 2
    val centerY = GameConstants.NUM_COLS / 2
    potentialMoves.filter(move => move != (centralThrone, centerY))


  // deze methode berekent de basiswegeingen voor dit piece 
  // abstract methode die door subklassen (soldier, king) vervolledigd moet worden 
  protected def getPotentialMoves(startRow: Int, startCol: Int, grid: Grid[GamePiece]): List[(Int, Int)]
  
  
  // Eeen Herbruikbare helper methode om in een lijst van richtingen te zoeken naar lege vakjes 
  protected def scanDirections(startRow: Int, startCol: Int, directions: List[(Int, Int)], grid: Grid[GamePiece]): List[(Int, Int)] =
    val moves = ListBuffer[(Int, Int)]()
    for (dr, dc) <- directions do
      var currentRow = startRow + dr
      var currentCol = startCol + dc
      var blocked = false
      while grid.isValid(currentRow, currentCol) && !blocked do
        grid.get(currentRow, currentCol) match
          //zodra er een piece op ons pad komt, is het gaan voor die richting, anders verder
          case Some(_) => blocked = true
          case None =>
            moves += ((currentRow, currentCol))
            currentRow += dr
            currentCol += dc
    moves.toList
