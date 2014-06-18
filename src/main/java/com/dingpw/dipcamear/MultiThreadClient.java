package com.dingpw.dipcamear;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class MultiThreadClient
{
	public static void main(String[] args)
	{
		int numTasks = 10;

//		ExecutorService exec = Executors.newCachedThreadPool();
//
//		for (int i = 0; i < numTasks; i++)
//		{
//			exec.execute(createTask(i));
//		}
		new Thread(createTask(22)).start();
	}

	// ����һ���򵥵�����
	private static Runnable createTask(final int taskID)
	{
		return new Runnable()
		{
			private Socket	socket	= null;
			private int		port	= 1234;

			public void run()
			{
				System.out.println("Task " + taskID + ":start");
				try
				{
					socket = new Socket("localhost", port);
					// ���͹ر�����
					OutputStream socketOut = socket.getOutputStream();
					socketOut.write("�� ��ҪҪ����\r\n".getBytes());
					socketOut.write("�� ��ҪҪ����\r\n".getBytes());
					socketOut.write("�� ��ҪҪ����\r\n".getBytes());
					socketOut.write("�� ��ҪҪ����\r\n".getBytes());

					// ���շ������ķ���
//					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//					String msg = null;
//					while ((msg = br.readLine()) != null)
//						System.out.println(msg);
					socketOut.close();
					socket.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

		};
	}

}
