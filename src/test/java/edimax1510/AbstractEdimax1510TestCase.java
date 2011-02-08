/**
 * 
 */
package edimax1510;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the base class for test cases of this projects. It provides logging.
 */
public abstract class AbstractEdimax1510TestCase {

	private final Logger logger;

	/**
	 * Initializes the test case instance. A {@link Logger} instance is
	 * associated with the fully-qualified class name.
	 */
	public AbstractEdimax1510TestCase() {
		logger = LoggerFactory.getLogger(getClass());
	}

	private void buildMessage(char level, String mn, String mnDetails,
			Object... messageParts) {
		switch (level) {
		case 'd':
			if (!logger.isDebugEnabled())
				return;
			break;
		case 'i':
			if (!logger.isInfoEnabled())
				return;
			break;
		case 'w':
			if (!logger.isWarnEnabled())
				return;
			break;
		case 'e':
			if (!logger.isErrorEnabled())
				return;
			break;
		}
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(mn);
		if (mnDetails != null) {
			sb.append('/');
			sb.append(mnDetails);
		}
		sb.append("] ");
		for (Object messagePart : messageParts) {
			sb.append(messagePart);
		}
		switch (level) {
		case 'd':
			logger.debug(sb.toString());
			break;
		case 'i':
			logger.info(sb.toString());
			break;
		case 'w':
			logger.warn(sb.toString());
			break;
		case 'e':
			logger.error(sb.toString());
			break;
		}
	}

	/**
	 * Method specific prolog.
	 * 
	 * @param mn
	 *            The name of the method.
	 * @param messageParts
	 *            The message details.
	 * @return The method name.
	 */
	public String debugEntering(String mn, Object... messageParts) {
		buildMessage('d', mn, "Entering", messageParts);
		return mn;
	}

	/**
	 * Method specific epilog.
	 * 
	 * @param mn
	 *            The name of the method.
	 * @param messageParts
	 *            The message details.
	 */
	public void debugLeaving(String mn, Object... messageParts) {
		buildMessage('d', mn, "Leaving", messageParts);
	}

	/**
	 * Debug method.
	 * 
	 * @param mn
	 *            The name of the method.
	 * @param messageParts
	 *            The message details.
	 */
	public void debug(String mn, Object... messageParts) {
		buildMessage('d', mn, null, messageParts);
	}

	/**
	 * Debug method.
	 * 
	 * @param mn
	 *            The name of the method.
	 * @param messageParts
	 *            The message details.
	 */
	public void info(String mn, Object... messageParts) {
		buildMessage('i', mn, null, messageParts);
	}

	/**
	 * Debug method.
	 * 
	 * @param mn
	 *            The name of the method.
	 * @param messageParts
	 *            The message details.
	 */
	public void warn(String mn, Object... messageParts) {
		buildMessage('w', mn, null, messageParts);
	}

	/**
	 * Debug method.
	 * 
	 * @param mn
	 *            The name of the method.
	 * @param messageParts
	 *            The message details.
	 */
	public void error(String mn, Object... messageParts) {
		buildMessage('e', mn, null, messageParts);
	}
}
