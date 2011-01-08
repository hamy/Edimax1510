package edimax1510.simplemjpeg;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edimax1510.AbstractEdimax1510TestCase;

public class TestSimpleDownloader extends AbstractEdimax1510TestCase {

	private static final String PATH = "http://192.168.2.101/jpg/image.jpg";

	enum ChunkType {
		INVALID, MYBOUNDARY, CONTENT_TYPE, CONTEXT_LENGTH, END_OF_HEADER, JPEG_DATA, END_OF_DATA
	}

	private ChunkType chunkType;
	private StringBuilder chunkText;
	private InputStream inputStream;

	@Before
	public void setUp() throws Exception {
		String mn = debugEntering("setUp");
		chunkType = ChunkType.INVALID;
		chunkText = new StringBuilder();
		URL url = new URL(PATH);
		debug(mn, "url: ", url);
		URLConnection uc = url.openConnection();
		debug(mn, "url con: ", uc);
		String authorizationString = "Basic Z3Vlc3Q6Z3Vlc3Q=";
 		uc.setRequestProperty("Authorization", authorizationString);
		debug(mn, "url con: ", uc);
		inputStream = uc.getInputStream();
		debugLeaving(mn, "stream: ", inputStream);
	}

	@After
	public void tearDown() throws Exception {
		if (inputStream != null) {
			try {
				inputStream.close();
			} finally {
				inputStream = null;
			}
		}
	}

	@Test
	public void download() throws IOException {
		String mn = debugEntering("download");
		int nc = 0;
		while (inputStream.read() >= 0) {
			nc++;
		}
		debug(mn, "read ", nc, " bytes");
		debugLeaving(mn);
	}
}
