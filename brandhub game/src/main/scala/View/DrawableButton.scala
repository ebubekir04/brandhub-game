package View

import java.awt.{Color, Font, Graphics2D}
import Magic.PowerUp

/**
 * Een  implementatie voor het tekenen van een button in de UI-rij.
 */

class DrawableButton(
                      val powerUp: PowerUp,
                      val row: Int,
                      val col: Int,
                      val cellWidth: Int,
                      val cellHeight: Int,
                      val padding: Int
                    ) extends DrawableCell:

  override def draw(g: Graphics2D): Unit =

    // pixel coordinaten bepalen
    val drawX = (col * cellWidth) + padding
    val drawY = (row * cellHeight) + padding

    // teken een achtergrond voor de knop
    g.setColor(Color.PINK)
    g.fillRect(drawX, drawY, cellWidth, cellHeight)

    // Teken een rand
    g.setColor(Color.BLACK)
    g.drawRect(drawX, drawY, cellWidth - 1, cellHeight - 1)

    // haal de eerste letter van de powerup als tekst-icoon
    // voor een meer mooier uitzicht, kon ook images geprint worden
    val text = powerUp.toString.substring(0, 1)
    g.setFont(new Font("Arial", Font.ITALIC, 40))

    // hier wordt gechekt hoe breed en hoog de letter in pixels is om te kunnen centraliseren
    val textWidth = g.getFontMetrics.stringWidth(text)
    val textHeight = g.getFontMetrics.getAscent
    val textX = drawX + (cellWidth - textWidth) / 2
    val textY = drawY + (cellHeight + textHeight) / 2
    g.drawString(text, textX, textY)
