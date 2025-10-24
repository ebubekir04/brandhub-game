package GameBasis

import Grid.Grid
import GamePiece.*
import PlayerAttributes.Player

/**
 * Een  object met de verantwoordelijkheid voor het opzetten van het spelbord.
 * Dit object centraliseert de logica voor het plaatsen van alle spelstukken
 * in hun initiële posities volgens de regels van Brandhub.
 * Er zal maar 1 instantatie zijn dus daarom voor een object gekozen
 */
object BoardSetUp: 

  // methode die de stukken van de aanvaller en verdediger op het grid in de kruisopstelling plaatst
  def placePieces(grid: Grid[GamePiece], attacker: Player, defender: Player): Unit =
  
    // bepaal het centrum van het speelbare veld (de UI-rij voor de buttons wordt niet mee gerekend)
    val centerX = (GameConstants.NUM_ROWS - 2) / 2
    val centerY = GameConstants.NUM_COLS / 2

    // haal de koning en de soldaten op uit de lijst van de verdediger.
    val king = defender.pieces.collectFirst { case k: King => k }.get
    val defenderSoldiers = defender.pieces.collect { case s: Soldier => s }

    // Plaats de stukken van de verdediger rond het centrum
    grid.set(centerX, centerY, king)
    grid.set(centerX - 1, centerY, defenderSoldiers(0))
    grid.set(centerX + 1, centerY, defenderSoldiers(1))
    grid.set(centerX, centerY - 1, defenderSoldiers(2))
    grid.set(centerX, centerY + 1, defenderSoldiers(3))

    // haal de soldaten op uit de lijst van de aanvaller
    val attackerSoldiers = attacker.pieces.collect { case s: Soldier => s }

    // plaats de stukken van de aanvaller in de buitenste ring van de kruisopstelling
    grid.set(centerX - 3, centerY, attackerSoldiers(0))
    grid.set(centerX - 2, centerY, attackerSoldiers(1))
    grid.set(centerX + 2, centerY, attackerSoldiers(2))
    grid.set(centerX + 3, centerY, attackerSoldiers(3))
    grid.set(centerX, centerY - 3, attackerSoldiers(4))
    grid.set(centerX, centerY - 2, attackerSoldiers(5))
    grid.set(centerX, centerY + 2, attackerSoldiers(6))
    grid.set(centerX, centerY + 3, attackerSoldiers(7))
