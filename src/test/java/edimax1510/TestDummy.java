/**
 * 
 */
package edimax1510;

import org.junit.Test;

/**
 * @author rz65
 * 
 */
public class TestDummy extends AbstractEdimax1510TestCase {

	@Test
	public void testDummy() {
		String mn = debugEntering("testDummy");
		debug(mn, "foo");
		debug(mn, "bar");
		debugLeaving(mn);
	}
}
