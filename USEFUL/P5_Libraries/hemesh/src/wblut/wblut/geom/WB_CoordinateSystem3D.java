package wblut.geom;

import wblut.WB_Epsilon;



public class WB_CoordinateSystem3D {
	private WB_CoordinateSystem3D _parent;

	protected final static WB_CoordinateSystem3D WORLD() {
		return new WB_CoordinateSystem3D(true);
	}

	private WB_Point3d _origin;
	private WB_Vector3d _X;
	private WB_Vector3d _Y;
	private WB_Vector3d _Z;
	private boolean _isWorld;

	protected WB_CoordinateSystem3D(final WB_Point3d origin,
			final WB_Vector3d x, final WB_Vector3d y, final WB_Vector3d z,
			final WB_CoordinateSystem3D parent) {
		_origin = origin.get();
		_X = x.get();
		_Y = y.get();
		_Z = z.get();
		_parent = parent;
		_isWorld = (_parent == null);
	}

	protected WB_CoordinateSystem3D(final boolean world) {
		_origin = WB_Point3d.ZERO();
		_X = WB_Vector3d.X();
		_Y = WB_Vector3d.Y();
		_Z = WB_Vector3d.Z();
		_isWorld = world;
		_parent = (world) ? null : WORLD();
	}

	public WB_CoordinateSystem3D() {
		this(false);
	}

	public WB_CoordinateSystem3D(final WB_CoordinateSystem3D parent) {
		_origin = WB_Point3d.ZERO();
		_X = WB_Vector3d.X();
		_Y = WB_Vector3d.Y();
		_Z = WB_Vector3d.Z();
		_parent = parent;
		_isWorld = (_parent == null);
	}

	public WB_CoordinateSystem3D get() {
		return new WB_CoordinateSystem3D(_origin, _X, _Y, _Z, _parent);
	}

	protected void set(final WB_Point3d origin, final WB_Vector3d x,
			final WB_Vector3d y, final WB_Vector3d z) {
		_origin = origin.get();
		_X = x.get();
		_Y = y.get();
		_Z = z.get();
	}

	public WB_CoordinateSystem3D setParent(final WB_CoordinateSystem3D parent) {
		_parent = parent;
		_isWorld = (_parent == null);
		return this;
	}

	public WB_CoordinateSystem3D setOrigin(final WB_Point3d o) {
		_origin.set(o);
		return this;
	}

	public WB_CoordinateSystem3D setOrigin(final double ox, final double oy,
			final double oz) {
		_origin.set(ox, oy, oz);
		return this;
	}

	public WB_CoordinateSystem3D setXY(final WB_Vector3d X, final WB_Vector3d Y) {
		_X.set(X);
		_X.normalize();
		_Y.set(Y);
		_Y.normalize();
		_Z.set(WB_Vector3d.cross(_X, _Y));
		if (WB_Epsilon.isZeroSq(_Z.mag2())) {
			throw new IllegalArgumentException("Vectors can not be parallel.");
		}
		_Z.normalize();
		_Y.set(WB_Vector3d.cross(_Z, _X));
		_Y.normalize();
		return this;
	}

	public WB_CoordinateSystem3D setYX(final WB_Vector3d Y, final WB_Vector3d X) {
		_X.set(X);
		_X.normalize();
		_Y.set(Y);
		_Y.normalize();
		_Z.set(WB_Vector3d.cross(_X, _Y));
		if (WB_Epsilon.isZeroSq(_Z.mag2())) {
			throw new IllegalArgumentException("Vectors can not be parallel.");
		}
		_Z.normalize();
		_X.set(WB_Vector3d.cross(_Y, _Z));
		_X.normalize();
		return this;
	}

	public WB_CoordinateSystem3D setXZ(final WB_Vector3d X, final WB_Vector3d Z) {
		_X.set(X);
		_X.normalize();
		_Z.set(Z);
		_Z.normalize();
		_Y.set(WB_Vector3d.cross(_Z, _X));
		if (WB_Epsilon.isZeroSq(_Y.mag2())) {
			throw new IllegalArgumentException("Vectors can not be parallel.");
		}
		_Y.normalize();
		_Z.set(WB_Vector3d.cross(_X, _Y));
		_Z.normalize();
		return this;
	}

	public WB_CoordinateSystem3D setZX(final WB_Vector3d Z, final WB_Vector3d X) {
		_X.set(X);
		_X.normalize();
		_Z.set(Z);
		_Z.normalize();
		_Y.set(WB_Vector3d.cross(_Z, _X));
		if (WB_Epsilon.isZeroSq(_Y.mag2())) {
			throw new IllegalArgumentException("Vectors can not be parallel.");
		}
		_Y.normalize();
		_X.set(WB_Vector3d.cross(_Y, _Z));
		_X.normalize();
		return this;
	}

	public WB_CoordinateSystem3D setYZ(final WB_Vector3d Y, final WB_Vector3d Z) {
		_Y.set(Y);
		_Y.normalize();
		_Z.set(Z);
		_Z.normalize();
		_X.set(WB_Vector3d.cross(_Y, _Z));
		if (WB_Epsilon.isZeroSq(_X.mag2())) {
			throw new IllegalArgumentException("Vectors can not be parallel.");
		}
		_X.normalize();
		_Z.set(WB_Vector3d.cross(_X, _Y));
		_Z.normalize();
		return this;
	}

	public WB_CoordinateSystem3D setZY(final WB_Vector3d Z, final WB_Vector3d Y) {
		_Y.set(Y);
		_Y.normalize();
		_Z.set(Z);
		_Z.normalize();
		_X.set(WB_Vector3d.cross(_Y, _Z));
		if (WB_Epsilon.isZeroSq(_X.mag2())) {
			throw new IllegalArgumentException("Vectors can not be parallel.");
		}
		_X.normalize();
		_Y.set(WB_Vector3d.cross(_Z, _X));
		_Y.normalize();
		return this;
	}

	public WB_Vector3d getX() {
		return _X.get();
	}

	public WB_Vector3d getY() {
		return _Y.get();
	}

	public WB_Vector3d getZ() {
		return _Z.get();
	}

	public WB_Point3d getOrigin() {
		return _origin.get();
	}

	public WB_CoordinateSystem3D getParent() {
		return _parent;
	}

	public boolean isWorld() {
		return _isWorld;
	}

	public WB_CoordinateSystem3D setXY(final double xx, final double xy,
			final double xz, final double yx, final double yy, final double yz) {
		_X.set(xx, xy, xz);
		_X.normalize();
		_Y.set(yx, yy, yz);
		_Y.normalize();
		_Z.set(WB_Vector3d.cross(_X, _Y));
		if (WB_Epsilon.isZeroSq(_Z.mag2())) {
			throw new IllegalArgumentException("Vectors can not be parallel.");
		}
		_Z.normalize();
		_Y.set(WB_Vector3d.cross(_Z, _X));
		_Y.normalize();
		return this;
	}

	public WB_CoordinateSystem3D setYX(final double yx, final double yy,
			final double yz, final double xx, final double xy, final double xz) {
		_X.set(xx, xy, xz);
		_X.normalize();
		_Y.set(yx, yy, yz);
		_Y.normalize();
		_Z.set(WB_Vector3d.cross(_X, _Y));
		if (WB_Epsilon.isZeroSq(_Z.mag2())) {
			throw new IllegalArgumentException("Vectors can not be parallel.");
		}
		_Z.normalize();
		_X.set(WB_Vector3d.cross(_Y, _Z));
		_X.normalize();
		return this;
	}

	public WB_CoordinateSystem3D setXZ(final double xx, final double xy,
			final double xz, final double zx, final double zy, final double zz) {
		_X.set(xx, xy, xz);
		_X.normalize();
		_Z.set(zx, zy, zz);
		_Z.normalize();
		_Y.set(WB_Vector3d.cross(_Z, _X));
		if (WB_Epsilon.isZeroSq(_Y.mag2())) {
			throw new IllegalArgumentException("Vectors can not be parallel.");
		}
		_Y.normalize();
		_Z.set(WB_Vector3d.cross(_X, _Y));
		_Z.normalize();
		return this;
	}

	public WB_CoordinateSystem3D setZX(final double zx, final double zy,
			final double zz, final double xx, final double xy, final double xz) {
		_X.set(xx, xy, xz);
		_X.normalize();
		_Z.set(zx, zy, zz);
		_Z.normalize();
		_Y.set(WB_Vector3d.cross(_Z, _X));
		if (WB_Epsilon.isZeroSq(_Y.mag2())) {
			throw new IllegalArgumentException("Vectors can not be parallel.");
		}
		_Y.normalize();
		_X.set(WB_Vector3d.cross(_Y, _Z));
		_X.normalize();
		return this;
	}

	public WB_CoordinateSystem3D setYZ(final double yx, final double yy,
			final double yz, final double zx, final double zy, final double zz) {
		_Y.set(yx, yy, yz);
		_Y.normalize();
		_Z.set(zx, zy, zz);
		_Z.normalize();
		_X.set(WB_Vector3d.cross(_Y, _Z));
		if (WB_Epsilon.isZeroSq(_X.mag2())) {
			throw new IllegalArgumentException("Vectors can not be parallel.");
		}
		_X.normalize();
		_Z.set(WB_Vector3d.cross(_X, _Y));
		_Z.normalize();
		return this;
	}

	public WB_CoordinateSystem3D setZY(final double zx, final double zy,
			final double zz, final double yx, final double yy, final double yz) {
		_Y.set(yx, yy, yz);
		_Y.normalize();
		_Z.set(zx, zy, zz);
		_Z.normalize();
		_X.set(WB_Vector3d.cross(_Y, _Z));
		if (WB_Epsilon.isZeroSq(_X.mag2())) {
			throw new IllegalArgumentException("Vectors can not be parallel.");
		}
		_X.normalize();
		_Y.set(WB_Vector3d.cross(_Z, _X));
		_Y.normalize();
		return this;
	}

	public WB_CoordinateSystem3D setX(final WB_Vector3d X) {
		final WB_Vector3d lX = X.get();
		lX.normalize();
		final WB_Vector3d tmp = lX.cross(_X);
		if (!WB_Epsilon.isZeroSq(tmp.mag2())) {
			rotate(-Math.acos(_X.dot(lX)), tmp);
		} else if (_X.dot(lX) < -1 + WB_Epsilon.EPSILON) {
			flipX();
		}
		return this;
	}

	public WB_CoordinateSystem3D setY(final WB_Vector3d Y) {
		final WB_Vector3d lY = Y.get();
		lY.normalize();
		final WB_Vector3d tmp = lY.cross(_Y);
		if (!WB_Epsilon.isZeroSq(tmp.mag2())) {
			rotate(-Math.acos(_Y.dot(lY)), tmp);
		} else if (_Y.dot(lY) < -1 + WB_Epsilon.EPSILON) {
			flipY();
		}
		return this;
	}

	public WB_CoordinateSystem3D setZ(final WB_Vector3d Z) {
		final WB_Vector3d lZ = Z.get();
		lZ.normalize();
		final WB_Vector3d tmp = lZ.cross(_Z);
		if (!WB_Epsilon.isZeroSq(tmp.mag2())) {
			rotate(-Math.acos(_Z.dot(lZ)), tmp);
		} else if (_Z.dot(lZ) < -1 + WB_Epsilon.EPSILON) {
			flipZ();
		}
		return this;
	}

	public WB_CoordinateSystem3D rotateX(final double a) {
		_Y.rotateAboutOrigin(a, _X);
		_Z.rotateAboutOrigin(a, _X);
		return this;
	}

	public WB_CoordinateSystem3D rotateY(final double a) {
		_X.rotateAboutOrigin(a, _Y);
		_Z.rotateAboutOrigin(a, _Y);
		return this;
	}

	public WB_CoordinateSystem3D rotateZ(final double a) {
		_X.rotateAboutOrigin(a, _Z);
		_Y.rotateAboutOrigin(a, _Z);
		return this;
	}

	public WB_CoordinateSystem3D rotate(final double a, final WB_Vector3d v) {
		final WB_Vector3d lv = v.get();
		lv.normalize();
		_X.rotateAboutOrigin(a, lv);
		_Y.rotateAboutOrigin(a, lv);
		_Z.rotateAboutOrigin(a, lv);
		return this;
	}

	public WB_Transform getTransformFromParent() {
		final WB_Transform result = new WB_Transform();
		result.addFromParentToCS(this);
		return result;
	}

	public WB_Transform getTransformToParent() {
		final WB_Transform result = new WB_Transform();
		result.addFromCSToParent(this);
		return result;
	}

	public WB_Transform getTransformFromWorld() {
		final WB_Transform result = new WB_Transform();
		result.addFromWorldToCS(this);
		return result;
	}

	public WB_Transform getTransformToWorld() {
		final WB_Transform result = new WB_Transform();
		result.addFromCSToWorld(this);
		return result;
	}

	public WB_Transform getTransformFrom(final WB_CoordinateSystem3D CS) {
		final WB_Transform result = new WB_Transform();
		result.addFromCSToCS(CS, this);
		return result;
	}

	public WB_Transform getTransformTo(final WB_CoordinateSystem3D CS) {
		final WB_Transform result = new WB_Transform();
		result.addFromCSToCS(this, CS);
		return result;
	}

	public WB_CoordinateSystem3D setX(final double xx, final double xy,
			final double xz) {
		final WB_Vector3d lX = new WB_Vector3d(xx, xy, xz);
		lX.normalize();
		final WB_Vector3d tmp = lX.cross(_X);
		if (!WB_Epsilon.isZeroSq(tmp.mag2())) {
			rotate(-Math.acos(_X.dot(lX)), tmp);
		} else if (_X.dot(lX) < -1 + WB_Epsilon.EPSILON) {
			flipX();
		}
		return this;
	}

	public WB_CoordinateSystem3D setY(final double yx, final double yy,
			final double yz) {
		final WB_Vector3d lY = new WB_Vector3d(yx, yy, yz);
		lY.normalize();
		final WB_Vector3d tmp = lY.cross(_Y);
		if (!WB_Epsilon.isZeroSq(tmp.mag2())) {
			rotate(-Math.acos(_Y.dot(lY)), tmp);
		} else if (_Y.dot(lY) < -1 + WB_Epsilon.EPSILON) {
			flipY();
		}
		return this;
	}

	public WB_CoordinateSystem3D setZ(final double zx, final double zy,
			final double zz) {
		final WB_Vector3d lZ = new WB_Vector3d(zx, zy, zz);
		lZ.normalize();
		final WB_Vector3d tmp = lZ.cross(_Z);
		if (!WB_Epsilon.isZeroSq(tmp.mag2())) {
			rotate(-Math.acos(_Z.dot(lZ)), tmp);
		} else if (_Z.dot(lZ) < -1 + WB_Epsilon.EPSILON) {
			flipZ();
		}
		return this;
	}

	public void flipX() {
		_X.mult(-1);
		_Y.mult(-1);
	}

	public void flipY() {
		_X.mult(-1);
		_Y.mult(-1);
	}

	public void flipZ() {
		_Z.mult(-1);
		_Y.mult(-1);
	}

	@Override
	public String toString() {
		return "WB_CoordinateSystem3d: origin: " + _origin + " [X=" + _X
				+ ", Y=" + _Y + ", Z=" + _Z + "]";
	}
}
