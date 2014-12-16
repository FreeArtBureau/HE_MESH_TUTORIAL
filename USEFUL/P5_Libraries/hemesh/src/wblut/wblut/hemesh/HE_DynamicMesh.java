/**
 * 
 */
package wblut.hemesh;

import java.util.ArrayList;


/**
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class HE_DynamicMesh extends HE_Mesh {
	private final ArrayList<HE_Machine>	modifierStack;
	private HE_Mesh							bkp;

	public HE_DynamicMesh(final HE_Mesh baseMesh) {
		this.set(baseMesh);
		bkp = get();
		modifierStack = new ArrayList<HE_Machine>();
	}

	public void update() {
		this.set(bkp);
		applyStack();
	}

	private void applyStack() {
		for (int i = 0; i < modifierStack.size(); i++) {
			modifierStack.get(i).apply(this);
		}

	}

	public void add(final HE_Machine mod) {
		modifierStack.add(mod);
	}

	public void remove(final HE_Machine mod) {
		modifierStack.remove(mod);
	}

	@Override
	public void clear() {
		modifierStack.clear();
		set(bkp);
	}

	public HE_DynamicMesh setBaseMesh(final HE_Mesh baseMesh) {
		set(baseMesh);
		bkp = get();
		applyStack();
		return this;
	}

}
