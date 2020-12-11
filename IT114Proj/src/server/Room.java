package server;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

//import client.Chair;
import client.Player;
import client.Ticket;
import core.BaseGamePanel;
import core.Helpers;

public class Room extends BaseGamePanel implements AutoCloseable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2396927244891036163L;
	private static SocketServer server;// used to refer to accessible server functions
	private String name;
	private final static Logger log = Logger.getLogger(Room.class.getName());
	Random rand = new Random();

	// Commands
	private final static String COMMAND_TRIGGER = "/";
	private final static String CREATE_ROOM = "createroom";
	private final static String JOIN_ROOM = "joinroom";
	// private final static String READY = "ready";
	private List<ClientPlayer> clients = new ArrayList<ClientPlayer>();
	static Dimension gameAreaSize = new Dimension(800, 800);
	// private List<Chair> chairs = new ArrayList<Chair>();
	private List<Ticket> tickets = new ArrayList<Ticket>();

	public Room(String name, boolean delayStart) {
		super(delayStart);
		this.name = name;
		isServer = true;
	}

	public Room(String name) {
		this.name = name;
		// set this for BaseGamePanel to NOT draw since it's server-side
		isServer = true;
	}

	public static void setServer(SocketServer server) {
		Room.server = server;
	}

	public String getName() {
		return name;
	}

	private static Point getRandomStartPosition() {
		Point startPos = new Point();
		startPos.x = (int) (Math.random() * gameAreaSize.width);
		startPos.y = (int) (Math.random() * gameAreaSize.height);
		return startPos;
	}

	/*
	 * private void generateSeats() { int players = clients.size(); final int chairs
	 * = Helpers.getNumberBetween(players, (int) (players * 1.5)); final Dimension
	 * chairSize = new Dimension(25, 25); final float paddingLeft = .1f; final float
	 * paddingRight = .9f; final float paddingTop = .1f; final float chairSpacing =
	 * chairSize.height * 1.75f; final int chairHalfWidth = (int) (chairSize.width *
	 * .5); final int screenWidth = gameAreaSize.width; final int screenHeight =
	 * gameAreaSize.height; for (int i = 0; i < chairs; i++) { Chair chair = new
	 * Chair("Chair " + (i + 1)); Point chairPosition = new Point(); if (i % 2 == 0)
	 * { chairPosition.x = (int) ((screenWidth * paddingRight) - chairHalfWidth); }
	 * else { chairPosition.x = (int) (screenWidth * paddingLeft); } chairPosition.y
	 * = (int) ((screenHeight * paddingTop) + (chairSpacing * (i / 2)));
	 * chair.setPosition(chairPosition); chair.setSize(chairSize.width,
	 * chairSize.height); chair.setPlayer(null); this.chairs.add(chair); }
	 * 
	 * }
	 * 
	 * private void syncChairs() { // fairest way seems to be syncing 1 chair at a
	 * time across all players Iterator<Chair> chairIter = chairs.iterator(); while
	 * (chairIter.hasNext()) { Chair chair = chairIter.next(); if (chair != null) {
	 * syncChair(chair); } } }
	 * 
	 * private void resetChairs() { Iterator<ClientPlayer> iter =
	 * clients.iterator(); while (iter.hasNext()) { ClientPlayer cp = iter.next();
	 * if (cp != null) { if (cp.player.isSitting()) { cp.player.unsit(); }
	 * cp.client.sendChair(null, null, null, null); } } Iterator<Chair> citer =
	 * chairs.iterator(); while (citer.hasNext()) { citer.next(); citer.remove(); }
	 * }
	 */

	private void generateTickets() {
		int players = clients.size() + 1;
		final int tickets = Helpers.getNumberBetween(players, (int) (players * 1.5));
		final int screenWidth = gameAreaSize.width;
		final int screenHeight = gameAreaSize.height;
		final float paddingLeft = .3f;
		final float paddingRight = .7f;
		final float paddingTop = .3f;
		final float paddingBottom = .7f;
		Dimension ticketSize = new Dimension(30, 20);
		System.out.println("Tickets to be made: " + tickets);
		for (int i = 0; i < tickets; i++) {
			// TODO: Note to self - keep # in the name, will use it to split to get ticket
			// value for now
			Ticket ticket = new Ticket("#" + (i + 1) + "-" + Helpers.getNumberBetween(1, 10));
			Point ticketPosition = new Point();
			ticket.setPlayer(null);
			ticketPosition.x = Helpers.getNumberBetween((int) (screenWidth * paddingLeft),
					(int) (screenWidth * paddingRight));
			ticketPosition.y = Helpers.getNumberBetween((int) (screenHeight * paddingTop),
					(int) (screenHeight * paddingBottom));
			ticket.setPosition(ticketPosition);
			ticket.setSize(ticketSize.width, ticketSize.height);
			this.tickets.add(ticket);
		}
		System.out.println("Tickets made: " + this.tickets.size());
	}

	private void syncTickets() {
		// fairest way seems to be syncing 1 ticket at a time across all players
		Iterator<Ticket> ticketIter = tickets.iterator();
		while (ticketIter.hasNext()) {
			Ticket ticket = ticketIter.next();
			if (ticket != null) {
				syncTicket(ticket);
			}
		}
	}

	private void resetTickets() {
		Iterator<ClientPlayer> iter = clients.iterator();
		while (iter.hasNext()) {
			ClientPlayer cp = iter.next();
			if (cp != null) {
				if (cp.player.hasTicket()) {
					cp.player.takeTicket();
				}
				cp.client.sendTicket(null, null, null, null);
			}
		}
		Iterator<Ticket> ticketIterator = tickets.iterator();
		while (ticketIterator.hasNext()) {
			ticketIterator.next();
			ticketIterator.remove();
		}
	}

	private void syncTicket(Ticket ticket) {
		if (ticket != null) {
			Iterator<ClientPlayer> iter = clients.iterator();
			while (iter.hasNext()) {
				ClientPlayer cp = iter.next();
				if (cp != null) {
					// changed to pass holder name
					cp.client.sendTicket(ticket.getName(), ticket.getPosition(), ticket.getSize(),
							ticket.getHolderName());
				}
			}
		}
	}

	/*
	 * private void syncChair(Chair chair) { if (chair != null) {
	 * Iterator<ClientPlayer> iter = clients.iterator(); while (iter.hasNext()) {
	 * ClientPlayer cp = iter.next(); if (cp != null) { // changed to pass holder
	 * name cp.client.sendChair(chair.getName(), chair.getPosition(),
	 * chair.getSize(), chair.getSitterName()); } } } }
	 */

	private void syncGameSize() {
		Iterator<ClientPlayer> iter = clients.iterator();
		while (iter.hasNext()) {
			ClientPlayer cp = iter.next();
			if (cp != null) {
				cp.client.sendGameAreaSize(gameAreaSize);
			}
		}
	}

	protected synchronized void addClient(ServerThread client) {
		client.setCurrentRoom(this);
		boolean exists = false;
		// since we updated to a different List type, we'll need to loop through to find
		// the client to check against
		Iterator<ClientPlayer> iter = clients.iterator();
		while (iter.hasNext()) {
			ClientPlayer c = iter.next();
			if (c.client == client) {
				exists = true;
				if (c.player == null) {
					log.log(Level.WARNING, "Client " + client.getClientName() + " player was null, creating");
					Player p = new Player();
					p.setName(client.getClientName());
					c.player = p;
					syncClient(c);
				}
				break;
			}
		}

		if (exists) {
			log.log(Level.INFO, "Attempting to add a client that already exists");
		} else {
			// create a player reference for this client
			// so server can determine position
			Player p = new Player();
			p.setName(client.getClientName());
			// add Player and Client reference to ClientPlayer object reference
			ClientPlayer cp = new ClientPlayer(client, p);
			clients.add(cp);// this is a "merged" list of Clients (ServerThread) and Players (Player)
			// objects
			// that's so we don't have to keep track of the same client in two different
			// list locations
			syncClient(cp);

		}
	}

	private void syncClient(ClientPlayer cp) {
		if (cp.client.getClientName() != null) {
			cp.client.sendClearList();
			sendConnectionStatus(cp.client, true, "joined the room " + getName());
			// calculate random start position
			Point startPos = Room.getRandomStartPosition();
			cp.player.setPosition(startPos);
			cp.client.sendGameAreaSize(gameAreaSize);
			// tell our client of our server determined position
			cp.client.sendPosition(cp.client.getClientName(), startPos);
			// tell everyone else about our server determiend position
			sendPositionSync(cp.client, startPos);
			// get the list of connected clients (for ui panel)
			updateClientList(cp.client);
			// get dir/pos of existing players
			updatePlayers(cp.client);

		}
	}

	/***
	 * Syncs the existing players in the room with our newly connected player
	 * 
	 * @param client
	 */
	private synchronized void updatePlayers(ServerThread client) {
		// when we connect, send all existing clients current position and direction so
		// we can locally show this on our client
		Iterator<ClientPlayer> iter = clients.iterator();
		while (iter.hasNext()) {
			ClientPlayer c = iter.next();
			if (c.client != client) {
				boolean messageSent = client.sendDirection(c.client.getClientName(), c.player.getDirection());
				if (messageSent) {
					messageSent = client.sendPosition(c.client.getClientName(), c.player.getPosition());
				}
			}
		}
	}

	/**
	 * Syncs the existing clients in the room with our newly connected client
	 * 
	 * @param client
	 */
	private synchronized void updateClientList(ServerThread client) {
		Iterator<ClientPlayer> iter = clients.iterator();
		while (iter.hasNext()) {
			ClientPlayer c = iter.next();
			if (c.client != client) {
				client.sendConnectionStatus(c.client.getClientName(), true, null);
			}
		}
	}

	protected synchronized void removeClient(ServerThread client) {
		Iterator<ClientPlayer> iter = clients.iterator();
		while (iter.hasNext()) {
			ClientPlayer c = iter.next();
			if (c.client == client) {
				iter.remove();
				log.log(Level.INFO, "Removed client " + c.client.getClientName() + " from " + getName());
			}
		}
		if (clients.size() > 0) {
			sendConnectionStatus(client, false, "left the room " + getName());
		} else {
			cleanupEmptyRoom();
		}
	}

	private void cleanupEmptyRoom() {
		// If name is null it's already been closed. And don't close the Lobby
		if (name == null || name.equalsIgnoreCase(SocketServer.LOBBY)) {
			return;
		}
		try {
			log.log(Level.INFO, "Closing empty room: " + name);
			close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void joinRoom(String room, ServerThread client) {
		server.joinRoom(room, client);
	}

	protected void joinLobby(ServerThread client) {
		server.joinLobby(client);
	}

	protected void createRoom(String room, ServerThread client) {
		if (server.createNewRoom(room)) {
			sendMessage(client, "Created a new room");
			joinRoom(room, client);
		}
	}

	/*
	 * private boolean takeASeat(ClientPlayer cp) { if (cp.player.hasTicket()) { //
	 * check seats since we have a ticket in hand Iterator<Chair> iter =
	 * chairs.iterator(); while (iter.hasNext()) { Chair c = iter.next(); if (c !=
	 * null && c.isAvailable()) { int dist = (int) ((c.getSize().width * .5) +
	 * (cp.player.getSize().width * .5)); Point p = cp.player.getCenter(); Point
	 * chairp = c.getCenter();
	 * 
	 * if (chairp.distanceSq(p) <= (dist * dist)) { // chair is within range, do the
	 * sit :) c.setPlayer(cp.player); cp.player.setChair(c); syncChair(c); return
	 * true; }
	 * 
	 * } } } return false; }
	 */

	protected synchronized void doPickup(ServerThread client) {
		ClientPlayer cp = getCP(client);
		if (cp != null) {
			// TODO reject too frequent request (keep this value in sync with client)
			// ignore requests quicker than 500ms
			long currentMs = System.currentTimeMillis();
			if (cp.player.getLastAction() > 0L && (currentMs - cp.player.getLastAction()) < 500) {
				return;
			}
			cp.player.setLastAction(currentMs);
			// if (takeASeat(cp)) {
			// we sat or are sitting, no need to do anything else
			// return;
			// }
			Iterator<Ticket> iter = tickets.iterator();
			Ticket currentlyHeld = cp.player.takeTicket();
			String chName = null;
			// drop current ticket
			if (currentlyHeld != null) {
				chName = currentlyHeld.getName();
				currentlyHeld.setPlayer(null);
				currentlyHeld.setPosition(cp.player.getPosition());
				syncTicket(currentlyHeld);
			}
			while (iter.hasNext()) {
				Ticket t = iter.next();
				try {
					if (t != null && t.isAvailable() && !t.getName().equalsIgnoreCase(chName)) {

						Point p = cp.player.getCenter();
						Point tp = t.getCenter();
						System.out.println(getName());
						System.out.println("P: " + p);
						System.out.println("T: " + tp);
						System.out.println("Dist: " + tp.distance(p));
						// add the two halfwidths together to get the max distance between the two until
						// a collision occurs
						int dist = (int) ((t.getSize().height * .5) + (cp.player.getSize().width * .5));

						// NOTE:
						// same as below IF: if(tp.distance(p) <= dist)
						// (more expensive - calcs square root)
						if (tp.distanceSq(p) <= (dist * dist)) { // (cheaper - doesn't need to calc square root)
							// ticket is within range, do the exchange/pickup
							t.setPlayer(cp.player);
							cp.player.setTicket(t);
							syncTicket(t);
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private ClientPlayer getCP(ServerThread client) {
		Iterator<ClientPlayer> iter = clients.iterator();
		while (iter.hasNext()) {
			ClientPlayer cp = iter.next();
			if (cp.client == client) {
				return cp;
			}
		}
		return null;
	}

	/***
	 * Helper function to process messages to trigger different functionality.
	 * 
	 * @param message The original message being sent
	 * @param client  The sender of the message (since they'll be the ones
	 *                triggering the actions)
	 */
	private String processCommands(String message, ServerThread client) {
		String response = null;
		try {
			if (message.indexOf(COMMAND_TRIGGER) > -1) {
				String[] comm = message.split(COMMAND_TRIGGER);
				log.log(Level.INFO, message);
				String part1 = comm[1];
				String[] comm2 = part1.split(" ");
				String command = comm2[0];
				if (command != null) {
					command = command.toLowerCase();
				}
				String roomName;
				ClientPlayer cp = null;
				switch (command) {
				case CREATE_ROOM:
					roomName = comm2[1];
					createRoom(roomName, client);
					break;
				case JOIN_ROOM:
					roomName = comm2[1];
					joinRoom(roomName, client);
					break;
				case "flip":
					int toss = rand.nextInt(2);
					if (toss == 1) {
						// sendMessage(client, "Flip: Heads");
						response = "<b color=green>Flip: Heads</b>";
					} else {
						// sendMessage(client, "Flip: Tails");
						response = "<b color=green>Flip: Tails</b>";
					}
					break;
				case "roll":
					int face = rand.nextInt(6) + 1;
					// sendMessage(client, "Roll : " + face);
					response = "<b color=orange>Roll: " + face + "</b>";
					break;
				case "mute":
					String muteClientName = client.getClientName().toLowerCase();
					muteClientName = comm2[1];
					client.addMute(muteClientName);
					break;
				case "unmute":
					String unmuteClientName = client.getClientName().toLowerCase();
					unmuteClientName = comm2[1];
					client.removeMute(unmuteClientName);
					break;
				case "pm":
					String preceiver = client.getClientName().toLowerCase();
					preceiver = comm2[1];
					List<String> pmlist = new ArrayList<String>();
					pmlist.add(preceiver);
					sendPrivateMessage(client, message, pmlist);
					response = null;
					break;
				default:
					// not a command, let's fix this function from eating messages
					response = message;
					break;
				}
			} else {
				// not a command, let's fix this function from eating messages
				response = message;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (message.indexOf("@@") > -1) {
			String[] sentence = message.split("@@");
			String bold = "";
			bold += sentence[0];
			for (int i = 1; i < sentence.length; i++) {
				if (i % 2 == 0) {
					bold += sentence[i];
				} else {
					bold += "<b>" + sentence[i] + "</b>";
				}
			}
			response = bold;
			// sendMessage(client, bold);
		}
		if (message.indexOf("%%") > -1) {
			String[] sentence = message.split("%%");
			String italics = "";
			italics += sentence[0];
			for (int i = 1; i < sentence.length; i++) {
				if (i % 2 == 0) {
					italics += sentence[i];
				} else {
					italics += "<i>" + sentence[i] + "</i>";
				}
			}
			response = italics;
			// sendMessage(client, italics);
		}
		if (message.indexOf("__") > -1) {
			String[] sentence = message.split("__");
			String underline = "";
			underline += sentence[0];
			for (int i = 1; i < sentence.length; i++) {
				if (i % 2 == 0) {
					underline += sentence[i];
				} else {
					underline += "<u>" + sentence[i] + "</u>";
				}
			}
			response = underline;
			// sendMessage(client, underline);
		}
		if (message.indexOf("--") > -1) {
			String[] sentence = message.split("--");
			String color = "";
			color += sentence[0];
			for (int i = 1; i < sentence.length; i++) {
				if (i % 2 == 0) {
					color += sentence[i];
				} else {
					color += "<font size=4 color=red>" + sentence[i] + "</font>";
				}
			}
			response = color;
			// sendMessage(client, color);
		}
		if (message.indexOf("@") > -1) {
			String[] sentence = message.split("@");
			String tag = "";
			String foreign = "";
			List<String> pmessage = new ArrayList<String>();
			for (int i = 0; i < sentence.length; i++) {
				if (sentence[i] != " ") {
					tag += sentence[i];
					foreign += sentence[i];
					break;
				} else {
					tag += sentence[i];
				}
			}
			pmessage.add(foreign);
			sendPrivateMessage(client, message, pmessage);
		}
		return response;
	}

	private void readyCheck() {
		Iterator<ClientPlayer> iter = clients.iterator();
		int total = clients.size();
		int ready = 0;
		while (iter.hasNext()) {
			ClientPlayer cp = iter.next();
			if (cp != null && cp.player.isReady()) {
				ready++;
			}
		}
		if (ready >= total) {
			// start
			System.out.println("Everyone's ready, let's do this!");
			resetTickets();
			generateTickets();
			syncTickets();
			sendSystemMessage("Let the games begin!");
		}
	}

	protected void sendConnectionStatus(ServerThread client, boolean isConnect, String message) {
		Iterator<ClientPlayer> iter = clients.iterator();
		while (iter.hasNext()) {
			ClientPlayer c = iter.next();
			boolean messageSent = c.client.sendConnectionStatus(client.getClientName(), isConnect, message);
			if (!messageSent) {
				iter.remove();
				log.log(Level.INFO, "Removed client " + c.client.getId());
			}
		}
	}

	/***
	 * Takes a sender and a message and broadcasts the message to all clients in
	 * this room. Client is mostly passed for command purposes but we can also use
	 * it to extract other client info.
	 * 
	 * @param sender  The client sending the message
	 * @param message The message to broadcast inside the room
	 */
	protected void sendMessage(ServerThread sender, String message) {
		log.log(Level.INFO, getName() + ": Sending message to " + clients.size() + " clients");
		String resp = processCommands(message, sender);
		if (resp == null) {
			// it was a command, don't broadcast
			return;
		}
		message = resp;
		Iterator<ClientPlayer> iter = clients.iterator();
		while (iter.hasNext()) {
			ClientPlayer client = iter.next();
			if (!client.client.isMuted(sender.getClientName())) {
				boolean messageSent = client.client.send(sender.getClientName(), message);
				if (!messageSent) {
					iter.remove();
				}
			} else {
				boolean messageSent = client.client.send(sender.getClientName(),
						"Muted sender. Message not delivered.");
			}
			log.log(Level.INFO, "Removed client " + client.client.getId());
		}
	}

	protected void sendPrivateMessage(ServerThread sender, String message, List<String> pmlist) {
		Iterator<ClientPlayer> iter = clients.iterator();
		while (iter.hasNext()) {
			ClientPlayer client = iter.next();
			if (pmlist.contains(client.client.getClientName())) {
				if (!client.client.isMuted(sender.getClientName())) {
					boolean messageSent = client.client.send(sender.getClientName(), message);
					if (!messageSent) {
						iter.remove();
					}
					break;
				} else {
					boolean messageSent = client.client.send(sender.getClientName(),
							"Muted by recipient. Message blocked");
					break;
				}
				// log.log(Level.INFO, "Removed client " + client.client.getId());
			}
		}
	}

	// added as sample during Feature-PickupTicket
	protected void sendSystemMessage(String message) {
		Iterator<ClientPlayer> iter = clients.iterator();
		while (iter.hasNext()) {
			ClientPlayer client = iter.next();
			boolean messageSent = client.client.send("[Announcer]", message);
			if (!messageSent) {
				iter.remove();
				log.log(Level.INFO, "Removed client " + client.client.getId());
			}
		}
	}

	/**
	 * Broadcasts this client/player direction to all connected clients/players
	 * 
	 * @param sender
	 * @param dir
	 */
	protected void sendDirectionSync(ServerThread sender, Point dir) {
		boolean changed = false;
		// first we'll find the clientPlayer that sent their direction
		// and update the server-side instance of their direction
		Iterator<ClientPlayer> iter = clients.iterator();
		while (iter.hasNext()) {
			ClientPlayer client = iter.next();
			// update only our server reference for this client
			// if we don't have this "if" it'll update all clients (meaning everyone will
			// move in sync)
			if (client.client == sender) {
				changed = client.player.setDirection(dir.x, dir.y);
				break;
			}
		}
		// if the direction is "changed" (it should be, but check anyway)
		// then we'll broadcast the change in direction to all clients
		// so their local movement reflects correctly
		if (changed) {
			iter = clients.iterator();
			while (iter.hasNext()) {
				ClientPlayer client = iter.next();
				boolean messageSent = client.client.sendDirection(sender.getClientName(), dir);
				if (!messageSent) {
					iter.remove();
					log.log(Level.INFO, "Removed client " + client.client.getId());
				}
			}

		}
	}

	/**
	 * Broadcasts this client/player position to all connected clients/players
	 * 
	 * @param sender
	 * @param pos
	 */
	protected void sendPositionSync(ServerThread sender, Point pos) {
		Iterator<ClientPlayer> iter = clients.iterator();
		while (iter.hasNext()) {
			ClientPlayer client = iter.next();
			boolean messageSent = client.client.sendPosition(sender.getClientName(), pos);
			if (!messageSent) {
				iter.remove();
				log.log(Level.INFO, "Removed client " + client.client.getId());
			}
		}
	}

	public List<String> getRooms(String search) {
		return server.getRooms(search);
	}

	/***
	 * Will attempt to migrate any remaining clients to the Lobby room. Will then
	 * set references to null and should be eligible for garbage collection
	 */
	@Override
	public void close() throws Exception {
		int clientCount = clients.size();
		if (clientCount > 0) {
			log.log(Level.INFO, "Migrating " + clients.size() + " to Lobby");
			Iterator<ClientPlayer> iter = clients.iterator();
			Room lobby = server.getLobby();
			while (iter.hasNext()) {
				ClientPlayer client = iter.next();
				lobby.addClient(client.client);
				iter.remove();
			}
			log.log(Level.INFO, "Done Migrating " + clients.size() + " to Lobby");
		}
		server.cleanupRoom(this);
		name = null;
		isRunning = false;
		// should be eligible for garbage collection now
	}

	@Override
	public void awake() {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		log.log(Level.INFO, getName() + " start called");
	}

	long frame = 0;

	void checkPositionSync(ClientPlayer cp) {
		// determine the maximum syncing needed
		// you do NOT need it every frame, if you do it could cause network congestion
		// and lots of bandwidth that doesn't need to be utilized
		if (frame % 120 == 0) {// sync every 120 frames (i.e., if 60 fps that's every 2 seconds)
			// check if it's worth syncing the position
			// again this is to save unnecessary data transfer
			if (cp.player.changedPosition()) {
				sendPositionSync(cp.client, cp.player.getPosition());
			}
		}

	}

	@Override
	public void update() {
		// We'll make the server authoritative
		// so we'll calc movement/collisions and send the action to the clients so they
		// can visually update. Client's won't be determining this themselves
		Iterator<ClientPlayer> iter = clients.iterator();
		while (iter.hasNext()) {
			ClientPlayer p = iter.next();
			if (p != null) {
				// have the server-side player calc their potential new position
				p.player.move();
				// determine if we should sync this player's position to all other players
				checkPositionSync(p);
			}
		}

	}

	// don't call this more than once per frame
	private void nextFrame() {
		// we'll do basic frame tracking so we can trigger events
		// less frequently than each frame
		// update frame counter and prevent overflow
		if (Long.MAX_VALUE - 5 <= frame) {
			frame = Long.MIN_VALUE;
		}
		frame++;
	}

	@Override
	public void lateUpdate() {
		nextFrame();
	}

	@Override
	public void draw(Graphics g) {
		// this is the server, we won't be using this unless you're adding this view to
		// the Honor's student extra section
	}

	@Override
	public void quit() {
		// don't call close here
		log.log(Level.WARNING, getName() + " quit() ");
	}

	@Override
	public void attachListeners() {
		// no listeners either since server side receives no input
	}

}