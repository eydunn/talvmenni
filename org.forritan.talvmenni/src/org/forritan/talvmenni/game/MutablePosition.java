package org.forritan.talvmenni.game;

import java.util.Stack;


public class MutablePosition extends PositionFactory {
   
   Stack<Bitboard> whiteBitboards;
   Stack<Bitboard> blackBitboards;
   
//   int pushMoves;

   /**
    * 
    * @param whiteKings
    * @param whiteQueens
    * @param whiteRooks
    * @param whiteBishops
    * @param whiteKnights
    * @param whitePawns
    * @param whiteCastling
    * @param whiteEnpassant
    * @param blackKings
    * @param blackQueens
    * @param blackRooks
    * @param blackBishops
    * @param blackKnights
    * @param blackPawns
    * @param blackCastling
    * @param blackEnpassant
    */
   protected MutablePosition(
         long whiteKings,
         long whiteQueens,
         long whiteRooks,
         long whiteBishops,
         long whiteKnights,
         long whitePawns,
         long whiteCastling,
         long whiteEnpassant,
         long blackKings,
         long blackQueens,
         long blackRooks,
         long blackBishops,
         long blackKnights,
         long blackPawns,
         long blackCastling,
         long blackEnpassant) {
      super(
            whiteKings,
            whiteQueens,
            whiteRooks,
            whiteBishops,
            whiteKnights,
            whitePawns,
            whiteCastling,
            whiteEnpassant,
            blackKings,
            blackQueens,
            blackRooks,
            blackBishops,
            blackKnights,
            blackPawns,
            blackCastling,
            blackEnpassant);
      this.whiteBitboards= new Stack<Bitboard>();
      this.blackBitboards= new Stack<Bitboard>();
   }

   /**
    * @param white
    * @param black
    */
   protected MutablePosition(
         Bitboard white,
         Bitboard black) {
      super(
            white,
            black);
      this.whiteBitboards= new Stack<Bitboard>();
      this.blackBitboards= new Stack<Bitboard>();
   }

   public Position pushMove(
         Bitboard white,
         Bitboard black) {
      
//      String pre= "";
//      
//      this.pushMoves++;
//
//      for (int i= 0; i < this.pushMoves; i++) {
//         pre += ">";
//      }
//      System.err.println(pre);

      PositionFactory.positionsCreated++;
      this.whiteBitboards.push(this.white);
      this.blackBitboards.push(this.black);
      this.white= white;
      this.black= black;
      return this;
   }
   
   public Position popMove() {
      this.white= this.whiteBitboards.pop();
      this.black= this.blackBitboards.pop();

//      String pre= "";
//
//      for (int i= 0; i < this.pushMoves; i++) {
//         pre += "<";
//      }
//      System.err.println(pre);
//      
//      this.pushMoves--;

      return this;
   }

   public Position getImmutable() {
      return PositionFactory.create(false, this.getWhite(), this.getBlack());
   }

   public Position getMutable() {
      return this;
   }
}