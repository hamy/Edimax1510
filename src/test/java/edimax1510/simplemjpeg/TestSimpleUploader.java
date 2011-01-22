package edimax1510.simplemjpeg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edimax1510.AbstractEdimax1510TestCase;

public class TestSimpleUploader extends AbstractEdimax1510TestCase {

	private static final int PORT = 18090;
	private ServerSocket serverSocket;
	private Acceptor acceptor;
	private List<Handler> handlers;

	@Before
	public void setUp() throws Exception {
		String mn = debugEntering("setUp", "port: ", PORT);
		serverSocket = new ServerSocket(PORT);
		debug(mn, "server socket: ", serverSocket);
		handlers = new ArrayList<Handler>();
		acceptor = new Acceptor();
		debugLeaving(mn);
	}

	@After
	public void tearDown() throws Exception {
		String mn = debugEntering("tearDown");
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (Throwable ignored) {
				debug(mn, "ignored: ", ignored);
			} finally {
				serverSocket = null;
			}
		}
		if (acceptor != null) {
			acceptor.shutdown();
		}
		for (Handler handler : handlers) {
			handler.shutdown();
		}
		debugLeaving(mn);
	}

	private float saturation = 1.0f;
	private float brightness = 1.0f;
	private float hue = 0.0f;
	private float dhue = 0.1f;

	private byte[] createImage() throws Exception {
		BufferedImage image = new BufferedImage(640, 480,
				BufferedImage.TYPE_3BYTE_BGR);
		int irgb = Color.HSBtoRGB(hue, saturation, brightness);
		hue += dhue;
		if (hue >= 1f) {
			hue = 0f;
		}
		Graphics2D g2d = image.createGraphics();
		g2d.setPaint(new Color(irgb));
		g2d.fillRect(0, 0, 641, 481);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "JPEG", baos);
		byte[] result = baos.toByteArray();
		baos.close();
		return result;
	}

	@Test
	public void testUpload() throws Exception {
		String mn = debugEntering("testUpload");
		Thread.sleep(20000);
		debugLeaving(mn);
	}

	private class Acceptor implements Runnable {
		private boolean running = false;
		private Future future;

		private Acceptor() {
			ExecutorService pool = Executors.newSingleThreadExecutor();
			running = true;
			future = pool.submit(this);
		}

		private void shutdown() {
			String mn = debugEntering("shutdown");
			running = false;
			future.cancel(true);
			debugLeaving(mn);
		}

		public void run() {
			String mn = debugEntering("run");
			while (running) {
				try {
					Socket socket = serverSocket.accept();
					Handler handler = new Handler(socket);
					handlers.add(handler);
				} catch (Throwable ta) {
					debug(mn, "caught: ", ta);
					running = false;
				}
			}
			debugLeaving(mn);
		}
	}

	private class Handler implements Runnable {
		private boolean running = false;
		private Future future;
		private Socket socket;
		private InputStream inputStream;
		private LineNumberReader reader;

		private OutputStream outputStream;

		private void println(String line) throws Exception {
			debug("println", line);
			outputStream.write(line.getBytes());
			outputStream.write(13);
			outputStream.write(10);
		}

		private Handler(Socket socket) throws Exception {
			this.socket = socket;
			inputStream = socket.getInputStream();
			reader = new LineNumberReader(new InputStreamReader(inputStream));
			outputStream = socket.getOutputStream();
			ExecutorService pool = Executors.newSingleThreadExecutor();
			running = true;
			future = pool.submit(this);
		}

		private void shutdown() {
			String mn = debugEntering("shutdown");
			running = false;
			future.cancel(true);
			if (reader != null) {
				try {
					reader.close();
				} catch (Throwable ignored) {
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Throwable ignored) {
				}
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (Throwable ignored) {
				}
			}
			debugLeaving(mn);
		}

		public void run() {
			String mn = debugEntering("run");
			boolean readingHeader = true;
			boolean writingHeader = true;
			while (running) {
				try {
					if (readingHeader) {
						String line = reader.readLine();
						debug(mn, "header line: ", line);
						readingHeader = !line.equals("");
						debug(mn, "flag in-header: ", readingHeader);
					} else {
						if (writingHeader) {
							println("HTTP/1.1 200 OK");
							println("Server: TestSimpleUploader");
							println("Pragma: no-cache");
							println("Cache-Control: no-cache");
							println("Content-Type: multipart/x-mixed-replace; boundary=myboundary");
							println("");
							writingHeader = false;
						}
						println("--myboundary");
						println("Content-Type: image/jpeg");
						byte[] image = createImage();
						println("Content-Length: " + image.length);
						println("");
						outputStream.write(image);
						println("");
						Thread.sleep(100);
					}
				} catch (Throwable ta) {
					debug(mn, "caught: ", ta);
					running = false;
				}

			}
			debugLeaving(mn);
		}

	}
}
