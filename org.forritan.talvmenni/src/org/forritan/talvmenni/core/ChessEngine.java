package org.forritan.talvmenni.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

import org.forritan.talvmenni.game.Position;
import org.forritan.talvmenni.ui.ConsoleProtocol;
import org.forritan.talvmenni.ui.DebugWindow;
import org.forritan.talvmenni.ui.UciProtocol;
import org.forritan.talvmenni.ui.UiProtocol;
import org.forritan.talvmenni.ui.XboardProtocol;


public class ChessEngine implements Runnable {

   private boolean             running;

   private Protocol            protocol;

   private ThreadFactory       threadFactory;

   private LinkedBlockingQueue<String> inMessages;

   private LinkedBlockingQueue<String> outMessages;

   public static ChessEngine create() {
      ChessEngine engine= new ChessEngine();

      if (TalvMenni.DEBUG_WINDOW) {
         //TODO: !!!! Skal flytast / gerast asynkront / �rogvar i/o vi�
         // WinBoard
         long time= -System.currentTimeMillis();
         new DebugWindow();
         time+= System.currentTimeMillis();
         System.err.println("Used "
               + time
               + " millis in main thread to create DebugWindow");
      }

      return engine;
   }

   private ChessEngine() {

      this.running= false;
      this.protocol= new ProtocolImpl();
      this.threadFactory= Executors.defaultThreadFactory();
      this.inMessages= new LinkedBlockingQueue<String>();
      this.outMessages= new LinkedBlockingQueue<String>();
   }

   public boolean isRunning() {
      return this.running;
   }

   public void setRunning(boolean running) {
      this.running= running;
      // FIXME: Shoudn't be necessary to System.exit(0) to quit...
      if(!this.running) {
         System.exit(0);
      }

   }

   public void run() {
      this.running= true;
      this.threadFactory.newThread(new ProtocolHandler()).start();
      this.threadFactory.newThread(new InStreamHandler()).start();
      this.threadFactory.newThread(new OutStreamHandler()).start();
   }

   public interface Protocol {
      public String processInput(String input);

      public void newGame();
      public void stop();
      public void whiteToMove();
      public void blackToMove();
      public boolean isWhiteToMove();
      public String getStringPiece(long sq);
   }

   private class ProtocolImpl implements Protocol {

      private UiProtocol uiProtocol;
      private Position   currentPosition;
      private boolean    WhiteToMove = true;

      public String processInput(String theInput) {

         String theOutput= null;

         if (uiProtocol == null) {
            if (theInput.equalsIgnoreCase("xboard")) {
               uiProtocol= XboardProtocol.create(this); //Change protocol to
               // XboardProtocol
            } else if (theInput.equals("uci")) {
               uiProtocol= UciProtocol.create(this); //Change protocol to
               // UCIProtocol
            } else if (theInput.equals("cmd")) {
               uiProtocol= ConsoleProtocol.create(this); //Change protocol to
               // ConsoleProtocol
            }
         }

         if (uiProtocol != null) {
            theOutput= uiProtocol.processInput(theInput);
         }

         return theOutput;
      }

      public void stop() {
         currentPosition= null;
         ChessEngine.this.setRunning(false);
      }

      public void newGame() {
         currentPosition= Position.createInitial();
         WhiteToMove = true;
      }
      
      
      public void whiteToMove() {
         this.WhiteToMove= true;
      }

      public void blackToMove() {
         this.WhiteToMove= false;
      }

      public String getStringPiece(long square) {
         if (currentPosition != null){
            if (currentPosition.white.isPawn(square)) 
               return "P";
            else
            if (currentPosition.black.isPawn(square)) 
               return "p";
            else
            if (currentPosition.white.isRook(square)) 
               return "R";
            else
            if (currentPosition.black.isRook(square)) 
               return "r";
            else
            if (currentPosition.white.isBishop(square)) 
               return "B";
            else
            if (currentPosition.black.isBishop(square)) 
               return "b";
            else
            if (currentPosition.white.isKnight(square)) 
               return "N";
            else
            if (currentPosition.black.isKnight(square)) 
               return "n";
            else
            if (currentPosition.white.isQueen(square)) 
               return "Q";
            else
            if (currentPosition.black.isQueen(square)) 
               return "q";
            else
            if (currentPosition.white.isKing(square)) 
               return "K";
            else
            if (currentPosition.black.isKing(square)) 
               return "k";
            else
               return ".";                     
      }
         else
            return ".";
         }

      public boolean isWhiteToMove() {
         return this.WhiteToMove;
      }

   }

   private class ProtocolHandler implements Runnable {

      public void run() {

         while (ChessEngine.this.isRunning()) {

            try {
               String reply= ChessEngine.this.protocol
                     .processInput(ChessEngine.this.inMessages.take());

               if (reply != null) {
                  if (TalvMenni.DEBUG_WINDOW) {
                     DebugWindow.updateTekst("From_Talvmenni: "
                           + reply);
                  }
                  ChessEngine.this.outMessages.add(reply);
               }

            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      }
   }

   private class InStreamHandler implements Runnable {

      public void run() {

         BufferedReader inReader= new BufferedReader(new InputStreamReader(
               System.in));

         String inputMessage= "";

         while (ChessEngine.this.isRunning()) {

            try {
               inputMessage= inReader.readLine();
            } catch (IOException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
            if (inputMessage != null) {
               ChessEngine.this.inMessages.add(inputMessage);
               if (TalvMenni.DEBUG_WINDOW) {
                  DebugWindow.updateTekst("FromUI: "
                        + inputMessage);
               }

            }
            inputMessage= null;
         }
      }
   }

   private class OutStreamHandler implements Runnable {

      public void run() {

         while (ChessEngine.this.isRunning()) {

            try {
               System.out.println((String) ChessEngine.this.outMessages.take());
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      }
   }
}