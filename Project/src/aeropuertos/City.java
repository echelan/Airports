/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aeropuertos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author Andres
 */
public class City {
	
	public static int STATE_DIS = 0;
	public static int STATE_CON = 1;
	public static int STATE_AIR = 2;
	
	private final static int DELTA_X = 37;
	private final static int DELTA_Y = 29;
	
	private String name;
	private Object[][] connections;
	private int state;
	private int fontSize;
	private int fontX;
	private int fontY;
	private int airportCost;
	private int x;
	private int y;

	public City(String name, int airportCost, int[] travelCost) {
		this.name = name;
		this.x = -200;
		this.airportCost = airportCost;
		this.y = -200;
		this.state = STATE_DIS;
		this.connections = new Object[travelCost.length][2];
		for (int i = 0; i < travelCost.length; i++) {
			this.connections[i][0] = travelCost[i];
			this.connections[i][1] = false;
		}
		
		fontSize = 40;
		Font font = new Font("Arial",Font.PLAIN,fontSize);
		Graphics g = new BufferedImage(300, 250, BufferedImage.TYPE_INT_ARGB).getGraphics();
		java.awt.FontMetrics metrics = g.getFontMetrics(font);
		while (60 - metrics.stringWidth(this.name) < 0 || 20 - metrics.getHeight() < 0) {
			fontSize--;
			if (fontSize <= 12) {
				fontSize = 40;
				this.name = this.name.substring(0, this.name.length()-1);
			}
			font = new Font("Arial",Font.PLAIN,fontSize);
			metrics = g.getFontMetrics(font);
		}
		fontX = (60 - metrics.stringWidth(this.name)) / 2;
		fontY = ((20 - metrics.getHeight()) / 2) + metrics.getAscent();
		
	}
	
	/**
	 * ciudad esta a 0 o 1 salto de un aeropuerto
	 * @return 
	 */
	public boolean isDone() {
		return isAirport() || isConnected();
	}

	/**
	 * ciudad esta a 0 saltos de un aeropuerto
	 * @return 
	 */
	public boolean isAirport() {
		return this.state == City.STATE_AIR;
	}
	
	/**
	 * ciudad esta a 1 salto de un aeropuerto
	 * @return 
	 */
	public boolean isConnected() {
		boolean connected = false;
		for (int i = 0; i < connections.length; i++) {
			connected = connected || (boolean)connections[i][1];
		}
		return connected && !isAirport();
	}
	
	/**
	 * la imagen representativa de la ciudad
	 * @return 
	 */
	public BufferedImage getImage() {
		BufferedImage image = new BufferedImage(60, 70, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		
		g.setColor(Color.white);
		g.fillOval(5, 0, 50, 50);
		
        if (state == STATE_AIR) {
			g.drawImage(Icons.CITY_AIR, 6, 1, 48, 48, null);
		} else if (state == STATE_CON) {
			g.drawImage(Icons.CITY_CON, 6, 1, 48, 48, null);
		} else if (state == STATE_DIS) {
			g.drawImage(Icons.CITY_DIS, 6, 1, 48, 48, null);
		}
		g.setColor(Color.black);
		g.fillRect(0, 50, 60, 20);
		g.setColor(Color.white);
		g.setFont(new Font("Arial",Font.PLAIN,fontSize));
		g.drawString(this.name, fontX, 50+fontY);
		
		return image;
	}
	
	/**
	 * el valor de la carretera mas barata para construir
	 * @return 
	 */
	public int getCheapestTravelCost() {
		int lowestCost = -1;
		for (int i = 0; i < connections.length; i++) {
			if (isValidConnection(i)) {
				if (getTravelCost(i) < lowestCost || lowestCost == -1) {
					lowestCost = getTravelCost(i);
				}
			}
		}
		return lowestCost;
	}
	
	/**
	 * el numero de carreteras con el costo mas barato
	 * @return 
	 */
	public int getNumCheapestConnections() {
		int lowestCost = getCheapestTravelCost();
		int numCheapest = 0;
		for (int i = 0; i < connections.length; i++) {
			if (getTravelCost(i) == lowestCost) {
				numCheapest++;
			}
		}
		return numCheapest;
	}
	
	/**
	 * todos los indices de carreteras con el costo mas barato
	 * @return 
	 */
	public int[] getCheapestConnections() {
		int[] cheapestIDs = new int[getNumCheapestConnections()];
		int lowestCost = getCheapestTravelCost();
		int added = 0;
		for (int i = 0; i < connections.length; i++) {
			if (getTravelCost(i) == lowestCost) {
				cheapestIDs[added] = i;
				added++;
			}
		}
		return cheapestIDs;
	}
	
	/**
	 * la conexion a el indice dado es valido
	 * @param index
	 * @return 
	 */
	public boolean isValidConnection(int index) {
		return getTravelCost(index) > 0;
	}
	
	/**
	 * la conexion a el indice dado es mas barato que un aeropuerto
	 * @param index
	 * @return 
	 */
	public boolean isCheapConnection(int index) {
		return isValidConnection(index) && getTravelCost(index) <= getAirportCost();
	}
	
	/**
	 * la conexion a el indice dado tiene el menor valor posible
	 * @param index
	 * @return 
	 */
	public boolean isCheapestConnection(int index) {
		return getTravelCost(index) == getCheapestTravelCost();
	}
	
	/**
	 * numero de conexiones validas
	 * @return 
	 */
	public int numValidWays() {
		int vw = 0;
		for (int i = 0; i < connections.length; i++) {
			if (isValidConnection(i)) {
				vw++;
			}
		}
		return vw;
	}
	
	/**
	 * numero de conexiones mas baratas que su aeropuerto
	 * @return 
	 */
	public int numCheaperWays() {
		int vw = 0;
		for (int i = 0; i < connections.length; i++) {
			if (isValidConnection(i) && getTravelCost(i) < getAirportCost()) {
				vw++;
			}
		}
		return vw;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the travelCost
	 * @param index
	 */
	public int getTravelCost(int index) {
		return (int)connections[index][0];
	}

	/**
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the x
	 */
	public int getCenterX() {
		return x+DELTA_X;
	}
	
	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x-DELTA_X;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * @return the y
	 */
	public int getCenterY() {
		return y+DELTA_Y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y-DELTA_Y;
	}

	/**
	 * @return the connections
	 * @param index
	 */
	public boolean getConnection(int index) {
		return (boolean)connections[index][1];
	}

	/**
	 * @param connection the connections to set
	 * @param index
	 */
	public void setConnection(int index, boolean connection) {
		this.connections[index][1] = connection;
	}

	/**
	 * @return the airportCost
	 */
	public int getAirportCost() {
		return airportCost;
	}
	
}
