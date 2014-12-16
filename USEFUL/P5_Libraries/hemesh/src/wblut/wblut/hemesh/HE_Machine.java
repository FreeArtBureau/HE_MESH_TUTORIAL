/**
 * 
 */
package wblut.hemesh;

/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public interface HE_Machine {
	public HE_Mesh apply(HE_Mesh mesh);

	public HE_Mesh apply(HE_Selection selection);

}
