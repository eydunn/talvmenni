package org.forritan.talvmenni.knowledge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

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
import org.forritan.talvmenni.bitboard.paths.BlackPawnMoves;
import org.forritan.talvmenni.bitboard.paths.WhitePawnMoves;


public interface Position extends Serializable {

   public static final long serialVersionUID = 1L;

   public Bitboard getBlack();

   public Bitboard getWhite();

   public Position getImmutable();

   public Position getMutable();

   public Position move(
         long from,
         long to);

   public Position move(
         long from,
         long to,
         int promotionPiece);

   public boolean isLegalMove(
         long from,
         long to);

   public boolean isPromotionMove(
         long from,
         long to);

   public Position pushMove(
         Bitboard white,
         Bitboard black);

   public Position popMove();

   public class Bitboard implements Serializable {

      public static final long serialVersionUID = 1L;

      public final int         hashCode;
      public final int         partitionSearchHashCode;

      public final boolean     whiteBoard;
      public final Position    parent;

      public final long        kings;
      public final long        queens;
      public final long        rooks;
      public final long        bishops;
      public final long        knights;
      public final long        pawns;

      public final long        castling;
      public final long        enpassant;

      public final long        allPieces;

      private List             possibleMoves;
      private List             possibleCaptureMoves;
      private List             betterOrderMoves;
      private long             allCaptureMovesAttackedSquares;
      private boolean          allCaptureMovesAttackedSquaresInitialized;
      private Boolean          kingsSideCastlingLegal;
      private Boolean          queensSideCastlingLegal;

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
            boolean whiteBoard,
            Position parent,
            long kings,
            long queens,
            long rooks,
            long bishops,
            long knights,
            long pawns,
            long castling,
            long enpassant) {
         this.whiteBoard= whiteBoard;
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

         int SEED= 17;
         int PRIME_FACTOR= 37;
         int hash= SEED;
         hash= PRIME_FACTOR
               * hash
               + ((int) (this.kings ^ (this.kings >>> 32)));
         hash= PRIME_FACTOR
               * hash
               + ((int) (this.queens ^ (this.queens >>> 32)));
         hash= PRIME_FACTOR
               * hash
               + ((int) (this.rooks ^ (this.rooks >>> 32)));
         hash= PRIME_FACTOR
               * hash
               + ((int) (this.bishops ^ (this.bishops >>> 32)));
         hash= PRIME_FACTOR
               * hash
               + ((int) (this.knights ^ (this.knights >>> 32)));
         hash= PRIME_FACTOR
               * hash
               + ((int) (this.castling ^ (this.castling >>> 32)));
         hash= PRIME_FACTOR
               * hash
               + (whiteBoard ? 0 : 1);

         int partitionSearchHash= hash;

         partitionSearchHash= PRIME_FACTOR
               * partitionSearchHash
               + Bitboard.populationCount(pawns);

         partitionSearchHash= PRIME_FACTOR
               * partitionSearchHash
               + Bitboard.populationCount(enpassant);

         this.partitionSearchHashCode= partitionSearchHash;

         hash= PRIME_FACTOR
               * hash
               + ((int) (this.pawns ^ (this.pawns >>> 32)));
         hash= PRIME_FACTOR
               * hash
               + ((int) (this.enpassant ^ (this.enpassant >>> 32)));

         this.hashCode= hash;

      }

      public static int populationCount(
            long i) {
         i= i
               - ((i >>> 1) & 0x5555555555555555L);
         i= (i & 0x3333333333333333L)
               + ((i >>> 2) & 0x3333333333333333L);
         i= (i + (i >>> 4)) & 0x0f0f0f0f0f0f0f0fL;
         i= i
               + (i >>> 8);
         i= i
               + (i >>> 16);
         i= i
               + (i >>> 32);
         return (int) i & 0x7f;
      }

      public boolean equals(
            Object o) {
         if (o instanceof Bitboard) {
            return (this.whiteBoard == ((Bitboard) o).whiteBoard)
                  && (this.kings == ((Bitboard) o).kings)
                  && (this.queens == ((Bitboard) o).queens)
                  && (this.rooks == ((Bitboard) o).rooks)
                  && (this.bishops == ((Bitboard) o).bishops)
                  && (this.knights == ((Bitboard) o).knights)
                  && (this.pawns == ((Bitboard) o).pawns)
                  && (this.castling == ((Bitboard) o).castling)
                  && (this.enpassant == ((Bitboard) o).enpassant);
         } else {
            return false;
         }
      }

      public boolean partitionSearchEquals(
            Object o) {
         if (o instanceof Bitboard) {
            return (Bitboard.populationCount(this.pawns) == Bitboard
                  .populationCount(((Bitboard) o).pawns))
                  && (Bitboard.populationCount(this.enpassant) == Bitboard
                        .populationCount(((Bitboard) o).enpassant))
                  && (this.whiteBoard == ((Bitboard) o).whiteBoard)
                  && (this.kings == ((Bitboard) o).kings)
                  && (this.queens == ((Bitboard) o).queens)
                  && (this.rooks == ((Bitboard) o).rooks)
                  && (this.bishops == ((Bitboard) o).bishops)
                  && (this.knights == ((Bitboard) o).knights)
                  && (this.castling == ((Bitboard) o).castling);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.hashCode;
      }

      public int partitionSearchHashCode() {
         return this.partitionSearchHashCode;
      }

      public void betterMove(
            Move move) {
         if (this.betterOrderMoves == null) {
            this.betterOrderMoves= new ArrayList();
         }
         this.betterOrderMoves.add(
               0,
               move);
      }

      public void updatePossibleMovesOrdering() {
         if (this.possibleMoves != null
               && this.betterOrderMoves != null) {
            List currentPossibleMoves= new ArrayList();
            currentPossibleMoves.addAll(this.possibleMoves);
            this.possibleMoves= new ArrayList();
            this.possibleMoves.addAll(this.betterOrderMoves);
            currentPossibleMoves.removeAll(this.betterOrderMoves);
            this.possibleMoves.addAll(currentPossibleMoves);
            this.betterOrderMoves= null;
         }
      }

      public List getPossibleCaptureMoves() {
         if (this.possibleCaptureMoves == null) {
            this.possibleCaptureMoves= new ArrayList();

            long allPossibleOpponentCaptures= this
                  .getAllCaptureMovesAttackedSquares();
            
            if (this.whiteBoard) {
               allPossibleOpponentCaptures &= this.parent.getBlack().allPieces;
            } else {
               allPossibleOpponentCaptures &= this.parent.getWhite().allPieces;
            }
            
            for(Iterator it= this.getPossibleMoves().iterator(); it.hasNext(); ) {
               Move move= (Move) it.next();
               if((move.to & allPossibleOpponentCaptures) != Square._EMPTY_BOARD) {
                  this.possibleCaptureMoves.add(0, move);
               }
            }
         }
         return this.possibleCaptureMoves;
      }

      public List getPossibleMoves() {
         if (this.possibleMoves == null) {
            this.possibleMoves= new ArrayList();

            BitboardIterator rooks= this.rooksIterator();
            while (rooks.hasNext()) {
               long fromSquare= rooks.nextBitboard();
               findMoves(
                     this.possibleMoves,
                     fromSquare,
                     new BitboardIterator(
                           Rook.attacksFrom(
                                 fromSquare,
                                 this.parent)));
            }

            BitboardIterator bishops= this.bishopsIterator();
            while (bishops.hasNext()) {
               long fromSquare= bishops.nextBitboard();
               findMoves(
                     this.possibleMoves,
                     fromSquare,
                     new BitboardIterator(
                           Bishop.attacksFrom(
                                 fromSquare,
                                 this.parent)));
            }

            BitboardIterator knights= this.knightsIterator();
            while (knights.hasNext()) {
               long fromSquare= knights.nextBitboard();
               findMoves(
                     this.possibleMoves,
                     fromSquare,
                     new BitboardIterator(
                           Knight.attacksFrom(
                                 fromSquare,
                                 this.parent)));
            }

            BitboardIterator queens= this.queensIterator();
            while (queens.hasNext()) {
               long fromSquare= queens.nextBitboard();
               findMoves(
                     this.possibleMoves,
                     fromSquare,
                     new BitboardIterator(
                           Queen.attacksFrom(
                                 fromSquare,
                                 this.parent)));
            }

            BitboardIterator pawns= this.pawnsIterator();
            while (pawns.hasNext()) {
               long fromSquare= pawns.nextBitboard();

               if (this.whiteBoard) {
                  findMoves(
                        this.possibleMoves,
                        fromSquare,
                        new BitboardIterator(
                              WhitePawn.captureMoveAttacksFrom(
                                    fromSquare,
                                    this.parent)
                                    | WhitePawn.moveAttacksFrom(
                                          fromSquare,
                                          this.parent)));
               } else {
                  findMoves(
                        this.possibleMoves,
                        fromSquare,
                        new BitboardIterator(
                              BlackPawn.captureMoveAttacksFrom(
                                    fromSquare,
                                    this.parent)
                                    | BlackPawn.moveAttacksFrom(
                                          fromSquare,
                                          this.parent)));
               }
            }

            BitboardIterator kings= this.kingsIterator();
            while (kings.hasNext()) {
               long fromSquare= kings.nextBitboard();
               findMoves(
                     this.possibleMoves,
                     fromSquare,
                     new BitboardIterator(
                           King.attacksFrom(
                                 fromSquare,
                                 this.parent)));
            }

            if (this.isQueensSideCastlingLegal()) {
               if (this.whiteBoard) {
                  this.possibleMoves.add(new Move(
                        Square._E1,
                        Square._C1,
                        PromotionPiece.NONE));
               } else {
                  this.possibleMoves.add(new Move(
                        Square._E8,
                        Square._C8,
                        PromotionPiece.NONE));
               }
            }

            if (this.isKingsSideCastlingLegal()) {
               if (this.whiteBoard) {
                  this.possibleMoves.add(new Move(
                        Square._E1,
                        Square._G1,
                        PromotionPiece.NONE));
               } else {
                  this.possibleMoves.add(new Move(
                        Square._E8,
                        Square._G8,
                        PromotionPiece.NONE));
               }
            }
         }
         return this.possibleMoves;
      }

      private void findMoves(
            List result,
            long fromSquare,
            BitboardIterator moves) {
         while (moves.hasNext()) {
            long toSquare= moves.nextBitboard();
            Position newPosition;
            if (this.parent.isPromotionMove(
                  fromSquare,
                  toSquare)) {
               for (int promoteTo= PromotionPiece.QUEEN; promoteTo <= PromotionPiece.KNIGHT; promoteTo++) {
                  newPosition= this.parent.move(
                        fromSquare,
                        toSquare,
                        promoteTo);
                  this.addMoveToResultIfNotInCheck(
                        result,
                        fromSquare,
                        toSquare,
                        promoteTo,
                        newPosition);
                  this.parent.popMove();
               }
            } else {
               newPosition= this.parent.move(
                     fromSquare,
                     toSquare,
                     PromotionPiece.NONE);
               this.addMoveToResultIfNotInCheck(
                     result,
                     fromSquare,
                     toSquare,
                     PromotionPiece.NONE,
                     newPosition);
               this.parent.popMove();
            }
            newPosition= null;
         }
      }

      /**
       * @param result
       * @param fromSquare
       * @param toSquare
       * @param newPosition
       */
      private void addMoveToResultIfNotInCheck(
            List result,
            long fromSquare,
            long toSquare,
            int promotionPiece,
            Position newPosition) {
         if (this.whiteBoard) {
            if (!newPosition.getWhite().isChecked()) {
               result.add(new Move(
                     fromSquare,
                     toSquare,
                     promotionPiece));
            }
         } else {
            if (!newPosition.getBlack().isChecked()) {
               result.add(new Move(
                     fromSquare,
                     toSquare,
                     promotionPiece));
            }
         }
      }

      /**
       * Is "castling kings side" (o-o) legal?
       */
      public boolean isKingsSideCastlingLegal() {

         if (this.kingsSideCastlingLegal == null) {

            boolean piecesNotMoved;

            if (whiteBoard) {
               piecesNotMoved= (this.castling & Square._E1) != Square._EMPTY_BOARD
                     && (this.castling & Square._H1) != Square._EMPTY_BOARD;
            } else {
               piecesNotMoved= (this.castling & Square._E8) != Square._EMPTY_BOARD
                     && (this.castling & Square._H8) != Square._EMPTY_BOARD;
            }

            boolean result= false;

            if (piecesNotMoved) {
               if (whiteBoard) {
                  result= ((this.parent.getBlack()
                        .getAllCaptureMovesAttackedSquares() & (Square._E1
                        | Square._F1 | Square._G1)) == Square._EMPTY_BOARD)
                        && (((this.allPieces | this.parent.getBlack().allPieces) & (Square._F1 | Square._G1)) == Square._EMPTY_BOARD);
               } else {
                  result= ((this.parent.getWhite()
                        .getAllCaptureMovesAttackedSquares() & (Square._E8
                        | Square._F8 | Square._G8)) == Square._EMPTY_BOARD)
                        && (((this.allPieces | this.parent.getWhite().allPieces) & (Square._F8 | Square._G8)) == Square._EMPTY_BOARD);
               }
            }
            this.kingsSideCastlingLegal= Boolean.valueOf(result);
         }
         return this.kingsSideCastlingLegal.booleanValue();
      }

      /**
       * Is "castling queens side" (o-o-o) legal?
       */
      public boolean isQueensSideCastlingLegal() {

         if (this.queensSideCastlingLegal == null) {

            boolean piecesNotMoved;

            if (whiteBoard) {
               piecesNotMoved= (this.castling & Square._A1) != Square._EMPTY_BOARD
                     && (this.castling & Square._E1) != Square._EMPTY_BOARD;
            } else {
               piecesNotMoved= (this.castling & Square._A8) != Square._EMPTY_BOARD
                     && (this.castling & Square._E8) != Square._EMPTY_BOARD;
            }

            boolean result= false;

            if (piecesNotMoved) {
               if (whiteBoard) {
                  result= ((this.parent.getBlack()
                        .getAllCaptureMovesAttackedSquares() & (Square._C1
                        | Square._D1 | Square._E1)) == Square._EMPTY_BOARD)
                        && (((this.allPieces | this.parent.getBlack().allPieces) & (Square._B1
                              | Square._C1 | Square._D1)) == Square._EMPTY_BOARD);
               } else {
                  result= ((this.parent.getWhite()
                        .getAllCaptureMovesAttackedSquares() & (Square._C8
                        | Square._D8 | Square._E8)) == Square._EMPTY_BOARD)
                        && (((this.allPieces | this.parent.getWhite().allPieces) & (Square._B8
                              | Square._C8 | Square._D8)) == Square._EMPTY_BOARD);
               }
            }
            this.queensSideCastlingLegal= Boolean.valueOf(result);
         }
         return this.queensSideCastlingLegal.booleanValue();
      }

      public long getAllPawnsReadyForPromotion() {
         //TODO: Implementation...
         return Square._EMPTY_BOARD;
      }

      public boolean isChecked() {
         long allSquaresUnderAttackByOppositionPieces;
         if (this.whiteBoard) {
            allSquaresUnderAttackByOppositionPieces= this.parent.getBlack()
                  .getAllCaptureMovesAttackedSquares();
         } else {
            allSquaresUnderAttackByOppositionPieces= this.parent.getWhite()
                  .getAllCaptureMovesAttackedSquares();
         }
         return (allSquaresUnderAttackByOppositionPieces & this.kings) != Squares._EMPTY_BOARD;
      }

      public BitboardIterator allPiecesIterator() {
         return new BitboardIterator(
               this.allPieces);
      }

      public BitboardIterator kingsIterator() {
         return new BitboardIterator(
               this.kings);
      }

      public BitboardIterator queensIterator() {
         return new BitboardIterator(
               this.queens);
      }

      public BitboardIterator rooksIterator() {
         return new BitboardIterator(
               this.rooks);
      }

      public BitboardIterator bishopsIterator() {
         return new BitboardIterator(
               this.bishops);
      }

      public BitboardIterator knightsIterator() {
         return new BitboardIterator(
               this.knights);
      }

      public BitboardIterator pawnsIterator() {
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

         if (!this.allCaptureMovesAttackedSquaresInitialized) {
            this.allCaptureMovesAttackedSquaresInitialized= true;

            long result= Square._EMPTY_BOARD;

            BitboardIterator kings= this.kingsIterator();
            while (kings.hasNext()) {
               long square= kings.nextBitboard();
               result|= King.attacksFrom(
                     square,
                     this.parent);
            }

            BitboardIterator queens= this.queensIterator();
            while (queens.hasNext()) {
               long square= queens.nextBitboard();
               result|= Queen.attacksFrom(
                     square,
                     this.parent);
            }

            BitboardIterator rooks= this.rooksIterator();
            while (rooks.hasNext()) {
               long square= rooks.nextBitboard();
               result|= Rook.attacksFrom(
                     square,
                     this.parent);
            }

            BitboardIterator bishops= this.bishopsIterator();
            while (bishops.hasNext()) {
               long square= bishops.nextBitboard();
               result|= Bishop.attacksFrom(
                     square,
                     this.parent);
            }

            BitboardIterator knights= this.knightsIterator();
            while (knights.hasNext()) {
               long square= knights.nextBitboard();
               result|= Knight.attacksFrom(
                     square,
                     this.parent);
            }

            BitboardIterator pawns= this.pawnsIterator();
            while (pawns.hasNext()) {
               if (this.whiteBoard) {
                  long square= pawns.nextBitboard();
                  result|= WhitePawn.captureMoveAttacksFrom(
                        square,
                        this.parent);
               } else {
                  long square= pawns.nextBitboard();
                  result|= BlackPawn.captureMoveAttacksFrom(
                        square,
                        this.parent);
               }
            }
            this.allCaptureMovesAttackedSquares= result;
         }
         return this.allCaptureMovesAttackedSquares;
      }
   }

   public static interface PromotionPiece extends Serializable {

      public static final long     serialVersionUID = 1L;

      public final static int      NONE             = 4;

      public final static int      QUEEN            = 0;
      public final static int      ROOK             = 1;
      public final static int      BISHOP           = 2;
      public final static int      KNIGHT           = 3;
      public final static String[] STRINGS          = new String[] {
                                                          "q",
                                                          "r",
                                                          "b",
                                                          "n",
                                                          ""};
   }

   public static class Move implements Serializable {

      public static final long serialVersionUID = 1L;

      public final static int  SEED             = 17;
      public final static int  PRIME_FACTOR     = 37;

      public final long        from;
      public final long        to;
      public final int         promotionPiece;

      public final int         hashCode;

      public Move(
            long from,
            long to,
            int promotionPiece) {
         this.from= from;
         this.to= to;
         this.promotionPiece= promotionPiece;

         int hash= SEED;
         hash= PRIME_FACTOR
               * hash
               + ((int) (this.from ^ (this.from >>> 32)));
         hash= PRIME_FACTOR
               * hash
               + ((int) (this.to ^ (this.to >>> 32)));
         this.hashCode= hash;
      }

      public String toString() {
         Square sq= Squares.create();
         return sq.getSquareName(from)
               + sq.getSquareName(to)
               + (this.promotionPiece == PromotionPiece.NONE ? ""
                     : PromotionPiece.STRINGS[this.promotionPiece]);
      }

      public boolean equals(
            Move otherMove) {
         return (this.from == otherMove.from)
               && (this.to == otherMove.to);
      }

      public boolean equalsIncludingPromotion(
            Move otherMove) {
         return (this.from == otherMove.from)
               && (this.to == otherMove.to)
               && (this.promotionPiece == otherMove.promotionPiece);
      }

      public boolean equals(
            Object obj) {
         if (obj instanceof Move) {
            return this.equals((Move) obj);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.hashCode;
      }

   }

   public static class Factory {
      public static Position createInitial(
            boolean partitionSearchPosition,
            boolean mutable) {
         if (mutable
               && !partitionSearchPosition) {
            return new MutablePosition(
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
         } else if (!mutable
               && !partitionSearchPosition) {
            return new ImmutablePosition(
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
         } else if (mutable
               && partitionSearchPosition) {
            return new MutablePartitionSearchPosition(
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
         } else if (!mutable
               && partitionSearchPosition) { return new ImmutablePartitionSearchPosition(
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
         ); }
         return null;
      }

      /**
       * @param mutable
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
            boolean partitionSearchPosition,
            boolean mutable,
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
         if (mutable
               && !partitionSearchPosition) {
            return new MutablePosition(
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
         } else if (!mutable
               && !partitionSearchPosition) {
            return new ImmutablePosition(
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
         } else if (mutable
               && partitionSearchPosition) {
            return new MutablePartitionSearchPosition(
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
         } else if (!mutable
               && partitionSearchPosition) { return new ImmutablePartitionSearchPosition(
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
               blackEnpassant); }
         return null;
      }

      public static Position create(
            boolean partitionSearchPosition,
            boolean mutable,
            Bitboard white,
            Bitboard black) {
         if (mutable
               && !partitionSearchPosition) {
            return new MutablePosition(
                  white,
                  black);
         } else if (!mutable
               && !partitionSearchPosition) {
            return new ImmutablePosition(
                  white,
                  black);
         } else if (mutable
               && partitionSearchPosition) {
            return new MutablePartitionSearchPosition(
                  white,
                  black);
         } else if (!mutable
               && partitionSearchPosition) { return new ImmutablePartitionSearchPosition(
               white,
               black); }

         return null;

      }

      public static ColorPosition createPositionFromFEN(
            boolean partitionSearchPosition,
            boolean mutable,
            String FENString) {

         Boolean whiteMove= null;

         long whiteKings= Square._EMPTY_BOARD;
         long whiteQueens= Square._EMPTY_BOARD;
         long whiteRooks= Square._EMPTY_BOARD;
         long whiteBishops= Square._EMPTY_BOARD;
         long whiteKnights= Square._EMPTY_BOARD;
         long whitePawns= Square._EMPTY_BOARD;
         long whiteCastling= Square._EMPTY_BOARD;
         long whiteEnpassant= Square._EMPTY_BOARD;
         long blackKings= Square._EMPTY_BOARD;
         long blackQueens= Square._EMPTY_BOARD;
         long blackRooks= Square._EMPTY_BOARD;
         long blackBishops= Square._EMPTY_BOARD;
         long blackKnights= Square._EMPTY_BOARD;
         long blackPawns= Square._EMPTY_BOARD;
         long blackCastling= Square._EMPTY_BOARD;
         long blackEnpassant= Square._EMPTY_BOARD;

         Square square2= Squares.create();
         long sq;

         StringTokenizer st= new StringTokenizer(
               FENString);

         String positionString= st.nextToken();

         int y= 0;
         int x= 0;

         for (int pos= 0; pos < positionString.length(); pos++) {
            char activeChar= positionString.charAt(pos);

            if (activeChar == '/') {
               y++;
               x= 0;
            } else if (Character.isDigit(activeChar)) {
               x+= Integer.parseInt(""
                     + activeChar);
            } else {
               int square= y
                     * 8
                     + x;
               sq= square2.getSquareNormalizedIndex(square);

               if (activeChar == 'r') {
                  blackRooks|= sq;
               } else if (activeChar == 'R') {
                  whiteRooks|= sq;
               } else if (activeChar == 'b') {
                  blackBishops|= sq;
               } else if (activeChar == 'B') {
                  whiteBishops|= sq;
               } else if (activeChar == 'q') {
                  blackQueens|= sq;
               } else if (activeChar == 'Q') {
                  whiteQueens|= sq;
               } else if (activeChar == 'k') {
                  blackKings|= sq;
               } else if (activeChar == 'K') {
                  whiteKings|= sq;
               } else if (activeChar == 'n') {
                  blackKnights|= sq;
               } else if (activeChar == 'N') {
                  whiteKnights|= sq;
               } else if (activeChar == 'p') {
                  blackPawns|= sq;
               } else if (activeChar == 'P') {
                  whitePawns|= sq;
               }

               x++;
            }
         }

         if (st.hasMoreTokens()) {
            String colorToMove= st.nextToken();
            if (colorToMove.equals("w")) {
               whiteMove= Boolean.TRUE;
            } else if (colorToMove.equals("b")) {
               whiteMove= Boolean.FALSE;
            }
         } else {
            // Malformed FEN string
            throw new IllegalArgumentException(
                  "FENString malformed - must say which side is next to move");
         }

         if (st.hasMoreTokens()) {
            String castlingString= st.nextToken();

            for (int pos= 0; pos < castlingString.length(); pos++) {
               char activeChar= castlingString.charAt(pos);
               if (activeChar == 'K') {
                  whiteCastling|= Square._E1;
                  whiteCastling|= Square._H1;
               }
               if (activeChar == 'Q') {
                  whiteCastling|= Square._E1;
                  whiteCastling|= Square._A1;
               }
               if (activeChar == 'k') {
                  blackCastling|= Square._E8;
                  blackCastling|= Square._H8;
               }
               if (activeChar == 'q') {
                  blackCastling|= Square._E8;
                  blackCastling|= Square._A8;
               }
            }
         }

         if (st.hasMoreTokens()) {
            String targetSquareStr= st.nextToken().toUpperCase();
            if (!"-".equalsIgnoreCase(targetSquareStr)) {
               long targetSquare= Squares.create().getSquare(
                     targetSquareStr.toUpperCase());
               if (whiteMove.booleanValue()) {
                  if ((blackPawns & (targetSquare >> 8)) != Square._EMPTY_BOARD) {
                     blackEnpassant= BlackPawnMoves.create().getPathsFrom(
                           (targetSquare << 8));
                  }
               } else {
                  if ((whitePawns & (targetSquare << 8)) != Square._EMPTY_BOARD) {
                     whiteEnpassant= WhitePawnMoves.create().getPathsFrom(
                           (targetSquare >> 8));
                  }
               }
            }
         }

         Position fenPosition= Position.Factory.create(
               partitionSearchPosition,
               mutable,
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

         return new ColorPosition(
               fenPosition,
               whiteMove);
      }
   }

   public static class ColorPosition implements Serializable {

      public static final long serialVersionUID = 1L;

      public Position          position;
      public Boolean           whiteToMove;

      public ColorPosition(
            Position position,
            Boolean whiteMove) {
         this.position= position;
         this.whiteToMove= whiteMove;
      }
   }
}