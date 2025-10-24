// Pad: Magic/XJumpAndDiagonalMove.scala
package Magic

import GamePiece.GamePiece

/**
 * Een gecombineerde power-up.
 * Deze trait erft van beide andere traits en combineert zo hun gedrag.
 * De body kan leeg blijven, Scala doet alles zelf.
 */
trait XJumpAndDiagonalMove extends Jump with DiagonalMove