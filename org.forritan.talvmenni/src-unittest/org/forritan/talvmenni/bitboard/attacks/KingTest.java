package org.forritan.talvmenni.bitboard.attacks;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.forritan.talvmenni.bitboard.Square;
import org.forritan.talvmenni.knowledge.ImmutablePosition;
import org.forritan.talvmenni.knowledge.Move;
import org.forritan.talvmenni.knowledge.Position;


public class KingTest extends TestCase {

   private final String[] zeroPrefix = new String[64];

   public KingTest() {
      for (int i= 0, j= 64; i < zeroPrefix.length; i++, j--) {
         StringBuffer zeroes= new StringBuffer();
         for (int k= 1; k < j; k++) {
            zeroes.append('0');
         }
         zeroPrefix[i]= zeroes.toString();
      }
   }

   public void testWhiteKingInitial() {
      Position initialPosition= Position.Factory.createInitial(
            false,
            false);
      long kingAttacksFromE1= King.attacksFrom(
            Square._E1,
            initialPosition);

      Assert.assertEquals(
            ""
                  + //
                  "00000000"
                  + // A8-H8
                  "00000000"
                  + // A7-H7
                  "00000000"
                  + // A6-H6
                  "00000000"
                  + // A5-H5
                  "00000000"
                  + // A4-H4
                  "00000000"
                  + // A3-H3
                  "00000000"
                  + // A2-H2
                  "00000000", // A1-H1
            (this.zeroPrefix[Long.toBinaryString(
                  kingAttacksFromE1).length() - 1] + Long
                  .toBinaryString(kingAttacksFromE1)));

   }

   public void testWhiteKingAfterD2D4() {
      Position p= Position.Factory.createInitial(
            false,
            false).move(
            new Position.Move(
                  Square._D2,
                  Square._D4,
                  Position.PromotionPiece.NONE));
      long kingAttacksFromE1= King.attacksFrom(
            Square._E1,
            p);

      Assert.assertEquals(
            ""
                  + //
                  "00000000"
                  + // A8-H8
                  "00000000"
                  + // A7-H7
                  "00000000"
                  + // A6-H6
                  "00000000"
                  + // A5-H5
                  "00000000"
                  + // A4-H4
                  "00000000"
                  + // A3-H3
                  "00010000"
                  + // A2-H2
                  "00000000", // A1-H1
            (this.zeroPrefix[Long.toBinaryString(
                  kingAttacksFromE1).length() - 1] + Long
                  .toBinaryString(kingAttacksFromE1)));

   }

   public void testWhiteKingAfterD2D4_E2E4() {
      Position p= Position.Factory.createInitial(
            false,
            false).move(
            new Position.Move(
                  Square._D2,
                  Square._D4,
                  Position.PromotionPiece.NONE)).move(
            new Position.Move(
                  Square._E2,
                  Square._E4,
                  Position.PromotionPiece.NONE));
      long kingAttacksFromE1= King.attacksFrom(
            Square._E1,
            p);

      Assert.assertEquals(
            ""
                  + //
                  "00000000"
                  + // A8-H8
                  "00000000"
                  + // A7-H7
                  "00000000"
                  + // A6-H6
                  "00000000"
                  + // A5-H5
                  "00000000"
                  + // A4-H4
                  "00000000"
                  + // A3-H3
                  "00011000"
                  + // A2-H2
                  "00000000", // A1-H1
            (this.zeroPrefix[Long.toBinaryString(
                  kingAttacksFromE1).length() - 1] + Long
                  .toBinaryString(kingAttacksFromE1)));

   }

   public void testWhiteKingAfterD2D3_E2E3_F2F4() {
      Position p= Position.Factory.createInitial(
            false,
            false).move(
            new Position.Move(
                  Square._D2,
                  Square._D3,
                  Position.PromotionPiece.NONE)).move(
            new Position.Move(
                  Square._E2,
                  Square._E3,
                  Position.PromotionPiece.NONE)).move(
            new Position.Move(
                  Square._F2,
                  Square._F4,
                  Position.PromotionPiece.NONE));
      long kingAttacksFromE1= King.attacksFrom(
            Square._E1,
            p);

      Assert.assertEquals(
            ""
                  + //
                  "00000000"
                  + // A8-H8
                  "00000000"
                  + // A7-H7
                  "00000000"
                  + // A6-H6
                  "00000000"
                  + // A5-H5
                  "00000000"
                  + // A4-H4
                  "00000000"
                  + // A3-H3
                  "00011100"
                  + // A2-H2
                  "00000000", // A1-H1
            (this.zeroPrefix[Long.toBinaryString(
                  kingAttacksFromE1).length() - 1] + Long
                  .toBinaryString(kingAttacksFromE1)));

   }

   public void testBlackKingInitial() {
      Position initialPosition= Position.Factory.createInitial(
            false,
            false);
      long kingAttacksFromE8= King.attacksFrom(
            Square._E8,
            initialPosition);

      Assert.assertEquals(
            ""
                  + //
                  "00000000"
                  + // A8-H8
                  "00000000"
                  + // A7-H7
                  "00000000"
                  + // A6-H6
                  "00000000"
                  + // A5-H5
                  "00000000"
                  + // A4-H4
                  "00000000"
                  + // A3-H3
                  "00000000"
                  + // A2-H2
                  "00000000", // A1-H1
            (this.zeroPrefix[Long.toBinaryString(
                  kingAttacksFromE8).length() - 1] + Long
                  .toBinaryString(kingAttacksFromE8)));

   }

   public void testBlackKingAfterD7D5_F7F5() {
      Position p= Position.Factory.createInitial(
            false,
            false).move(
            new Position.Move(
                  Square._D7,
                  Square._D5,
                  Position.PromotionPiece.NONE)).move(
            new Position.Move(
                  Square._F7,
                  Square._F5,
                  Position.PromotionPiece.NONE));
      long kingAttacksFromE8= King.attacksFrom(
            Square._E8,
            p);

      Assert.assertEquals(
            ""
                  + //
                  "00000000"
                  + // A8-H8
                  "00010100"
                  + // A7-H7
                  "00000000"
                  + // A6-H6
                  "00000000"
                  + // A5-H5
                  "00000000"
                  + // A4-H4
                  "00000000"
                  + // A3-H3
                  "00000000"
                  + // A2-H2
                  "00000000", // A1-H1
            (this.zeroPrefix[Long.toBinaryString(
                  kingAttacksFromE8).length() - 1] + Long
                  .toBinaryString(kingAttacksFromE8)));

   }

   public void testBlackKingAfterD7D5() {
      Position p= Position.Factory.createInitial(
            false,
            false).move( new Position.Move(
            Square._D7,
            Square._D5,
            Position.PromotionPiece.NONE));
      long kingAttacksFromE8= King.attacksFrom(
            Square._E8,
            p);

      Assert.assertEquals(
            ""
                  + //
                  "00000000"
                  + // A8-H8
                  "00010000"
                  + // A7-H7
                  "00000000"
                  + // A6-H6
                  "00000000"
                  + // A5-H5
                  "00000000"
                  + // A4-H4
                  "00000000"
                  + // A3-H3
                  "00000000"
                  + // A2-H2
                  "00000000", // A1-H1
            (this.zeroPrefix[Long.toBinaryString(
                  kingAttacksFromE8).length() - 1] + Long
                  .toBinaryString(kingAttacksFromE8)));

   }

   public void testBlackKingAfterE7E5() {
      Position p= Position.Factory.createInitial(
            false,
            false).move( new Position.Move(
            Square._E7,
            Square._E5,
            Position.PromotionPiece.NONE));
      long kingAttacksFromE8= King.attacksFrom(
            Square._E8,
            p);

      Assert.assertEquals(
            ""
                  + //
                  "00000000"
                  + // A8-H8
                  "00001000"
                  + // A7-H7
                  "00000000"
                  + // A6-H6
                  "00000000"
                  + // A5-H5
                  "00000000"
                  + // A4-H4
                  "00000000"
                  + // A3-H3
                  "00000000"
                  + // A2-H2
                  "00000000", // A1-H1
            (this.zeroPrefix[Long.toBinaryString(
                  kingAttacksFromE8).length() - 1] + Long
                  .toBinaryString(kingAttacksFromE8)));

   }

}