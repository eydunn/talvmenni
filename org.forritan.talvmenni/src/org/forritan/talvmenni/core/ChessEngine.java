package org.forritan.talvmenni.core;

import org.forritan.talvmenni.ui.ConsoleProtocol;
import org.forritan.talvmenni.ui.DebugWindow;
import org.forritan.talvmenni.ui.UciProtocol;
import org.forritan.talvmenni.ui.XboardProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

public class ChessEngine implements Runnable {

	private boolean running;

	private Protocol protocol;

	private ThreadFactory threadFactory;

	private LinkedBlockingQueue inMessages;

	private LinkedBlockingQueue outMessages;

	public static ChessEngine create() {
		if (TalvMenni.DEBUG_WINDOW) {
			new DebugWindow();
		}

		return new ChessEngine();
	}

	private ChessEngine() {

		if (TalvMenni.CROUCHING_TIGER_HIDDEN_DEBUG) {
			System.err.println("DEBUG: ChessEngine()");
		}

		this.running = false;
		this.protocol = new Protocol();
		this.threadFactory = Executors.defaultThreadFactory();
		this.inMessages = new LinkedBlockingQueue();
		this.outMessages = new LinkedBlockingQueue();
	}

	public boolean isRunning() {
		return this.running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public void run() {
		if (TalvMenni.CROUCHING_TIGER_HIDDEN_DEBUG) {
			System.err.println("DEBUG: ChessEngine.run()");
		}

		this.running = true;
		this.threadFactory.newThread(new ProtocolHandler()).start();
		this.threadFactory.newThread(new InStreamHandler()).start();
		this.threadFactory.newThread(new OutStreamHandler()).start();
	}

	private class Protocol {
		private XboardProtocol XboardObject;
		private UciProtocol UciObject;
		private ConsoleProtocol ConsoleObject;
		private int ActiveProtocol = 0;

		public Protocol() {
			this.XboardObject = new XboardProtocol();
			this.UciObject = new UciProtocol();
			this.ConsoleObject = new ConsoleProtocol();
		}

		public String processInput(String theInput) {

			if (TalvMenni.CROUCHING_TIGER_HIDDEN_DEBUG) {
				System.err
						.println("DEBUG: ChessEngine.Protocol.processInput( = "
								+ theInput + " )");
			}

			String theOutput = null;

			if (theInput.equalsIgnoreCase("xboard")) {
				ActiveProtocol = 1; //Change protocol to XboardProtocol
			} else if (theInput.equals("uci")) {
				ActiveProtocol = 2; //Change protocol to UCIProtocol
			} else if (theInput.equals("cmd")) {
				ChessEngine.this.setRunning(false);
				ActiveProtocol = 3; //Change protocol to ConsoleProtocol	
			}
			switch (ActiveProtocol) {
			case 1:
				theOutput = XboardObject.processInput(theInput);	
				break;
			case 2:
				theOutput = UciObject.processInput(theInput);
				break;
			case 3:
				theOutput = ConsoleObject.processInput(theInput);
				break;
			}

			return theOutput;
		}

	}

	private class ProtocolHandler implements Runnable {

		public void run() {

			if (TalvMenni.CROUCHING_TIGER_HIDDEN_DEBUG) {
				System.err.println("DEBUG: ChessEngine.ProtocolHandler.run()");
			}

			while (ChessEngine.this.isRunning()) {

				if (TalvMenni.CROUCHING_TIGER_HIDDEN_DEBUG) {
					System.err
							.println("DEBUG: ChessEngine.ProtocolHandler.run() - start loop");
				}

				try {
					String reply = ChessEngine.this.protocol
							.processInput((String) ChessEngine.this.inMessages
									.take());

					if (reply != null) {
						if (TalvMenni.DEBUG_WINDOW) {
							DebugWindow.updateTekst("ToUI: " + reply);
						}

						ChessEngine.this.outMessages.add(reply);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (TalvMenni.CROUCHING_TIGER_HIDDEN_DEBUG) {
					System.err
							.println("DEBUG: ChessEngine.ProtocolHandler.run() - end loop");
				}

			}

			if (TalvMenni.CROUCHING_TIGER_HIDDEN_DEBUG) {
				System.err
						.println("DEBUG: ChessEngine.ProtocolHandler.run() - thread terminates");
			}

		}

	}

	private class InStreamHandler implements Runnable {

		public void run() {

			if (TalvMenni.CROUCHING_TIGER_HIDDEN_DEBUG) {
				System.err.println("DEBUG: ChessEngine.InStreamHandler.run()");
			}

			BufferedReader inReader = new BufferedReader(new InputStreamReader(
					System.in));

			String inputMessage = "";

			while (ChessEngine.this.isRunning()) {

				if (TalvMenni.CROUCHING_TIGER_HIDDEN_DEBUG) {
					System.err
							.println("DEBUG: ChessEngine.InStreamHandler.run() - start loop");
				}

				try {
					inputMessage = inReader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (inputMessage != null) {
					ChessEngine.this.inMessages.add(inputMessage);
					if (TalvMenni.DEBUG_WINDOW) {
						DebugWindow.updateTekst("FromUI: " + inputMessage);
					}

				}
				inputMessage = null;

				if (TalvMenni.CROUCHING_TIGER_HIDDEN_DEBUG) {
					System.err
							.println("DEBUG: ChessEngine.InStreamHandler.run() - end loop");
				}

			}

			if (TalvMenni.CROUCHING_TIGER_HIDDEN_DEBUG) {
				System.err
						.println("DEBUG: ChessEngine.InStreamHandler.run() - thread terminates");
			}

		}
	}

	private class OutStreamHandler implements Runnable {

		public void run() {

			if (TalvMenni.CROUCHING_TIGER_HIDDEN_DEBUG) {
				System.err.println("DEBUG: ChessEngine.OutStreamHandler.run()");
			}

			while (ChessEngine.this.isRunning()) {

				if (TalvMenni.CROUCHING_TIGER_HIDDEN_DEBUG) {
					System.err
							.println("DEBUG: ChessEngine.OutStreamHandler.run() - start loop");
				}

				try {
					System.out.println((String) ChessEngine.this.outMessages
							.take());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (TalvMenni.CROUCHING_TIGER_HIDDEN_DEBUG) {
					System.err
							.println("DEBUG: ChessEngine.OutStreamHandler.run() - end loop");
				}

			}

			if (TalvMenni.CROUCHING_TIGER_HIDDEN_DEBUG) {
				System.err
						.println("DEBUG: ChessEngine.OutStreamHandler.run() - thread terminates");
			}

		}

	}
}