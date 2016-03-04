package robotTools;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import core.Plane3D;
import processing.core.PApplet;
import processing.data.XML;
import toxi.geom.Vec3D;

public class RobotClient extends Thread {
	
	DatagramSocket ds;
	int sendPort,receivePort;

	byte[] receiveBuffer = new byte[1024]; 
	byte[] receiveData;
	PApplet parent;
	
	boolean running;
	boolean available;

	//-------------------------------------------------------------------------------------

	//Constructors

	//-------------------------------------------------------------------------------------
	
	public RobotClient(int _sendport, int _receivePort, PApplet _parent) throws UnknownHostException{
		
		parent = _parent;
		sendPort = _sendport;
		receivePort = _receivePort;
		try {
		      ds = new DatagramSocket(receivePort);
		    } catch (SocketException e) {
		      e.printStackTrace();
		    }
		running =false;

	}
	
	//-------------------------------------------------------------------------------------

	//threading

	//-------------------------------------------------------------------------------------
	
	@Override
	public void start(){
		running = true;
		super.start();
		
	}
	
	public void run () { 
		while (running) {
		      checkForMessage();
		      // New data is available!
		      available = true;
		    }
	} 
	
	public void quit() { 
	    System.out.println("Quitting."); 
	    running = false;
	    // In case the thread is waiting. . . 
	    interrupt(); 
	  } 
	
	//-------------------------------------------------------------------------------------

	//send/receive functions

	//-------------------------------------------------------------------------------------
	
	private void checkForMessage() {
	    DatagramPacket p = new DatagramPacket(receiveBuffer, receiveBuffer.length); 
	    try {
	      ds.receive(p);
	    } 
	    catch (IOException e) {
	      e.printStackTrace();
	    } 
	    receiveData = p.getData();
	  }
	
	public String[] getPoseData(String element){
		
		String[] poseString = new String[6];
		//message should be a string that is formatted XML
		
		//perform cast
		String message = new String( receiveData );
		
		int end = message.indexOf("</Robot>");
		int start = message.indexOf("<Robot>");
		//System.out.println(message);
		
		String trimmedMessage = message.substring(start, end+8);

		XML xml = parent.parseXML(trimmedMessage);
		  if (xml == null) {
		    System.out.println("XML could not be parsed.");
		  } else {
		    XML posElement = xml.getChild("Pose");
		    poseString = posElement.getString(element).split(",");
		    
		  }
		available = false; //reset available after data returned
		return poseString;
	}
	
	public void sendVector(Vec3D v, String element, String ip){
		
		String attributes = "X=\""+v.x+"\" Y=\""+v.y+"\" Z=\""+v.z+"\"";
		String data = "<Robot><" + element + " "+attributes + "/></Robot>";
		sendString(data, ip);

	}
	public void sendPlane3D(Plane3D p, String ip){
		String element = "PoseXZ";
		String attributes = "XX=\""+p.xx.x+"\" XY=\""+p.xx.y+"\" XZ=\""+p.xx.z+"\" ZX=\""+p.zz.x+"\" ZY=\""+p.zz.y+"\" ZZ=\""+p.zz.z+"\"";
		String data = "<Robot><" + element + " "+attributes + "/></Robot>";
		sendString(data, ip);
		
	}
	
	public void sendString(String data, String ip){
		byte[] sendBytes = data.getBytes();
		try {
		    ds.send(new DatagramPacket(sendBytes,sendBytes.length, InetAddress.getByName(ip),sendPort));
		  } 
		  catch (Exception e) {
		    e.printStackTrace();
		  }
	}
	public void sendIO(int val,String element, String ip){
		String data = "<Robot><" + element + ">" +val + "</" + element +"></Robot>";
		byte[] sendBytes = data.getBytes();
		try {
		    ds.send(new DatagramPacket(sendBytes,sendBytes.length, InetAddress.getByName(ip),sendPort));
		  } 
		  catch (Exception e) {
		    e.printStackTrace();
		  }
	}
	
	public boolean sendArduino(String data, int arduinoPort, String ip){
		byte[] sendBytes = data.getBytes();
		try {
		    ds.send(new DatagramPacket(sendBytes,sendBytes.length, InetAddress.getByName(ip),arduinoPort));
		  } 
		  catch (Exception e) {
		    e.printStackTrace();
		    return false;
		  }
		return true;
	}
	
	public boolean available(){
		return available;
	}
	
}
