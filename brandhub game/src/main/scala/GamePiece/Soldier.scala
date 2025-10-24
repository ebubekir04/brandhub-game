package GamePiece

import Grid.Grid


/**
 * Dit is een subklasse van 'GamePiece'. Een soldaat kan de rol van zowel Attacker als Verdediger hebben.
 */

class Soldier(role: PlayerRole, id: Int = GamePiece.generateId()) extends GamePiece(role, id):
  override protected def getPotentialMoves(startRow: Int, startCol: Int, grid: Grid[GamePiece]): List[(Int, Int)] =
    val directions = List((-1, 0), (1, 0), (0, -1), (0, 1))
    scanDirections(startRow, startCol, directions, grid)
