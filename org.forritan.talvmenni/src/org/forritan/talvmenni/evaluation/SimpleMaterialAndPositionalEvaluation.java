package org.forritan.talvmenni.evaluation;

import java.util.Iterator;

import org.forritan.talvmenni.bitboard.BitboardIterator;
import org.forritan.talvmenni.bitboard.evaluation.Board;
import org.forritan.talvmenni.game.Position;

public class SimpleMaterialAndPositionalEvaluation implements Evaluation { 

   public int getScore(
         Position position) {
      int result= 0;
      for (Iterator it= position.white.kingsIterator(); it.hasNext();) {
         it.next();
         result += Evaluation.KING;         
      }
      for (Iterator it= position.white.queensIterator(); it.hasNext();) {
         it.next();
         result += Evaluation.QUEEN;         
      }
      for (Iterator it= position.white.rooksIterator(); it.hasNext();) {
         it.next();
         result += Evaluation.ROOK;         
      }
      for (Iterator it= position.white.bishopsIterator(); it.hasNext();) {
         it.next();
         result += Evaluation.BISHOP;         
      }
      for (Iterator it= position.white.knightsIterator(); it.hasNext();) {
         it.next();
         result += Evaluation.KNIGHT;         
      }
      for (Iterator it= position.white.pawnsIterator(); it.hasNext();) {
         it.next();
         result += Evaluation.PAWN;         
      }
      
      for (Iterator it= new BitboardIterator((position.white.allPieces & Board._CORE)); it.hasNext();) {
         it.next();
         result += Evaluation.POSITION_CORE;         
      }
      for (Iterator it= new BitboardIterator((position.white.allPieces & Board._INNER_BORDER)); it.hasNext();) {
         it.next();
         result += Evaluation.POSITION_INNER_BORDER;         
      }
      for (Iterator it= new BitboardIterator((position.white.allPieces & Board._MIDDLE_BORDER)); it.hasNext();) {
         it.next();
         result += Evaluation.POSITION_MIDDLE_BORDER;         
      }
      for (Iterator it= new BitboardIterator((position.white.allPieces & Board._OUTER_BORDER)); it.hasNext();) {
         it.next();
         result += Evaluation.POSITION_OUTER_BORDER;         
      }

      for (Iterator it= new BitboardIterator((position.white.getAllCaptureMovesAttackedSquares() & position.white.allPieces)); it.hasNext();) {
         it.next();
         result += Evaluation.POSITION_GUARD_REWARD;         
      }

      for (Iterator it= new BitboardIterator((position.white.getAllPawnsReadyForPromotion())); it.hasNext();) {
         it.next();
         result += Evaluation.POSITION_PAWN_READY_FOR_PROMOTION;         
      }

      
      
      for (Iterator it= position.black.kingsIterator(); it.hasNext();) {
         it.next();
         result -= Evaluation.KING;         
      }
      for (Iterator it= position.black.queensIterator(); it.hasNext();) {
         it.next();
         result -= Evaluation.QUEEN;         
      }
      for (Iterator it= position.black.rooksIterator(); it.hasNext();) {
         it.next();
         result -= Evaluation.ROOK;         
      }
      for (Iterator it= position.black.bishopsIterator(); it.hasNext();) {
         it.next();
         result -= Evaluation.BISHOP;         
      }
      for (Iterator it= position.black.knightsIterator(); it.hasNext();) {
         it.next();
         result -= Evaluation.KNIGHT;         
      }
      for (Iterator it= position.black.pawnsIterator(); it.hasNext();) {
         it.next();
         result -= Evaluation.PAWN;         
      }
      
      for (Iterator it= new BitboardIterator((position.black.allPieces & Board._CORE)); it.hasNext();) {
         it.next();
         result -= Evaluation.POSITION_CORE;         
      }
      for (Iterator it= new BitboardIterator((position.black.allPieces & Board._INNER_BORDER)); it.hasNext();) {
         it.next();
         result -= Evaluation.POSITION_INNER_BORDER;         
      }
      for (Iterator it= new BitboardIterator((position.black.allPieces & Board._MIDDLE_BORDER)); it.hasNext();) {
         it.next();
         result -= Evaluation.POSITION_MIDDLE_BORDER;         
      }
      for (Iterator it= new BitboardIterator((position.black.allPieces & Board._OUTER_BORDER)); it.hasNext();) {
         it.next();
         result -= Evaluation.POSITION_OUTER_BORDER;         
      }
      
      for (Iterator it= new BitboardIterator((position.black.getAllCaptureMovesAttackedSquares() & position.black.allPieces)); it.hasNext();) {
         it.next();
         result -= Evaluation.POSITION_GUARD_REWARD;         
      }
      
      for (Iterator it= new BitboardIterator((position.black.getAllPawnsReadyForPromotion())); it.hasNext();) {
         it.next();
         result -= Evaluation.POSITION_PAWN_READY_FOR_PROMOTION;         
      }

      
      
      return result;
   }
}
