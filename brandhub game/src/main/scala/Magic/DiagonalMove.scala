package Magic

import GamePiece.GamePiece
import Grid.Grid

/**
 * Stukken die deze trait mixen, krijgen de mogelijkheid om OOK diagonaal te bewegen,
 * naast hun standaard zetten. 
 * Het stapelt nieuw gedrag bovenop bestaand gedrag via een `super`-aanroep.
 */

trait DiagonalMove extends GamePiece:
  /**
   * Breidt de basis-bewegingslogica uit met diagonale zetten.
   * Deze methode gebruikt `abstract override` omdat het de bestaande implementatie van
   * `getPotentialMoves` aanroept via `super` en er vervolgens de diagonale zetten aan toevoegt.
   */
  abstract override protected def getPotentialMoves(startRow: Int, startCol: Int, grid: Grid[GamePiece]): List[(Int, Int)] =
    // haal de bestaande zetten op van de klasse waar deze trait op wordt gestapeld (bv. de standaard zetten van Soldier).
    val standardMoves = super.getPotentialMoves(startRow, startCol, grid)

    // bereken de extra diagonale zetten
    val diagonalDirections = List((-1, -1), (-1, 1), (1, -1), (1, 1))
    val diagonalMoves = scanDirections(startRow, startCol, diagonalDirections, grid)

    // combineer de oude en de nieuwe zetten
    standardMoves ++ diagonalMoves
