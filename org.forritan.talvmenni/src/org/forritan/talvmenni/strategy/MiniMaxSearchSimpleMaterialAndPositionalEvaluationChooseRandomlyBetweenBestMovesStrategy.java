package org.forritan.talvmenni.strategy;

import java.util.List;
import java.util.Random;

import org.forritan.talvmenni.evaluation.Evaluation;
import org.forritan.talvmenni.evaluation.SimpleMaterialAndPositionalEvaluation;
import org.forritan.talvmenni.game.Position;
import org.forritan.talvmenni.game.PositionFactory;
import org.forritan.talvmenni.game.TheoryBook;
import org.forritan.talvmenni.game.Position.Move;
import org.forritan.talvmenni.game.Position.PromotionPiece;
import org.forritan.talvmenni.search.MiniMaxSearch;
import org.forritan.talvmenni.search.Search;
import org.forritan.talvmenni.strategy.Strategy.DebugInfo;
import org.forritan.util.Tuple;


public class MiniMaxSearchSimpleMaterialAndPositionalEvaluationChooseRandomlyBetweenBestMovesStrategy
      implements Strategy {

   private DebugInfo  debugInfo;

   private TheoryBook book;
   private Search     search;
   private Evaluation evaluation;

   public MiniMaxSearchSimpleMaterialAndPositionalEvaluationChooseRandomlyBetweenBestMovesStrategy(
         int ply,
         TheoryBook book) {
      this.debugInfo= new DebugInfo();
      this.search= new MiniMaxSearch(
            ply);
      this.evaluation= new SimpleMaterialAndPositionalEvaluation();
      this.book= book;
   }

   public Move getNextMove(
         Position position,
         boolean whiteToMove) {

      if (this.book.containsKey(position)) {
         this.getDebugInfo().postText(
               "Move found in book... [searched through "
                     + this.book.size()
                     + " entries]...");

         Position bookPosition= this.book.get(position);

         if (bookPosition != null) {
            Position.Bitboard board;
            if (whiteToMove) {
               board= bookPosition.getWhite();
            } else {
               board= bookPosition.getBlack();
            }

            List<Tuple<Move, Integer>> moves= board.getBookMoves();

            int totalWeight= 0;

            for (Tuple<Move, Integer> tuple : moves) {
               totalWeight+= tuple.b.intValue();
            }

            if (totalWeight > 0) {

               int weight= new Random().nextInt(totalWeight);

               for (Tuple<Move, Integer> tuple : moves) {
                  weight-= tuple.b.intValue();
                  if (weight < 0) { return tuple.a; }
               }
            }

         }
      }

      List<Move> bestMoves= this.search.getBestMoves(
            position.getImmutable(),
            this.evaluation,
            whiteToMove);

      if (!bestMoves.isEmpty()) {
         int chosenMoveIndex= new Random().nextInt(bestMoves.size());
         return bestMoves.get(chosenMoveIndex);
      } else {
         return null;
      }
   }

   public int getPromotionPiece() {
      return PositionFactory.PromotionPiece.DEFAULT;
   }

   public Search getSearch() {
      return this.search;
   }

   public TheoryBook getTheoryBook() {
      return this.book;
   }

   public DebugInfo getDebugInfo() {
      return this.debugInfo;
   }

}