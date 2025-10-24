package GameBasis

/**
 * Alle constanten van het spel bewaren
 * Doel is om 'cleaner' code te verkrijgen zonder al die getallen.
 * Kon evengoed in de file GameApplication staan.
 * Leesbaarheid en onderhoudbaarheid (bv. grid aanpassen) zijn voordelen
 * Keuze voor object omdat er maar 1 instantiatie zal gebeuren
 */


object GameConstants:
  val NUM_ROWS: Int = 8 // een rij extra voor de buttons
  val NUM_COLS: Int = 7
  val CELL_WIDTH: Int = 90
  val CELL_HEIGHT: Int = 90
  val WINDOW_WIDTH: Int = NUM_COLS * CELL_WIDTH
  val WINDOW_HEIGHT: Int = NUM_ROWS * CELL_HEIGHT