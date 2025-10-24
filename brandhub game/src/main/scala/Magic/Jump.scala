package Magic

import GamePiece.GamePiece
import Grid.Grid
import GameBasis.GameConstants.NUM_ROWS
import scala.collection.mutable.ListBuffer

/**
 * Stukken die deze trait mixen, krijgen de mogelijkheid om over andere stukken heen te springen.
 * Dit betekent dat ze naar elk leeg vakje in een rechte horizontale of verticale lijn kunnen bewegen,
 * ongeacht of er andere stukken tussen de start- en eindpositie staan.
 */

trait Jump extends GamePiece:
  /**
   * Overschrijft de basis-bewegingslogica om het springen mogelijk te maken.
   * De methode scant in de vier basisrichtingen (N, Z, W, O) en verzamelt alle lege
   * vakjes binnen het speelbare gedeelte van het bord. In tegenstelling tot de standaard
   * `scanDirections`-methode, stopt deze scan niet wanneer een ander stuk wordt
   * tegengekomen.
   */
  override protected def getPotentialMoves(startRow: Int, startCol: Int, grid: Grid[GamePiece]): List[(Int, Int)] =
    val moves = ListBuffer[(Int, Int)]()
    val directions = List((-1, 0), (1, 0), (0, -1), (0, 1))
    val playableRows = NUM_ROWS - 1 // alleen rijen 0-6 zijn speelbaar in dit geval, laatste rij voor buttons

    for (dr, dc) <- directions do
      var nextRow = startRow + dr
      var nextCol = startCol + dc

      while grid.isValid(nextRow, nextCol) do
        // controleer of de zet binnen het speelbare veld is en of het vakje leeg is
        if nextRow < playableRows && grid.get(nextRow, nextCol).isEmpty then
          moves += ((nextRow, nextCol))

        //ga verder in dezelfde richting, zelfs als er een stuk staat
        nextRow += dr
        nextCol += dc
    moves.toList