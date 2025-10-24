package View

import gamelib.AssetsLoader
import java.awt.{Color, Graphics2D}
import GamePiece.{GamePiece, King, PlayerRole, Soldier}

/**
 * Een  implementatie voor het tekenen van een spelstuk (GamePiece).
 */

class DrawableGamePiece(
                         val piece: GamePiece,
                         val row: Int,
                         val col: Int,
                         val cellWidth: Int,
                         val cellHeight: Int,
                         val padding: Int
                       ) extends DrawableCell:

  override def draw(g: Graphics2D): Unit =
    
    //bepaal de kleur van de achtergrond obv de rol van speler
    val pieceColor = piece.role match
      case PlayerRole.Attacker => new Color(139, 69, 19) // bruin
      case PlayerRole.Defender => new Color(255, 215, 0) // goud, geel achtig
    
    // kies de juiste afbeelding obv het type piece
    val imageToDraw = piece match
      case _: King   => AssetsLoader.loadImage("king.png")
      case _: Soldier => AssetsLoader.loadImage("soldier.png")
    
    // pixel coordinaten berekenen
    val drawX = (col * cellWidth) + padding
    val drawY = (row * cellHeight) + padding
  
    // voer de teken operaties uit
    g.setColor(pieceColor)
    g.fillOval(drawX, drawY, cellWidth, cellHeight)
    g.drawImage(imageToDraw, drawX, drawY, cellWidth, cellHeight, null)
