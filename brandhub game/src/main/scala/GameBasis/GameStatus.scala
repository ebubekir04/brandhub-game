package GameBasis

/**
 * Definieert de mogelijke statussen van het spel.
 * Wordt gebruikt als return type van checkWinCondition-methode
 */

enum GameStatus:
  case AttackerWins, DefenderWins, GameOn