package edimax1510.simplemjpeg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import sun.misc.BASE64Encoder;

public class TestSimpleDownloader extends AbstractEdimax1510TestCase {

	private static final String PATH = "http://192.168.2.100/mjpg/video.mjpg";
	private static final int MAX_IMAGE_COUNT = 100;

	enum ChunkType {
		INVALID, MYBOUNDARY, CONTENT_TYPE, CONTEXT_LENGTH, END_OF_HEADER, JPEG_DATA, END_OF_DATA
	}

	private ChunkType chunkType;
	private InputStream inputStream;
	private int contentLength;
	private int imageCount;

	private String readLine() throws IOException {
		StringBuilder sb = new StringBuilder();
		boolean foundCR = false;
		boolean foundLF = false;
		while (true) {
			int ch = inputStream.read();
			switch (ch) {
			case -1:
				throw new IOException("Premature EOF");
			case 0x0d:
				foundCR = true;
				break;
			case 0x0a:
				foundLF = foundCR;
				if (foundLF) {
					return sb.toString();
				}
				break;
			default:
				foundLF = foundCR = false;
				sb.append((char) ch);
			}
		}
	}

	private static final byte[] GUEST_CREDENTIALS = "guest:guest".getBytes();

	@Before
	public void setUp() throws Exception {
		String mn = debugEntering("setUp");
		URL url = new URL(PATH);
		debug(mn, "url: ", url);
		URLConnection uc = url.openConnection();
		debug(mn, "url con: ", uc);
		BASE64Encoder encoder = new BASE64Encoder();
		String encoded = encoder.encode(GUEST_CREDENTIALS);
		debug(mn, "encoded credentials: ", encoded);
		uc.setRequestProperty("Authorization", "Basic " + encoded);
		debug(mn, "url con: ", uc);
		inputStream = uc.getInputStream();
		imageCount = 0;
		debugLeaving(mn, "stream: ", inputStream);
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
		debugLeaving(mn);
	}

	@Test
	public void download() throws IOException, NotFoundException,
			ChecksumException, FormatException {
		String mn = debugEntering("download");
		String line;
		chunkType = ChunkType.INVALID;
		Pattern p = Pattern.compile("^Content-Length:\\s+(\\d+)$");
		int okCount = 0;

		while (imageCount < MAX_IMAGE_COUNT) {
			debug(mn, "old chunk type: ", chunkType);
			switch (chunkType) {
			case INVALID:
				line = readLine();
				if (line.equals("--myboundary")) {
					chunkType = ChunkType.MYBOUNDARY;
				}
				break;
			case MYBOUNDARY:
				line = readLine();
				if (line.equals("Content-Type: image/jpeg")) {
					chunkType = ChunkType.CONTENT_TYPE;
				} else {
					chunkType = ChunkType.INVALID;
				}
				break;
			case CONTENT_TYPE:
				line = readLine();
				Matcher m = p.matcher(line);
				if (m.matches()) {
					debug(mn, "matching line: ", line);
					String g = m.group(1);
					debug(mn, "group: ", g);
					contentLength = Integer.parseInt(g);
					debug(mn, "content length: ", contentLength);
					chunkType = (contentLength > 0) ? ChunkType.CONTEXT_LENGTH
							: ChunkType.INVALID;
				} else {
					chunkType = ChunkType.INVALID;
				}
				break;
			case CONTEXT_LENGTH:
				line = readLine();
				if (line.equals("")) {
					chunkType = ChunkType.END_OF_HEADER;
				} else {
					chunkType = ChunkType.INVALID;
				}
				break;
			case END_OF_HEADER:
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				for (int i = 0; i < contentLength; i++) {
					int by = inputStream.read();
					baos.write(by);
				}
				chunkType = ChunkType.JPEG_DATA;
				imageCount++;
				debug(mn, "image #: ", imageCount);
				File file = new File("/tmp/" + imageCount + ".jpg");
				FileOutputStream fos = new FileOutputStream(file);
				byte[] ba = baos.toByteArray();
				fos.write(ba);
				InputStream bais = new ByteArrayInputStream(ba);
				BufferedImage bi = ImageIO.read(bais);
				debug(mn, "buffered image: ", bi);
				bais.close();
				baos.close();
				fos.close();

				LuminanceSource source = new BufferedImageLuminanceSource(bi);
				BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(
						source));
				Reader reader = new MultiFormatReader();
				try {
					Result result = reader.decode(bitmap);
					okCount++;
					debug(mn, "result: ", result);
					debug(mn, "ok count: ", okCount);
					debug(mn, "text: ", result.getText());
					debug(mn, "ts: ", result.getTimestamp());
					debug(mn, "fmt: ", result.getBarcodeFormat());
				} catch (NotFoundException nfe) {
					debug(mn, "oops: ", nfe);
				}
				break;
			case JPEG_DATA:
				line = readLine();
				chunkType = ChunkType.INVALID;
				break;
			case END_OF_DATA:
				break;
			}
			debug(mn, "new chunk type: ", chunkType);
		}
		debugLeaving(mn);
	}
}
