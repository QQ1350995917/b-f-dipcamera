package keturi;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

public class LocalServer {
	private LocalSocket receiver, sender;
	private LocalServerSocket lss;
	public LocalServer(int session_id) {
		try {
			lss = new LocalServerSocket("com.vilkas04.ol-" + session_id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			receiver = new LocalSocket();
			receiver.connect(new LocalSocketAddress("com.vilkas04.ol-" + session_id));
			receiver.setReceiveBufferSize(500000);
			receiver.setSendBufferSize(500000);
			sender = lss.accept();
			sender.setReceiveBufferSize(500000);
			sender.setSendBufferSize(500000);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	public FileDescriptor getSenderFileDescriptor() {
		return sender.getFileDescriptor();
	}
	public InputStream getInputStream() throws IOException {
		return receiver.getInputStream();
	}

}
