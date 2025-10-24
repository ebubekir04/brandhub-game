package GamePiece

import Grid.Grid

/**
 * Dit is een subklasse van 'GamePiece'. Een koning heeft altijd de rol
 * van Verdediger.
 */

class King(id: Int = GamePiece.generateId()) extends GamePiece(PlayerRole.Defender, id):
  override protected def getPotentialMoves(startRow: Int, startCol: Int, grid: Grid[GamePiece]): List[(Int, Int)] =
    val directions = List((-1, 0), (1, 0), (0, -1), (0, 1))
    scanDirections(startRow, startCol, directions, grid)
