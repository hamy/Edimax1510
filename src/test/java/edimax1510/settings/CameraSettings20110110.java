package edimax1510.settings;

/**
 * Constant definitions for the Edimax 1510 camera.
 */
public final class CameraSettings20110110 {

	/**
	 * The fixed IP address of the camera
	 */
	public static final String E1510_IP_ADDRESS = "192.168.2.201";

	/**
	 * The path to the login-free JPEG image.
	 */
	public static final String E1510_PATH_LOGINFREE_JPG = "/loginfree.jpg";

	/**
	 * The path to the still JPEG image.
	 */
	public static final String E1510_PATH_IMAGE_JPEG = "/jpg/image.jpg";

	/**
	 * The path to the Motion JPEG video.
	 */
	public static final String E1510_PATH_VIDEO_MJPEG = "/mjpg/video.mjpg";

	/**
	 * The guest account.
	 */
	public static final String E1510_GUEST_ACCOUNT = "guest";

	/**
	 * The guest password.
	 */
	public static final String E1510_GUEST_PASSWORD = "guest";

	
	// prevent instantiation
	private CameraSettings20110110() {}
}
