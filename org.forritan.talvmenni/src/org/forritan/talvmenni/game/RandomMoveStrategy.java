package org.forritan.talvmenni.game;

import java.util.List;
import java.util.Random;

import org.forritan.talvmenni.game.Position.Move;

public class RandomMoveStrategy implements Strategy {

   public Move getNextMove(Position position, boolean whiteToMove) {
         List<Move> possibleMoves;
         if(whiteToMove) {
            possibleMoves= position.white.getPossibleMoves();
         } else {
            possibleMoves= position.black.getPossibleMoves();
         }
         if(!possibleMoves.isEmpty()) {
            int chosenMoveIndex= new Random().nextInt(possibleMoves.size());
            return possibleMoves.get(chosenMoveIndex);
             
         } else {
            return null;
         }      
   }

   public int getPromotionPiece() {
      return Position.PromotionPiece.KNIGHT;
   }

}