package wblut;


public class WB_Disclaimer {
	public static final WB_Disclaimer CURRENT_DISCLAIMER = new WB_Disclaimer();

	public String toString() {
		String dis = "All code by Frederik Vanhoutte, W:Blut, unless noted otherwise.";
		dis += System.getProperty("line.separator");
		dis += "Attributed code should be handled by its own license. All";
		dis += System.getProperty("line.separator");
		dis += "W:Blut code is free Aleister-ware. You can redistribute it and";
		dis += System.getProperty("line.separator");
		dis += "modify it, non-commercialy and commercially. Attribution is";
		dis += System.getProperty("line.separator");
		dis += "encouraged en appreciated. But don't bother if it's a hassle...";
		dis += System.getProperty("line.separator");
		dis += System.getProperty("line.separator");
		dis += "Do what thou wilt shall be the whole of the law";
		dis += System.getProperty("line.separator");
		dis += System.getProperty("line.separator");
		dis += "Cheers!";
		return dis;
	}
}