package wblut;


public class WB_Version {
	public static final WB_Version CURRENT_VERSION = new WB_Version();
	public static final int MAJOR = 1;
	public static final int MINOR = 8;
	public static final int PATCH = 0;
	private static final String releaseInfo = "Alexander";

	public static void main(String[] args) {
		System.out.println(CURRENT_VERSION);
	}

	private WB_Version() {
	}

	public int getMajor() {
		return MAJOR;
	}

	public int getMinor() {
		return MINOR;
	}

	public int getPatch() {
		return PATCH;
	}

	public String toString() {
		String ver = "W:Blut HE_Mesh " + MAJOR + "." + MINOR + "." + PATCH;
		if (releaseInfo != null && releaseInfo.length() > 0)
			return ver + " " + releaseInfo;
		return ver;
	}
}