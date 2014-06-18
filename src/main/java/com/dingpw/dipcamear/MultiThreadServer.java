package com.dingpw.dipcamear;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadServer
{
	private int				port		= 1234;
	private ServerSocket	serverSocket;
	private ExecutorService	executorService;	// �̳߳�
	private final int		POOL_SIZE	= 10;	// ����CPU�̳߳ش�С

	public MultiThreadServer() throws IOException
	{
		serverSocket = new ServerSocket(port);
		// Runtime��availableProcessor()�������ص�ǰϵͳ��CPU��Ŀ.
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * POOL_SIZE);
		
		System.out.println("����������");
	}

	public void service()
	{
		while (true)
		{
			Socket socket = null;
			try
			{
				// ���տͻ�����,ֻҪ�ͻ�����������,�ͻᴥ��accept();�Ӷ�������
				socket = serverSocket.accept();
				//executorService.execute(new Handler(socket));
				new Thread(new Handler(socket)).start();

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException
	{
		new MultiThreadServer().service();
	}

}
