package edimax1510.simplemjpeg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import edimax1510.AbstractEdimax1510TestCase;
import static edimax1510.settings.CameraSettings20110110.*;

import sun.misc.BASE64Encoder;

public class TestVideo extends AbstractEdimax1510TestCase {

	private static final int BUFFER_SIZE = 65536;
	private byte[] buffer;
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;

	@Before
	public void setUp() throws Exception {
		String mn = debugEntering("setUp");
		buffer = new byte[BUFFER_SIZE];
		socket = new Socket(E1510_IP_ADDRESS, 4321);
		debug(mn, "socket: ", socket);
		inputStream = socket.getInputStream();
		debugLeaving(mn, "stream: ", inputStream);
		outputStream = socket.getOutputStream();
		debugLeaving(mn, "stream: ", outputStream);
	}

	@After
	public void tearDown() throws Exception {
		String mn = debugEntering("tearDown", "input stream: ", inputStream);
		if (inputStream != null) {
			try {
				inputStream.close();
			} finally {
				inputStream = null;
			}
		}
		if (outputStream != null) {
			try {
				outputStream.close();
			} finally {
				outputStream = null;
			}
		}
		if (socket != null) {
			try {
				socket.close();
			} finally {
				socket = null;
			}
		}
		debugLeaving(mn);
	}

	@Test
	public void download() throws IOException, NotFoundException,
			ChecksumException, FormatException {
		String mn = debugEntering("download");
		//outputStream.write(123);
		outputStream.flush();
		debug(mn, "flushed.");
		for (int i = 0; i < 10; i++) {
			int n = inputStream.read(buffer);
			debug(mn, "read ", i, ": ", n);
		}
		debugLeaving(mn);
	}
}
