/**
 * 
 */
package wblut.core;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class Flag {
	private int	_flags;

	public Flag() {

	}

	public void setFlag(final int i, final boolean flag) {
		int mask = 1 << (i - 1);
		if (flag) {
			_flags |= mask;
		} else {
			mask = ~mask;
			_flags &= mask;
		}

	}

	public boolean getFlag(final int i) {
		final int mask = 1 << (i - 1);
		return ((_flags & mask) >> (i - 1)) == 1;
	}
}
