package org.forritan.talvmenni.game;

import java.util.List;
import java.util.Observable;
import java.util.Random;

import org.forritan.talvmenni.game.Position.Move;
import org.forritan.talvmenni.search.Search;

public class RandomMoveStrategy extends Observable implements Strategy {

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

   public Observable getObservable() {
      return this;
   }

   public Search getSearch() {
      return null;
   }

}
