package org.forritan.talvmenni.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.forritan.talvmenni.bitboard.BitboardIterator;
import org.forritan.talvmenni.bitboard.Rank;
import org.forritan.talvmenni.bitboard.Square;
import org.forritan.talvmenni.bitboard.Squares;
import org.forritan.talvmenni.bitboard.attacks.Bishop;
import org.forritan.talvmenni.bitboard.attacks.BlackPawn;
import org.forritan.talvmenni.bitboard.attacks.King;
import org.forritan.talvmenni.bitboard.attacks.Knight;
import org.forritan.talvmenni.bitboard.attacks.Queen;
import org.forritan.talvmenni.bitboard.attacks.Rook;
import org.forritan.talvmenni.bitboard.attacks.WhitePawn;
import org.forritan.talvmenni.bitboard.paths.BlackPawnCaptures;
import org.forritan.talvmenni.bitboard.paths.BlackPawnMoves;
import org.forritan.talvmenni.bitboard.paths.WhitePawnCaptures;
import org.forritan.talvmenni.bitboard.paths.WhitePawnMoves;
import org.forritan.talvmenni.core.TalvMenni;
import org.forritan.talvmenni.ui.DebugWindow;


public class Position {

   public final Bitboard white;
   public final Bitboard black;

   public static Position createInitial() {
      return new Position(
            Square._E1, // whiteKings
            Square._D1, // whiteQueens
            Square._A1
                  | Square._H1, // whiteRooks
            Square._C1
                  | Square._F1, // whiteBishops
            Square._B1
                  | Square._G1, // whiteKnights
            Rank._2, // whitePawns
            Square._A1
                  | Square._E1
                  | Square._H1, // whiteCastling
            Square._EMPTY_BOARD, // whiteEnpassant
            Square._E8, // blackKings
            Square._D8, // blackQueens
            Square._A8
                  | Square._H8, // blackRooks
            Square._C8
                  | Square._F8, // blackBishops
            Square._B8
                  | Square._G8, // blackKnights
            Rank._7, // blackPawns
            Square._A8
                  | Square._E8
                  | Square._H8, // blackCastling
            Square._EMPTY_BOARD // blackEnpassant
      );
   }

   /**
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
    * @return
    */
   public static Position create(
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
      return new Position(
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
   }

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
   private Position(
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

      this.white= new Bitboard(
            true,
            this,
            whiteKings,
            whiteQueens,
            whiteRooks,
            whiteBishops,
            whiteKnights,
            whitePawns,
            whiteCastling,
            whiteEnpassant);
      this.black= new Bitboard(
            false,
            this,
            blackKings,
            blackQueens,
            blackRooks,
            blackBishops,
            blackKnights,
            blackPawns,
            blackCastling,
            blackEnpassant);
   }

   public Position move(
         long from,
         long to) {
      return this.move(from, to, PromotionPiece.DEFAULT);
   }
   
   public Position move(
         long from,
         long to,
         int promotionPiece) {

      long whiteKings= this.white.kings;
      long whiteQueens= this.white.queens;
      long whiteRooks= this.white.rooks;
      long whiteBishops= this.white.bishops;
      long whiteKnights= this.white.knights;
      long whitePawns= this.white.pawns;
      long whiteCastling= this.white.castling;
      long whiteEnpassant= this.white.enpassant;

      long blackKings= this.black.kings;
      long blackQueens= this.black.queens;
      long blackRooks= this.black.rooks;
      long blackBishops= this.black.bishops;
      long blackKnights= this.black.knights;
      long blackPawns= this.black.pawns;
      long blackCastling= this.black.castling;
      long blackEnpassant= this.black.enpassant;

      if (this.white.isAnyPieceOnPosition(from)) {
         // Move the the piece...
         if (this.white.isKing(from)) {
            whiteKings= whiteKings
                  ^ from
                  | to;
            if(from == Square._E1) {
               if(to == Square._C1) {
                  whiteRooks= whiteRooks
                  ^ Square._A1
                  | Square._D1;
               } else if (to == Square._G1) {
                  whiteRooks= whiteRooks
                  ^ Square._H1
                  | Square._F1;                  
               }
            }
            whiteEnpassant= Square._EMPTY_BOARD;
            blackEnpassant= Square._EMPTY_BOARD;
            whiteCastling= Square._EMPTY_BOARD; 
         } else if (this.white.isQueen(from)) {
            whiteQueens= whiteQueens
                  ^ from
                  | to;
            whiteEnpassant= Square._EMPTY_BOARD;
            blackEnpassant= Square._EMPTY_BOARD;
         } else if (this.white.isRook(from)) {
            whiteRooks= whiteRooks
                  ^ from
                  | to;
            whiteEnpassant= Square._EMPTY_BOARD;
            blackEnpassant= Square._EMPTY_BOARD;
            whiteCastling ^= from;
         } else if (this.white.isBishop(from)) {
            whiteBishops= whiteBishops
                  ^ from
                  | to;
            whiteEnpassant= Square._EMPTY_BOARD;
            blackEnpassant= Square._EMPTY_BOARD;
         } else if (this.white.isKnight(from)) {
            whiteKnights= whiteKnights
                  ^ from
                  | to;
            whiteEnpassant= Square._EMPTY_BOARD;
            blackEnpassant= Square._EMPTY_BOARD;
         } else if (this.white.isPawn(from)) {

            // First set en passant bits...
            if (((from & Rank._2) != Square._EMPTY_BOARD) &&
                  (to & Rank._4) != Square._EMPTY_BOARD) {
               whiteEnpassant= WhitePawnMoves.create().getPathsFrom(
                     from);
            } else {
               whiteEnpassant= Square._EMPTY_BOARD;
            }
            blackEnpassant= Square._EMPTY_BOARD;
            

            // Check promotion
            if((~Rank._7 & from) == Squares._EMPTY_BOARD) {
               whitePawns ^= from;
               switch (promotionPiece) {
                  case PromotionPiece.QUEEN:
                     whiteQueens |= to;                                                
                     break;
                  case PromotionPiece.ROOK:
                     whiteRooks |= to;                                                
                     break;
                  case PromotionPiece.BISHOP:
                     whiteBishops |= to;                                                
                     break;
                  case PromotionPiece.KNIGHT:
                     whiteKnights |= to;                                                
                     break;
               }
            } else {
               whitePawns ^= from;
               whitePawns |= to;
            }
         }

         //... and clear any captures
         if (this.black.isAnyPieceOnPosition(to)) {
            if (this.black.isQueen(to)) {
               blackQueens= blackQueens
                     ^ to;
            } else if (this.black.isRook(to)) {
               blackRooks= blackRooks
                     ^ to;
            } else if (this.black.isBishop(to)) {
               blackBishops= blackBishops
                     ^ to;
            } else if (this.black.isKnight(to)) {
               blackKnights= blackKnights
                     ^ to;
            } else if (this.black.isPawn(to)) {
               blackPawns= blackPawns
                     ^ to;
            }
         }

         // ... and clear any enpassant captures
         if (this.black.isEnpassant(to) && this.white.isPawn(from) && ((to & Rank._6) != Square._EMPTY_BOARD)) {
            blackPawns= blackPawns
                  ^ (this.black.enpassant ^ (this.black.enpassant & WhitePawnCaptures
                        .create().getPathsFrom(
                              from)));
         }

      } else if (this.black.isAnyPieceOnPosition(from)) {
         // Move the the piece...
         if (this.black.isKing(from)) {
            blackKings= blackKings
                  ^ from
                  | to;
            if(from == Square._E8) {
               if(to == Square._C8) {
                  blackRooks= blackRooks
                  ^ Square._A8
                  | Square._D8;
               } else if (to == Square._G8) {
                  blackRooks= blackRooks
                  ^ Square._H8
                  | Square._F8;                  
               }
            }
            whiteEnpassant= Square._EMPTY_BOARD;
            blackEnpassant= Square._EMPTY_BOARD;
            blackCastling= Square._EMPTY_BOARD; 
            } else if (this.black.isQueen(from)) {
            blackQueens= blackQueens
                  ^ from
                  | to;
            whiteEnpassant= Square._EMPTY_BOARD;
            blackEnpassant= Square._EMPTY_BOARD;
         } else if (this.black.isRook(from)) {
            blackRooks= blackRooks
                  ^ from
                  | to;
            whiteEnpassant= Square._EMPTY_BOARD;
            blackEnpassant= Square._EMPTY_BOARD;
            blackCastling ^= from;
         } else if (this.black.isBishop(from)) {
            blackBishops= blackBishops
                  ^ from
                  | to;
            whiteEnpassant= Square._EMPTY_BOARD;
            blackEnpassant= Square._EMPTY_BOARD;
         } else if (this.black.isKnight(from)) {
            blackKnights= blackKnights
                  ^ from
                  | to;
            whiteEnpassant= Square._EMPTY_BOARD;
            blackEnpassant= Square._EMPTY_BOARD;
         } else if (this.black.isPawn(from)) {

            if ( ((from & Rank._7) != Square._EMPTY_BOARD) &&
                  ((to & Rank._5) != Square._EMPTY_BOARD) ) {
               blackEnpassant= BlackPawnMoves.create().getPathsFrom(
                     from);
            } else {
               blackEnpassant= Square._EMPTY_BOARD;
            }
            whiteEnpassant= Square._EMPTY_BOARD;

            if((~Rank._2 & from) == Squares._EMPTY_BOARD) {
               blackPawns ^= from;
               switch (promotionPiece) {
                  case PromotionPiece.QUEEN:
                     blackQueens |= to;                                                
                     break;
                  case PromotionPiece.ROOK:
                     blackRooks |= to;                                                
                     break;
                  case PromotionPiece.BISHOP:
                     blackBishops |= to;                                                
                     break;
                  case PromotionPiece.KNIGHT:
                     blackKnights |= to;                                                
                     break;
               }
            } else {
               blackPawns= blackPawns
                  ^ from
                  | to;
            }
         }

         //... and clear any captures
         if (this.white.isAnyPieceOnPosition(to)) {
            if (this.white.isQueen(to)) {
               whiteQueens= whiteQueens
                     ^ to;
            } else if (this.white.isRook(to)) {
               whiteRooks= whiteRooks
                     ^ to;
            } else if (this.white.isBishop(to)) {
               whiteBishops= whiteBishops
                     ^ to;
            } else if (this.white.isKnight(to)) {
               whiteKnights= whiteKnights
                     ^ to;
            } else if (this.white.isPawn(to)) {
               whitePawns= whitePawns
                     ^ to;
            } else if (this.white.isEnpassant(to)) {
               whitePawns= whitePawns
                     ^ to;
            }
         }

         // ... and clear any enpassant captures
         if (this.white.isEnpassant(to) && this.black.isPawn(from) && ((to & Rank._3) != Square._EMPTY_BOARD)) {
            whitePawns= whitePawns
                  ^ (this.white.enpassant ^ (this.white.enpassant & BlackPawnCaptures
                        .create().getPathsFrom(
                              from)));
         }

      }

      return Position.create(
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
   }

   public boolean isLegalMove(
         long from,
         long to) {
      if(this.white.isAnyPieceOnPosition(from)) {
         return this.white.getPossibleMoves().contains(new Move(from, to));
      } else if (this.black.isAnyPieceOnPosition(from)) {
         return this.black.getPossibleMoves().contains(new Move(from, to));         
      }
      return false;
   }

   
   /**
    * @deprecated Use @link{org.forritan.talvmenni.game.Strategy.getNextMove(Position position, boolean whiteToMove)} instead
    * @param whiteMove
    * @return
    */
   public Move getRandomMove(boolean whiteMove) {
      List<Move> possibleMoves;
      if(whiteMove) {
         possibleMoves= this.white.getPossibleMoves();
      } else {
         possibleMoves= this.black.getPossibleMoves();
      }
      if(!possibleMoves.isEmpty()) {
         int chosenMoveIndex= new Random().nextInt(possibleMoves.size());
         return possibleMoves.get(chosenMoveIndex);
          
      } else {
         return null;
      }      
   }
   
   public static class Move {
      public final long from;
      public final long to;

      public Move(long from, long to) {
         this.from= from;
         this.to= to;
      }
      
      public String toString() {
         Square sq= Squares.create();
         return sq.getSquareName(from)
               + sq.getSquareName(to);
      }
      
      public boolean equals(Move otherMove) {
         return (this.from == otherMove.from) 
         && (this.to == otherMove.to);
      }
      
      public boolean equals(Object obj) {
         if(obj instanceof Position.Move) {
            return this.equals((Position.Move) obj);
         } else {
            return false;
         }
      }
      
   }
   
   public static interface PromotionPiece {
      public final static int DEFAULT= 0;
      public final static int QUEEN= 0;
      public final static int ROOK= 1;
      public final static int BISHOP= 2;
      public final static int KNIGHT= 3;      
      public final static String[] STRINGS= new String[] {"q", "r", "b", "n"};
   }

   public class Bitboard {
      public final boolean  white;
      public final Position parent;

      public final long     kings;
      public final long     queens;
      public final long     rooks;
      public final long     bishops;
      public final long     knights;
      public final long     pawns;

      public final long     castling;
      public final long     enpassant;

      public final long     allPieces;

      private List<Move> possibleMoves;

      /**
       * @param white
       * @param parent
       * @param kings
       * @param queens
       * @param rooks
       * @param bishops
       * @param knights
       * @param pawns
       * @param castling
       * @param enpassant
       */
      public Bitboard(
            boolean white,
            Position parent,
            long kings,
            long queens,
            long rooks,
            long bishops,
            long knights,
            long pawns,
            long castling,
            long enpassant) {
         this.white= white;
         this.parent= parent;
         this.kings= kings;
         this.queens= queens;
         this.rooks= rooks;
         this.bishops= bishops;
         this.knights= knights;
         this.pawns= pawns;

         this.castling= castling;
         this.enpassant= enpassant;

         this.allPieces= this.kings
               | this.queens
               | this.rooks
               | this.bishops
               | this.knights
               | this.pawns;
      }
      
      public List<Move> getPossibleMoves() {
         
         if(this.possibleMoves == null) {
            this.possibleMoves= new ArrayList<Move>();

            Iterator<Long> kings= this.kingsIterator();
            while (kings.hasNext()) {
                long fromSquare= kings.next().longValue();
                findMoves(
                  this.possibleMoves,
                  fromSquare,
                  new BitboardIterator(King.attacksFrom(fromSquare, this.parent)));            
            }

            if(this.isQueensSideCastlingLegal()) {
               if(this.white) {
                  this.possibleMoves.add(new Move(Square._E1, Square._C1));
               } else {
                  this.possibleMoves.add(new Move(Square._E8, Square._C8));
               }
            }

            if(this.isKingsSideCastlingLegal()) {
               if(this.white) {
                  this.possibleMoves.add(new Position.Move(Square._E1, Square._G1));
               } else {
                  this.possibleMoves.add(new Position.Move(Square._E8, Square._G8));
               }               
            }
         
            Iterator<Long> queens= this.queensIterator(); 
            while (queens.hasNext()) {
                long fromSquare= queens.next().longValue();
                findMoves(
                        this.possibleMoves,
                        fromSquare,
                        new BitboardIterator(Queen.attacksFrom(fromSquare, this.parent)));            
            }
         
            Iterator<Long> rooks= this.rooksIterator(); 
            while (rooks.hasNext()) {
                long fromSquare= rooks.next().longValue();
                findMoves(
                        this.possibleMoves,
                        fromSquare,
                        new BitboardIterator(Rook.attacksFrom(fromSquare, this.parent)));            
            }
         
            Iterator<Long> bishops= this.bishopsIterator(); 
            while (bishops.hasNext()) {
                long fromSquare= bishops.next().longValue();
                findMoves(
                  this.possibleMoves,
                  fromSquare,
                  new BitboardIterator(Bishop.attacksFrom(fromSquare, this.parent)));
            }
         
            Iterator<Long> knights= this.knightsIterator(); 
            while (knights.hasNext()) {
                long fromSquare= knights.next().longValue();
                findMoves(
                  this.possibleMoves,
                  fromSquare,
                  new BitboardIterator(Knight.attacksFrom(fromSquare, this.parent)));            
            }
         
            Iterator<Long> pawns= this.pawnsIterator();
            while (pawns.hasNext()) {
                long fromSquare= pawns.next().longValue();

                if(this.white) {
                    findMoves(
                     this.possibleMoves,
                     fromSquare,
                     new BitboardIterator(WhitePawn.captureMoveAttacksFrom(fromSquare, this.parent) | WhitePawn.moveAttacksFrom(fromSquare, this.parent)));            
                } else {
                  findMoves(
                        this.possibleMoves,
                        fromSquare,
                        new BitboardIterator(BlackPawn.captureMoveAttacksFrom(fromSquare, this.parent) | BlackPawn.moveAttacksFrom(fromSquare, this.parent)));            
                }
            }
         }
         return this.possibleMoves;          
      }

      private void findMoves(
            List<Move> result,
            long fromSquare,
            Iterator<Long> moves) {
         while (moves.hasNext()) {
            long toSquare= moves.next().longValue();
            Position newPosition= this.parent.move(fromSquare, toSquare, PromotionPiece.DEFAULT);
            if(this.white) {
               if(!newPosition.white.isChecked()) {
                  result.add(new Move(fromSquare, toSquare));
               } 
            } else {
               if(!newPosition.black.isChecked()) {
                  result.add(new Move(fromSquare, toSquare));                  
               }
            }
            newPosition= null;
         }
      }

      /**
       * Is "castling kings side" (o-o) legal?
       */
      public boolean isKingsSideCastlingLegal() {

         boolean piecesNotMoved;

         if (white) {
            piecesNotMoved= (this.castling & Square._E1) != Square._EMPTY_BOARD 
            && (this.castling & Square._H1) != Square._EMPTY_BOARD;
         } else {
            piecesNotMoved= (this.castling & Square._E8) != Square._EMPTY_BOARD 
            && (this.castling & Square._H8) != Square._EMPTY_BOARD;
         }
         
         boolean result= false;

         if (piecesNotMoved) {
            if (white) {
               result= ((this.parent.black.getAllCaptureMovesAttackedSquares() & (Square._E1
                     | Square._F1 | Square._G1)) == Square._EMPTY_BOARD) &&
                     (( (this.allPieces | this.parent.black.allPieces) & (Square._F1 | Square._G1)) == Square._EMPTY_BOARD);
               } else {
               result= ((this.parent.white.getAllCaptureMovesAttackedSquares() & (Square._E8
                     | Square._F8 | Square._G8)) == Square._EMPTY_BOARD) &&
                     (( (this.allPieces | this.parent.white.allPieces) & (Square._F8 | Square._G8)) == Square._EMPTY_BOARD);
            }            
         }        
         return result;

      }

      /**
       * Is "castling queens side" (o-o-o) legal?
       */
      public boolean isQueensSideCastlingLegal() {

         boolean piecesNotMoved;

         if (white) {
            piecesNotMoved= (this.castling & Square._A1) != Square._EMPTY_BOARD 
                             && (this.castling & Square._E1) != Square._EMPTY_BOARD;
         } else {
            piecesNotMoved= (this.castling & Square._A8) != Square._EMPTY_BOARD 
                             && (this.castling & Square._E8) != Square._EMPTY_BOARD;
         }
         
         boolean result= false;

         if (piecesNotMoved) {
            if (white) {
               result= ((this.parent.black.getAllCaptureMovesAttackedSquares() & (Square._C1
                     | Square._D1 | Square._E1)) == Square._EMPTY_BOARD) &&
                     (( (this.allPieces | this.parent.black.allPieces) & (Square._B1 | Square._C1 | Square._D1)) == Square._EMPTY_BOARD);
            } else {
               result= ((this.parent.white.getAllCaptureMovesAttackedSquares() & (Square._C8
                     | Square._D8 | Square._E8)) == Square._EMPTY_BOARD) &&
                     (( (this.allPieces | this.parent.white.allPieces) & (Square._B8 | Square._C8 | Square._D8)) == Square._EMPTY_BOARD);
            }
         }
         return result;
      }

      public long getAllPawnsReadyForPromotion() {
         return Square._EMPTY_BOARD;
      }

      public boolean isChecked() {
         long allSquaresUnderAttackByOppositionPieces;
         if(this.white) {
            allSquaresUnderAttackByOppositionPieces= this.parent.black.getAllCaptureMovesAttackedSquares();
         } else {
            allSquaresUnderAttackByOppositionPieces= this.parent.white.getAllCaptureMovesAttackedSquares();
         }
         return (allSquaresUnderAttackByOppositionPieces & this.kings) != Squares._EMPTY_BOARD;
      }
      
      public Iterator<Long> allPiecesIterator() {
         return new BitboardIterator(
               this.allPieces);
      }

      public Iterator<Long> kingsIterator() {
         return new BitboardIterator(
               this.kings);
      }

      public Iterator<Long> queensIterator() {
         return new BitboardIterator(
               this.queens);
      }

      public Iterator<Long> rooksIterator() {
         return new BitboardIterator(
               this.rooks);
      }

      public Iterator<Long> bishopsIterator() {
         return new BitboardIterator(
               this.bishops);
      }

      public Iterator<Long> knightsIterator() {
         return new BitboardIterator(
               this.knights);
      }

      public Iterator<Long> pawnsIterator() {
         return new BitboardIterator(
               this.pawns);
      }

      public boolean isAnyPieceOnPosition(
            long position) {
         return ((this.allPieces & position) != Square._EMPTY_BOARD);
      }

      public boolean isKing(
            long position) {
         return ((this.kings & position) != Square._EMPTY_BOARD);
      }

      public boolean isQueen(
            long position) {
         return ((this.queens & position) != Square._EMPTY_BOARD);
      }

      public boolean isRook(
            long position) {
         return ((this.rooks & position) != Square._EMPTY_BOARD);
      }

      public boolean isBishop(
            long position) {
         return ((this.bishops & position) != Square._EMPTY_BOARD);
      }

      public boolean isKnight(
            long position) {
         return ((this.knights & position) != Square._EMPTY_BOARD);
      }

      public boolean isPawn(
            long position) {
         return ((this.pawns & position) != Square._EMPTY_BOARD);
      }

      public boolean isEnpassant(
            long position) {
         return ((this.enpassant & position) != Square._EMPTY_BOARD);
      }

      public long getAllCaptureMovesAttackedSquares() {
         long result= Squares._EMPTY_BOARD;

         Iterator<Long> kings= this.kingsIterator();
         while (kings.hasNext()) {
            long square= kings.next().longValue();
            result|= King.attacksFrom(
                  square,
                  this.parent);
         }

         Iterator<Long> queens= this.queensIterator();
         while (queens.hasNext()) {
            long square= queens.next().longValue();
            result|= Queen.attacksFrom(
                  square,
                  this.parent);
         }

         Iterator<Long> rooks= this.rooksIterator();
         while (rooks.hasNext()) {
            long square= rooks.next().longValue();
            result|= Rook.attacksFrom(
                  square,
                  this.parent);
         }

         Iterator<Long> bishops= this.bishopsIterator();
         while (bishops.hasNext()) {
            long square= bishops.next().longValue();
            result|= Bishop.attacksFrom(
                  square,
                  this.parent);
         }

         Iterator<Long> knights= this.knightsIterator();
         while (knights.hasNext()) {
            long square= knights.next().longValue();
            result|= Knight.attacksFrom(
                  square,
                  this.parent);
         }

         Iterator<Long> pawns= this.pawnsIterator();
         while (pawns.hasNext()) {
            if (this.white) {
               long square= pawns.next().longValue();
               result|= WhitePawn.captureMoveAttacksFrom(
                     square,
                     this.parent);
            } else {
               long square= pawns.next().longValue();
               result|= BlackPawn.captureMoveAttacksFrom(
                     square,
                     this.parent);
            }
         }

         return result;
      }
   }

}