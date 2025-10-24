package Grid

/**
 * Deze klasse beheert de staat van een grid op een veilige  manier.
 * De inhoud van de cellen is van het generieke type [T]. Lege cellen worden
 * voorgesteld door `None`.
 */

// Een generieke grid, dus efficient voor generalisatie.
class Grid[T](val rows: Int, val columns: Int):

  // De interne representatie van het grid is privaat zodat niemand deze ooit kapot kan maken
  // option wordt gebruikt,  lege vakjes krijgen None ipv null , wat errors kan voorkomen (NullPointerException)
  // een vector (immutable) van vectoren = matrix = grid
  // Array (muttable) kon ook, is gewoon een programmeerkeuze
  private var grid: Vector[Vector[Option[T]]] = Vector.fill(rows, columns)(None)

  // validate van coordinaten
  def isValid(row: Int, col: Int): Boolean =
    row >= 0 && row < rows && col >= 0 && col < columns

  // vraag de inhoud op een coordinaat (x, y), opnieuw met een Option
  def get(row: Int, col: Int): Option[T] =
    if isValid(row, col) then
      grid(row)(col)
    else
      None

  // plaatst een waarde in een cel op een bepaald locatie
  def set(row: Int, col: Int, value: T): Unit =
    if isValid(row, col) then
      val updatedRow = grid(row).updated(col, Some(value))
      grid = grid.updated(row, updatedRow)
    else
      println(s"Fout: ongeldige coördinaten ($row, $col) voor 'set'. Zet wordt genegeerd.")


  // deze methode maakt een cel op een specifieke locatie leeg
  def clear(row: Int, col: Int): Unit =
    if isValid(row, col) then
      // maakt een kopie van de juiste rij, maar de column wordt none
      val updatedRow = grid(row).updated(col, None)
      // daarna maakt t een kopie van de hele grid en vervangt de oude rij door de updaterRow
      grid = grid.updated(row, updatedRow)


  // deze methode (hoger orde functie) zoekt naar de eerste cel die voldoet aan de predikaat en geeft de coordinaat terug
  def findPiece(predicate: T => Boolean): Option[(Int, Int)] =
    for r <- 0 until rows; c <- 0 until columns do

      //voor elke cel,vraagt het de inhoud aub, ofwel Some (..) of None
      get(r, c) match
        case Some(piece) if predicate(piece) => return Some((r, c))
        case _ => () //we komen in deze tak als er een None was op die cel of de inhoud voldat niet aan predikaat
    None // als er niks is gevonden
