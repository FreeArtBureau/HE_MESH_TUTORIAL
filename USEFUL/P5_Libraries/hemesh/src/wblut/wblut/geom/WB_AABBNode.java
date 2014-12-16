/**
 * 
 */
package wblut.geom;

import java.util.ArrayList;

import wblut.hemesh.HE_Face;



/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class WB_AABBNode {
	protected int					level;
	protected WB_AABB				aabb;
	protected WB_AABBNode			positive;
	protected WB_AABBNode			negative;
	protected WB_AABBNode			mid;
	protected WB_Plane				separator;
	protected ArrayList<HE_Face>	faces;
	protected boolean				isLeaf;

	public WB_AABBNode() {
		level = -1;
		faces = new ArrayList<HE_Face>();
	}

	public WB_AABB getAABB() {
		return aabb;
	}

	public WB_Plane getSeparator() {
		return separator;
	}

	public int getLevel() {
		return level;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public ArrayList<HE_Face> getFaces() {
		return faces;
	}

	public WB_AABBNode getPosChild() {
		return positive;

	}

	public WB_AABBNode getNegChild() {
		return negative;

	}

	public WB_AABBNode getMidChild() {
		return mid;

	}

}
