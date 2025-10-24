package App

import GameBasis.{Game, GameConstants}
import View.{DrawableCell, DrawableGamePiece, DrawableButton}
import gamelib.GUI
import collection.JavaConverters.seqAsJavaListConverter
import Magic.PowerUp
import GamePiece.*

/**
 * Deze klasse verbindt de spellogica (Model: `Game`) met de grafische weergave (View: `Drawable` klassen)
 * en verwerkt de input van de gebruiker.
 */
class GameApplication:

  // Maakt een nieuwe opgezette instantie van ons spel
  private val game = Game()
  private val padding = 20

  // dit veld initialiseert de grafische gebruikersinterface (GUI) met de juiste afmetingen
  private val gui = new GUI(
    GameConstants.WINDOW_WIDTH + padding * 2,
    GameConstants.WINDOW_HEIGHT + padding * 2,
    GameConstants.NUM_ROWS,
    GameConstants.NUM_COLS,
    padding
  )

  // houdt de UI-staat bij: welk stuk is geselecteerd voor een verplaatsing
  private var selectedPiecePos: Option[(Int, Int)] = None

  // Houdt de UI-staat bij: welke power-up is geselecteerd voor gebruik
  private var selectedPowerUp: Option[PowerUp] = None


  // start de applicatie
  def run(): Unit =
    redraw() //beginposities van het spel op het scherm tekenen
    setupMouseListener() // activeert de code die luister naar muisklikken

  /**
   * Tekent de volledige, huidige spelstaat op het scherm.
   * Vertaalt de logische `Game`-staat naar een lijst van visuele `DrawableCell`-objecten.
   */
  // methode die de huisige spelsaat tekent op het scherm
  private def redraw(): Unit =
    val panel = gui.getGridPanel
    panel.clear()
    val drawables = scala.collection.mutable.ListBuffer[DrawableCell]()

    // het doorloopt het grid en maakt voor elk piece een drawablegamepiece object aan en voegt die toe aan de lijst
    for r <- 0 until (GameConstants.NUM_ROWS - 1); c <- 0 until GameConstants.NUM_COLS do
      game.grid.get(r, c) match
        case Some(piece) =>
          drawables += new DrawableGamePiece(piece, r, c, GameConstants.CELL_WIDTH, GameConstants.CELL_HEIGHT, padding)
        case None => ()

    // idem voor de buttons, teken die voor de speler die aan de beurt is
    val uiRow = GameConstants.NUM_ROWS - 1
    var col = 0

    // voor elk powerup waarvan de count groter is dan 0, tekenen we ook een button
    for (powerUp, count) <- game.getCurrentPlayer.powerUps do
      if count > 0 then
        drawables += new DrawableButton(powerUp, uiRow, col, GameConstants.CELL_WIDTH, GameConstants.CELL_HEIGHT, padding)
        col += 1

    // we geven alle te tekenen objecten door aan de GUI en updaten het scherm
    panel.addDrawables(drawables.toList.asJava)
    panel.repaint()


  // dit is de listerner die reageert op muisklikken van de speler
  private def setupMouseListener(): Unit =

    // elke keer dat er geklikt wordt moet die pixels omgezet worden naar coordinaten
    gui.getGridPanel.setPressListener { (pixelX, pixelY) =>

      // reken pixel-coördinaten om naar grid-coördinaten.
      val col = (pixelX - padding) / GameConstants.CELL_WIDTH
      val row = (pixelY - padding) / GameConstants.CELL_HEIGHT

      // handel de logica van de klik af.
      handleMouseClick(row, col)

      // teken het bord opnieuw om de verandering te tonen
      redraw()
    }


  // deze methode bevat de logica voor het afhandelne van een klik
  private def handleMouseClick(row: Int, col: Int): Unit =
    (selectedPiecePos, selectedPowerUp) match
      
      // geval 1: een stuk was al geselecteerd, GEEN power-up. Dit is de tweede klik van een normale zet
      case (Some((startRow, startCol)), None) =>
        if startRow == row && startCol == col then // speler klikt op hetzelfde stuk
          selectedPiecePos = None // selcetie wordt gewoon gereset
          println("Selectie ongedaan gemaakt.")
        else
          game.movePiece(startRow, startCol, row, col) // roept de 'standaard' methode aan
          selectedPiecePos = None // reset altijd de selectie na een zetpoging

      // geval 2: een power-up was geselecteerd. Deze (eerste) klik is het doelwit.
      case (_, Some(powerUp)) =>
        game.usePowerUp(powerUp, row, col) // roept de 'standaard' methode aan

        // check welk type powerup het was
        powerUp match

          // voor de onderstaande powerups wordt de target direct het nieuw geselcteerd stuk
          case PowerUp.DiagonalMove | PowerUp.Jump | PowerUp.XJumpAndDiagonalMove =>
            println(s"Selecteer nu een doelwit-vakje om te verplaatsen.")
            selectedPiecePos = Some((row, col)) // het stuk is nu automatisch geselecteerd

          // voor actie powerups (verdeglen en extrasoldier) is de actie compleet en weer reset
          case _ =>
            selectedPiecePos = None //

        selectedPowerUp = None // Reset altijd de power-up selectie

      // geval 3: niets was geselecteerd (geen stuk, geen power-up).
      // Dit is de eerste klik van een nieuwe beurt.
      case (None, None) =>

      // eerste controle; Is de speler op de onderste button bar aan het klikken
      if row == GameConstants.NUM_ROWS - 1 then

        // haal de power-up voorraad van de huidige speler op (bv. Map(Jump -> 2, Annihilate -> 0)).
        val powerUpInventory = game.getCurrentPlayer.powerUps

        // filter de voorraad zodat alleen de power-ups overblijven waarvan de speler er nog minstens één heeft.
        val availablePowerUpsMap = powerUpInventory.filter { (powerUp, count) =>
          count > 0
        }

        // maak een lijst van alleen de namen van de beschikbare power-ups
        val availablePowerUpsList = availablePowerUpsMap.keys.toList
        // de geklikte kolom is de index in onze lijst van knoppen
        val powerUpIndex = col

        // controleer of de geklikte kolom een geldige knop is
        if powerUpIndex < availablePowerUpsList.size then

          // Zo ja, haal de geselecteerde power-up uit de lijst.
          val clickedPowerUp = availablePowerUpsList(powerUpIndex)
          // Onthoud dat deze power-up nu geselecteerd is voor de volgende klik.
          selectedPowerUp = Some(clickedPowerUp)
          println(s"${clickedPowerUp} geselecteerd. Kies nu een doelwit.")

      else // tweede controle: de klik was niet op de UI-rij, dus het was op het speelbord.
        

        // vraag aan het grid wat er op de geklikte positie staat.
        game.grid.get(row, col) match
          // als er een stuk staat (`Some(piece)`) EN dat stuk is van de huidige speler...
          case Some(piece) if piece.role == game.getCurrentPlayer.role =>
            
            // onthoud dan de positie van dit stuk voor de volgende klik.
            selectedPiecePos = Some((row, col))
            println(s"Stuk geselecteerd op ($row, $col).")

          // in alle andere gevallen (leeg vakje, stuk van tegenstander), doe niets
          case _ => ()