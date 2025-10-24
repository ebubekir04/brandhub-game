package GameBasis

import Grid.Grid
import GamePiece.*
import PlayerAttributes.Player
import Magic.{DiagonalMove, Jump, PowerUp, XJumpAndDiagonalMove}
import GameConstants.*

/**
 * Dit is de centrale klasse die het hele spel beheert.
 * Deze klasse bevat de volledige spelstaat (bord, spelers, status) en is de enige
 * die de regels kan toepassen om die staat te veranderen.

 */
class Game(
            val grid: Grid[GamePiece],
            val attacker: Player,
            val defender: Player
          ):

  // dit veld houdt bij welke speler momenteel aan de beurt is
  private var currentPlayerRole: PlayerRole = PlayerRole.Attacker

  // dit veld houdt de algemene status van het spel bij (AttackerWins, DefenderWins, GameOn).
  private var status: GameStatus = GameStatus.GameOn

  // dit veld houdt een map die actieve timers op stukken bijhoudt, gebruikmakend van het unieke stuk-ID.
  private var timedEffects: Map[Int, Int] = Map.empty

  // De volgende twee methodes zijn de publieke Getters
  // Op deze manier garanderen we dat er aan die velden niemand aan kan komen
  def getCurrentPlayer: Player = if currentPlayerRole == PlayerRole.Attacker then attacker else defender
  def getStatus: GameStatus = status


  // deze methode verwerkt een poging van een speler om een piece te verplaatsen
  def movePiece(startRow: Int, startCol: Int, endRow: Int, endCol: Int): Unit =

    // drie validatie checks voordat we beginnen
    if status != GameStatus.GameOn then
      println("Het spel is al afgelopen.")
      return
    if getCurrentPlayer.role != currentPlayerRole then
      println("Je bent niet aan de beurt.")
      return
    if !grid.isValid(startRow, startCol) || !grid.isValid(endRow, endCol) then
      println("Coördinaten zijn buiten het bord.")
      return

    // check de inhoud van de gegeven startpositie
    grid.get(startRow, startCol) match
      // als de positie geen inhoud heeft dan gedaan, anders check je de inhoud
      case None => println("Er staat geen stuk op de startpositie.")
      case Some(piece) if piece.role != currentPlayerRole => println("Dit stuk is niet van jou.")
      // als piece weldegelijk ons spelstuk is dan voeren we de 'move' uit
      case Some(piece) =>
        val validMoves = piece.getValidMoves(startRow, startCol, grid)
        if validMoves.contains((endRow, endCol)) then
          executeMove(piece, startRow, startCol, endRow, endCol)
          println("Zet succesvol uitgevoerd.") // Succesbericht
        else
          println("Ongeldige verplaatsing voor dit stuk.")



  // verwerkt een poging van een speler om een power-up te gebruiken
  def usePowerUp(powerUp: PowerUp, targetRow: Int, targetCol: Int): Unit =
    val player = getCurrentPlayer
    
    // drie validaties voordat we verder kunnen
    if status != GameStatus.GameOn then
      println("Het spel is al afgelopen.")
      return
    
    if !player.hasPowerUp(powerUp) then
      println(s"Je hebt geen ${powerUp}s meer.")
      return
    
    if !grid.isValid(targetRow, targetCol) then
      println("Doelwit is buiten het bord.")
      return

    
    
    // logica per Powerup
    powerUp match
      
      // geval 1 => ExtraSoldier: heeft een LEEG vakje als doelwit
      case PowerUp.ExtraSoldier =>
        grid.get(targetRow, targetCol) match
          case None => // het vakje is inderdaad leeg , ideaal
            player.usePowerup(powerUp) // 1 extrasoldier powerup wordt van de voorraad verwijdert
            val newSoldier = new Soldier(player.role)
            player.addPiece(newSoldier)
            grid.set(targetRow, targetCol, newSoldier)
            timedEffects += (newSoldier.id -> 3) // timer van 3 rondes gestart, timer = 0 => verdwijnen soldaat
            println(s"Extra Soldaat geplaatst op ($targetRow, $targetCol).")
            endTurn()
          case Some(_) => // het vakje is bezet.
            println("Fout: Je kunt een extra soldaat alleen op een leeg vakje plaatsen.")

      // geval 2 => Alle andere power-ups: hebben een BESTAAND stuk als doelwit
      case _ =>
        grid.get(targetRow, targetCol) match
          case None => // Het vakje is leeg.
            println(s"Fout: ${powerUp} kan niet op een leeg vakje gebruikt worden.")
          case Some(targetPiece) => // er staat een stuk
            powerUp match
              
              // geval 2.1: Annihilate
              case PowerUp.Annihilate =>
                if targetPiece.role == player.role then
                  println("Fout: Annihilate kan alleen op een stuk van de tegenstander.")
                  return // dit stopt de methode onmiddelijk

                targetPiece match
                  case _: Soldier =>
                    player.usePowerup(powerUp) // voorraad van dit powerup updaten
                    timedEffects += (targetPiece.id -> 3) // timer opzetten
                    println(s"Annihilate geactiveerd op soldaat op ($targetRow, $targetCol).")
                    endTurn() // belgangrijk dat dit wordt aangeroepen, want dit zorgt ervoor dat rondes wisselen
                  case _: King =>
                    println("Fout: Je kunt de Koning niet verdelgen.")

              // geval 2.3:  power-ups die gewoon geactiveerd moeten worden (Jump, DiagonalMove, etc.)
              case PowerUp.DiagonalMove | PowerUp.Jump | PowerUp.XJumpAndDiagonalMove =>
                if targetPiece.role != player.role then
                  println("Fout: Je kunt deze power-up alleen op je eigen stukken gebruiken.")
                  return

                // creeer het nieuwe, "verbeterde" stuk met de trait. Er wordt een nieuw piece gemaakt met diezelfde id
                // deze is makkelijkj uit te breiden
                val decoratedPiece = (targetPiece, powerUp) match
                  case (s: Soldier, PowerUp.DiagonalMove) => new Soldier(s.role, s.id) with DiagonalMove
                  case (k: King,    PowerUp.DiagonalMove) => new King(k.id) with DiagonalMove
                  case (s: Soldier, PowerUp.Jump)         => new Soldier(s.role, s.id) with Jump
                  case (k: King,    PowerUp.Jump)         => new King(k.id) with Jump
                  case (s: Soldier, PowerUp.XJumpAndDiagonalMove) => new Soldier(s.role, s.id) with XJumpAndDiagonalMove
                  case (k: King,    PowerUp.XJumpAndDiagonalMove) => new King(k.id) with XJumpAndDiagonalMove
                  case _ => targetPiece
                  

                // update de voorraad, vervang 'oud' piece door 'nieuw' piece
                player.usePowerup(powerUp)
                grid.set(targetRow, targetCol, decoratedPiece)
                player.replacePiece(targetPiece, decoratedPiece)
                println(s"${powerUp} geactiveerd op stuk op ($targetRow, $targetCol).")
  // BELANGRIJK!!!
  // Hier komt geen endTurn(), want het activeren van een poweruop is niet het einde van je beurt!


// De volgende methodes zijn private methode voor de interne logica

  // voert een valid zet uit en handelt de gevolgen af
  private def executeMove(piece: GamePiece, startRow: Int, startCol: Int, endRow: Int, endCol: Int): Unit =
    grid.set(endRow, endCol, piece)
    grid.clear(startRow, startCol)
    checkForCaptures(endRow, endCol)
    updateGameStatus()
    if status == GameStatus.GameOn then endTurn()

  // check of de zet die je uitvoerde een stuk van de tegenstander heeft 'gekilled'
  private def checkForCaptures(movedToRow: Int, movedToCol: Int): Unit =
    val directions = List((-1, 0), (1, 0), (0, -1), (0, 1))
    
    // doorloop elk van de vier richting op je nieuw positie
    for (dr, dc) <- directions do
      val neighborPos = (movedToRow + dr, movedToCol + dc) // vakje direct naast waar de piece is geland
      val behindPos = (movedToRow + 2 * dr, movedToCol + 2 * dc) //vakje daar weer achter 
      
      // eerst wordt gecheckt of er op die coordinaat  wel degelijk een piece staat
      (grid.get(neighborPos._1, neighborPos._2), grid.get(behindPos._1, behindPos._2)) match
        
        // als de buur een soldier is (geen koning dus) en de piece daarachter is ook spelstuk , en ze zijn van vershillende rollen , verwijder
        case (Some(neighborPiece: Soldier), Some(pieceBehind)) if neighborPiece.role != currentPlayerRole && pieceBehind.role == currentPlayerRole =>
          grid.clear(neighborPos._1, neighborPos._2) // clear want 
          val opponent = if currentPlayerRole == PlayerRole.Attacker then defender else attacker
          opponent.removePiece(neighborPiece)
        case _ => ()


  // deze methode werkt de spelstatus bij door te controleren of er een winnaar is
  private def updateGameStatus(): Unit =
    status = findWinner()
    if status != GameStatus.GameOn then
      println(s"Spel voorbij! De winnaar is: $status")

  // bepaalt de winnaar op basis van de huidige bordstand
  private def findWinner(): GameStatus =
  
    // eerst wordt het hele bord doorzocht op zoek naar de king
    val kingPosOpt = grid.findPiece { case _: King => true; case _ => false }
    kingPosOpt match
      case None => return GameStatus.AttackerWins // als de king niet te vinden, winnen de attackers
      
      // als de koning gevonden is en die zit op een hoek, dan winnen de verdedigers
      case Some((r, c)) =>
        val lastPlayableRow = NUM_ROWS - 2
        val lastCol = NUM_COLS - 1
        if (r == 0 && c == 0) || (r == 0 && c == lastCol) || (r == lastPlayableRow && c == 0) || (r == lastPlayableRow && c == lastCol) then
          return GameStatus.DefenderWins

        // checkt of de koning gecaptured is deze keer tussen 2 aanvalles
        val isHorizontal = isAttackerAt(r, c - 1) && isAttackerAt(r, c + 1)
        val isVertical = isAttackerAt(r - 1, c) && isAttackerAt(r + 1, c)
    
        if isHorizontal || isVertical then
          return GameStatus.AttackerWins
    
    GameStatus.GameOn // game verder als niks slaagt

  // helpermethode die checkt of er een piece van de attacker op een specifek coordinaat staat
  private def isAttackerAt(row: Int, col: Int): Boolean =
    grid.get(row, col) match
      case Some(piece) =>
        piece.role == PlayerRole.Attacker
      case None =>
        false


  // behandelt alle acties aan het einde van een beurt: timers en beurtwissel
  private def endTurn(): Unit =
  
    // de methode gaat als beurt = defender
    // timer worden per volledige ronde (een zet van defender + een zet van attacker) geupdate
    if currentPlayerRole == PlayerRole.Defender then
      val newTimedEffects = timedEffects.map { (id, turnsLeft) => (id, turnsLeft - 1) }
      
      // de nieuwe timermap wordt doorlopen
      newTimedEffects.foreach { (id, turnsLeft) =>
        if turnsLeft <= 0 then // als 0 dan wordt het bijhorende piece op t bord gevonden met id
          
          // check of de id van de piece op het bord overeenkomt met de id van in de map
          // herinnering: findpiece gaat alle cellen af totdat de predikaat klopt
          grid.findPiece(piece => piece.id == id).foreach { case (r, c) =>
            
            // de laatste get is nodig om de 'doos' open te maken, dus geen option terug krijgen
            val pieceToRemove = grid.get(r, c).get 
            grid.clear(r, c) // piece wordt van de grid verwijdert
            val owner = if pieceToRemove.role == PlayerRole.Attacker then attacker else defender
            owner.removePiece(pieceToRemove) // piece wordt ook van lijst van de eigenaar verwijdert
            println(s"Een stuk op ($r, $c) is verwijderd door een afgelopen timer.")
          }
      }
      
      // filter de tuples weg waarvan de turnleft kleiner dan 0 is, id is onbelangrijk daarom _
      timedEffects = newTimedEffects.filter { case (_, turnsLeft) => turnsLeft > 0 }
    
    // aan het einde ook van beurt wisselen
    currentPlayerRole = if currentPlayerRole == PlayerRole.Attacker then PlayerRole.Defender else PlayerRole.Attacker
    println(s"Het is nu de beurt aan: $currentPlayerRole")


object Game:
  
  /**
   * Creëert en initialiseert een volledig nieuw, speelklaar spel.
   * opnieuw deze functionaliteit is van belang voor elk instantie 
   * maar behoort niet tot 1 specifiek instantie, daarom 
   * het concept companion object
   */
  
  // deze methode maak een leeg grid, een map met beginvoorraad van Powerups en twee Player objecten met hun spelstukken
  def apply(): Game =
    val grid = new Grid[GamePiece](NUM_ROWS, NUM_COLS)
    val powerUps = Map(
      PowerUp.Annihilate -> 2, 
      PowerUp.DiagonalMove -> 3,
      PowerUp.Jump -> 2, 
      PowerUp.ExtraSoldier -> 1,
      PowerUp.XJumpAndDiagonalMove -> 1 // voorbeeld powerup, je kunt de voorraad aanpassen
                                        // nieuwe powerups kunnen hier makkellijk toegevoegd worden
    )
    val attacker = new Player(PlayerRole.Attacker, List.fill(8)(new Soldier(PlayerRole.Attacker)), powerUps)
    val defender = new Player(PlayerRole.Defender, List.fill(4)(new Soldier(PlayerRole.Defender)) :+ new King(), powerUps)

    BoardSetUp.placePieces(grid, attacker, defender)

    new Game(grid, attacker, defender)
