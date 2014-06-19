package com.dingpw.dipcamear.server.server;

//blog.csdn.net/zgyulongfei
//Email: zgyulongfei@gmail.com

public class AmrServer extends MediaServer {
//	private static final int udpPort = CommonConfig.AUDIO_SERVER_UP_PORT;
//	private static final int tcpPort = CommonConfig.AUDIO_SERVER_DOWN_PORT;
//	private static final int BUFFER_SIZE = 50 * 100;
//
//	private DatagramSocket udpServer;
//	private ServerSocket tcpServer;
//
//	public AmrServer() {
//		super(udpPort, tcpPort, BUFFER_SIZE);
//		udpServer = getUdpServer();
//		tcpServer = getTcpServer();
//	}
//
//	@Override
//	public void start() {
//		new Thread(new UdpThread()).start();
//		new Thread(new TcpThread()).start();
//	}
//
//	private class UdpThread implements Runnable {
//		@Override
//		public void run() {
//			System.out.println("audio udp server start...");
//
//			byte[] data = new byte[1024 * 10];
//			while (isServerRunning()) {
//				try {
//					DatagramPacket pack = new DatagramPacket(data, data.length);
//					udpServer.receive(pack);
//					addPacketToBuffer(new FramePacket(pack.getData(), pack.getLength()));
//				} catch (Exception e) {
//					System.out.println(e.toString());
//				}
//			}
//		}
//	}
//
//	private class TcpThread implements Runnable {
//		public void run() {
//			try {
//				System.out.println("audio tcp server start...");
//				new Thread(new MulticastThread()).start();
//				int clientId = 0;
//				while (isServerRunning()) {
//					Socket clientSocket = tcpServer.accept();
//					addClient(clientSocket, clientId);
//					++clientId;
//				}
//			} catch (Exception e) {
//				System.out.println("audio TcpThread error.....");
//			}
//		}
//	}
}
