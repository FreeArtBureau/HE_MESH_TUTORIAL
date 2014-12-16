/**
 * 
 */
package wblut.geom;

import javolution.util.FastList;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_BSPNode {
	protected WB_Plane				partition;
	protected FastList<WB_ExplicitPolygon>	polygons;
	protected WB_BSPNode			pos	= null;
	protected WB_BSPNode			neg	= null;

	public WB_BSPNode() {
		polygons = new FastList<WB_ExplicitPolygon>();
	}

}