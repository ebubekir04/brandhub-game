package Magic

/**
 * Definieert de vier mogelijke power-ups in het spel.
 * Wanneer er extra powerups bijkomen kunnen die hierook
 * toegevoegd worden
 */

enum PowerUp:
  case Annihilate, DiagonalMove, Jump, ExtraSoldier, XJumpAndDiagonalMove
  // de laatste is een extra powerup, toekomstige powerups kunnen hier ook toegevoegd worden