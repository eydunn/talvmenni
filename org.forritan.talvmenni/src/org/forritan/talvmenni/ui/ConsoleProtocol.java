package org.forritan.talvmenni.ui;

import org.forritan.talvmenni.bitboard.Square;
import org.forritan.talvmenni.bitboard.Squares;
import org.forritan.talvmenni.core.ChessEngine.Protocol;


public class ConsoleProtocol extends UiProtocolBase {

   private static ConsoleProtocol instance;

   private ConsoleProtocol(Protocol protocol) {
      super(protocol);
   }

   public static ConsoleProtocol create(Protocol protocol) {
      if (ConsoleProtocol.instance == null) {
         ConsoleProtocol.instance= new ConsoleProtocol(protocol);
      }
      return ConsoleProtocol.instance;
   }

   public String processInput(String theInput) {
      String theOutput= null;

      if ("cmd".equalsIgnoreCase(theInput)) {
         theOutput= welcomeMessage();
      }

      if ("help".equalsIgnoreCase(theInput)) {
         theOutput= helpMessage();
      }

      if ("new".equalsIgnoreCase(theInput)) {
         theOutput= "Setting up a new game";
         this.protocol.newGame();
      }

      if (theInput.startsWith("move")) {
         String move= theInput.substring(
               4).trim();

         theOutput=move;
         //this.protocol.makeMove(move);
      }

      if ("position".equalsIgnoreCase(theInput)) {
         theOutput= boardPosition();
      }
      if ("white".equalsIgnoreCase(theInput)) {
         if (this.protocol.isWhiteToMove())
            theOutput= "White is already to move";
         else
            theOutput= "White to Move";
         this.protocol.whiteToMove();
      }
      if ("black".equalsIgnoreCase(theInput)) {
         if (this.protocol.isWhiteToMove())
            theOutput= "Black to Move";
         else
            theOutput= "Black is already to move";
         this.protocol.blackToMove();
      }

      if ("quit".equalsIgnoreCase(theInput)) // quit and exit
      {
         theOutput= "bye";
         this.protocol.stop();
      }

      if (theOutput == null) {
         theOutput= "unknown command: "
               + theInput;
      }

      return theOutput;
   }

   private String boardPosition() {
      String positionString= "    ---------------\n";
      Square square= Squares.create();
      long sq;

      for (int x= 8; x > 0; x--) {
         positionString= positionString
               + x
               + " | ";
         for (int y= 0; y < 8; y++) {
            sq= square.getSquare(64
                  - (x * 8)
                  + y);
            positionString= positionString
                  + this.protocol.getStringPiece(sq)
                  + " ";
         }
         positionString= positionString
               + "|\n";
      }
      positionString= positionString
            + "    ---------------\n";
      positionString= positionString
            + "    a b c d e f g h\n";
      positionString= positionString
            + "    ["
            + whoisToMove()
            + " to move]";

      return positionString;

   }

   private String whoisToMove() {
      if (this.protocol.isWhiteToMove())
         return "White";
      else
         return "Black";
   }

   private String helpMessage() {

      String message;

      message= "--------------------------------------------------------\n";
      message= message
            + "---                 List of commands                 ---\n";
      message= message
            + "--------------------------------------------------------\n";
      message= message
            + "- \n";
      message= message
            + "- HELP: This help screen \n";
      //      message= message
      //            + "- \n";
      message= message
            + "- MOVELIST: Show the move-history \n";
      //      message= message
      //            + "- \n";
      message= message
            + "- POSITION: Display current position on prompt \n";
      //      message= message
      //            + "- \n";
      message= message
            + "- NEW: Start a new game \n";
      message= message
      + "- MOVE: Make move. Format: 'MOVE fftt' \n";
      message= message
            + "- WHITE: Give white the move \n";
      message= message
            + "- BLACK: Give black the move \n";
      message= message
            + "- QUIT: Quit and leave the program \n";
      message= message
            + "--------------------------------------------------------\n";

      return message;

   }

   private String welcomeMessage() {

      String message;

      message= "--------------------------------------------------------\n";
      message= message
            + "- Welcome to TalvMenni console -\n";
      message= message
            + "- \n";
      message= message
            + "- For help on usage, type in HELP and press enter. -\n";
      message= message
            + "- \n";
      message= message
            + "- (Type in quit and press enter to quit -\n";
      message= message
            + "--------------------------------------------------------\n";

      return message;
   }
}