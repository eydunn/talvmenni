package org.forritan.talvmenni.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import org.forritan.talvmenni.evaluation.Evaluation;
import org.forritan.talvmenni.game.Position;
import org.forritan.talvmenni.game.Position.Move;

public class FullSearch extends Observable implements Search {
   
   private Thinking thinking;
   private int depth;
   
   private int movesSearched;
   
   public FullSearch(int depth) {
      this.depth= depth;
      this.thinking= new Thinking();
   }

   public Thinking getThinking() {
      return this.thinking;
   }

   public List<Move> getBestMoves(
         Position p,
         Evaluation e,
         boolean whiteMove) {
      
      long time= -System.currentTimeMillis();
      this.movesSearched= 0;
      
      List<Move> result= new ArrayList<Move>();
      MoveScoreTuple bestScore= null;
      
      List<Move> moves;
      if(whiteMove) {
         moves= p.white.getPossibleMoves();         
      } else {
         moves= p.black.getPossibleMoves();         
      }
      
      for(Move move : moves) {
         long moveTime= -System.currentTimeMillis();
         int movesSearchedBeforeMove= this.movesSearched++;
         MoveScoreTuple score= this.getBestMove(p.move(move.from, move.to), e, !whiteMove, depth - 1);
         score.add(move, e.getScore(p));
         this.setChanged();
         this.notifyObservers("[" + move.toString() + "] " + score.getScore() + " and there are " + (this.movesSearched - movesSearchedBeforeMove) + " positions searched...");
         if(bestScore == null || (whiteMove ? score.getScore() > bestScore.getScore() : score.getScore() < bestScore.getScore())) {
            bestScore= score;
            result.clear();
            result.add(move);
            moveTime += System.currentTimeMillis();
            this.thinking.postThinking(depth, bestScore.getScore(), moveTime, this.movesSearched, bestScore.getMoveList().toString());
         }else if(bestScore != null && score.getScore() == bestScore.getScore()) {
            result.add(move);
         }
      }
      
      time += System.currentTimeMillis() + 1; // Hmmm... tricky one... add one
                                              // millisecond to make sure that
                                              // we don't get division by zero
                                              // in notifyObservers call below
                                              // :-)
      
      this.setChanged();
      this.notifyObservers(
            "\nFinished search of " 
            + this.movesSearched 
            + " positions in just " 
            + time 
            + " milliseconds...\ni.e: " 
            + 1L * this.movesSearched * 1000 / time + " pr. second."
            + "\nBest moves: " 
            + result.toString() + "\n");
      
      return result;
   }

   private MoveScoreTuple getBestMove(
         Position p,
         Evaluation e,
         boolean whiteMove,
         int depth) {

      MoveScoreTuple result= new MoveScoreTuple(null, 0);
      if(depth > 1) {
          List<Move> moves;
         MoveScoreTuple bestScore= null;
         
         if(whiteMove) {
            moves= p.white.getPossibleMoves();         
         } else {
            moves= p.black.getPossibleMoves();         
         }
         
         if(moves.size() > 0) {
            Move currentBestMove= null;
            for(Move move : moves) {
               this.movesSearched++;
               MoveScoreTuple score= this.getBestMove(p.move(move.from, move.to), e, !whiteMove, depth - 1);
               System.err.println("score: " + score);
               System.err.println("bestScore: " + bestScore);
               if(bestScore == null || (whiteMove ? score.getScore() > bestScore.getScore() : score.getScore() < bestScore.getScore())) {
                  bestScore= score;
                  currentBestMove= move;
               }
            }         
            result= bestScore;
            result.add(currentBestMove, e.getScore(p));
            
         } else {
            if(whiteMove){
               if(p.white.isChecked()) {
                  result= new MoveScoreTuple(null, -20000 + depth); // Checkmate
               } else {
                  result= new MoveScoreTuple(null, 0);  // Stalemate
               }
             } else {
               if(p.black.isChecked()) {
                  result= new MoveScoreTuple(null, 20000 - depth); // Checkmate
               } else {
                  result= new MoveScoreTuple(null, 0);  // Stalemate
               }
            }
         }
      } else {
         List<Move> moves;
         int bestScore= 0;
         if(whiteMove) {
            moves= p.white.getPossibleMoves();
         } else {
            moves= p.black.getPossibleMoves();         
         }
         
         if(moves.size() > 0) {

            for(Move move : moves) {
               this.movesSearched++;
               int score= e.getScore(p.move(move.from, move.to));
               if((whiteMove ? score >= bestScore : score <= bestScore)) {
                  bestScore= score;
                  result= new MoveScoreTuple(move, score);
               }
            }
         } else {
            if(whiteMove){
               if(p.white.isChecked()) {
                  result= new MoveScoreTuple(null, -20000 + depth); // Black
                                                                      // wins by
                                                                      // checkmate
               } else {
                  result= new MoveScoreTuple(null,  0);  // Stalemate
               }
             } else {
               if(p.black.isChecked()) {
                  result= new MoveScoreTuple(null,  20000 - depth); // White
                                                                       // wins
                                                                       // by
                                                                       // checkmate
               } else {
                  result= new MoveScoreTuple(null,  0);  // Stalemate
               }
            }
         }
      }
      
      return result;
   }
   
   public Observable getObservable() {
      return this;
   }

   
   private static class MoveScoreTuple {
      private List<Move> moves;
      private int score = 0;
      
      public MoveScoreTuple(Move move, int score) {
         this.moves= new ArrayList<Move>();
         if(move != null) {
            this.moves.add(move);
         }
         this.score= score;
      }
      
      public void add(Move move, int score) {
         if(move != null) {
            this.moves.add(0, move);
         }
         this.score += score;
      }
      
      public List<Move> getMoveList(){
         return Collections.unmodifiableList(this.moves);
      }
      
      public Move getMove() {
         return this.moves.get(0);
      }
      
      public int getScore() {
         return this.score;
      }
      
   }
}