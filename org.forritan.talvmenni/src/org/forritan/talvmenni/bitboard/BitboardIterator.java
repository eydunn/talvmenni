/**
 * talvmenni - A distributed chess-engine implemented in Java(TM)
 * and against Sun Microsystems Jini/JavaSpaces(TM).
 *  
 * Copyright (C) 2004-2006 Ey�un Lamhauge and Ey�un Nielsen
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. 
 */

package org.forritan.talvmenni.bitboard;

import java.util.Iterator;


public class BitboardIterator implements Iterator {

   private long bitboard;

   public BitboardIterator(
         long bitboard) {
      this.bitboard= bitboard;
   }

   public boolean hasNext() {
      return this.bitboard != 0L;
   }
   
   /**
    * @deprecated Use nextBitboard() instead...
    */
   public Object next() {
      return new Long(this.nextBitboard());
   }

   public long nextBitboard() {
      long result= this.bitboard & -this.bitboard;
      this.bitboard= this.bitboard
            ^ result;
      return result;
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }

}