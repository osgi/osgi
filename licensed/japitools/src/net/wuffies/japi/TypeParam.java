///////////////////////////////////////////////////////////////////////////////
// Japize - Output a machine-readable description of a Java API.
// Copyright (C) 2000,2002,2003,2004  Stuart Ballard <stuart.a.ballard@gmail.com>
// 
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////

package net.wuffies.japi;
import java.lang.reflect.Modifier;

// Note that a TypeParam *must* correspond to one of the entries in its
// getAssociatedWrapper().getTypeParams() array.
public class TypeParam extends NonArrayRefType {
  private GenericWrapper associatedWrapper;
  private String name;
  private NonArrayRefType[] bounds;
  public TypeParam(GenericWrapper associatedWrapper, String name, NonArrayRefType[] bounds) {
    if (bounds == null || bounds.length == 0) bounds = new NonArrayRefType[] {new ClassType("java.lang.Object")};
    this.associatedWrapper = associatedWrapper;
    this.name = name;
    this.bounds = bounds;
  }

  public GenericWrapper getAssociatedWrapper() {
    return associatedWrapper;
  }
  public String getName() {
    return name;
  }
  public NonArrayRefType getPrimaryConstraint() {
    return bounds[0];
  }
  public NonArrayRefType[] getBounds() {
    return bounds;
  }
  public int getIndex(GenericWrapper wrapper) {
    TypeParam[] params = getAllTypeParams(wrapper);
    if (params != null) {
      for (int i = 0; i < params.length; i++) {
        if (params[i] == this) return i;
      }
    }
    return -1;
  }
  public String getTypeSig(GenericWrapper wrapper) {
    int index = getIndex(wrapper);
    if (index < 0) throw new RuntimeException("Unbound type parameter " + this + " not associated with current wrapper " + wrapper);
    return "@" + index;
  }
  public String getJavaRepr(GenericWrapper wrapper) {
    return getTypeSig(wrapper);
  }
  public String getNonGenericTypeSig() {
    return getPrimaryConstraint().getNonGenericTypeSig();
  }
  public Type getNonGenericType() {
    return getPrimaryConstraint().getNonGenericType();
  }
  public void resolveTypeParameters() {
    for (int i = 0; i < bounds.length; i++) {
      if (bounds[i] instanceof ClassFile.UnresolvedTypeParam) {
        bounds[i] = ((ClassFile.UnresolvedTypeParam) bounds[i]).resolve();
      }
      bounds[i].resolveTypeParameters();
    }
  }

  private boolean binding = false;
  public Type bind(ClassType t) {
    debugStart("Bind", "to " + t);
    try {
      int index = getIndex(t.getWrapper());
      if (index == -1) {
        return this;
        // SABFIXME: No idea why the following doesn't work - it should be better, but apparently it isn't.
        // Probably because in the case of Enum<T extends Enum<T>> primaryConstraint == Enum<this> and you hit an infinite
        // loop. getNonGenericType() instead of bindWithFallback() might be better, or some loopbreaking code.
        // return new TypeParam(getAssociatedWrapper(), getName(), (NonArrayRefType) primaryConstraint.bindWithFallback(t));
        // Obviously the line above also would need changing now that bounds is an array instead of a single primaryConstraint.
      } else if (t.getTypeArguments() == null) {
        return null;
      } else {
        return t.getTypeArguments()[index];
      }
    } finally {
      debugEnd();
    }
  }

  public Type bindWithFallback(ClassType t) {
    Type result = bind(t);
    return result != null ? result : getPrimaryConstraint().bindWithFallback(t);
  }

  public String toStringImpl() {
    String s = "TypeParam:" + associatedWrapper.toString() + "-" + name;
    for (int i = 0; i < bounds.length; i++) {
      s += "/" + bounds[i].toString();
    }
    return s;
  }

  /**
   * Find all the type params of a particular GenericWrapper, including those inherited
   * from its containers.
   */
  public static TypeParam[] getAllTypeParams(GenericWrapper wrapper) {
    int count = 0;
    GenericWrapper container = wrapper;
    while (container != null) {
      if (container.getTypeParams() != null) {
        count += container.getTypeParams().length;
      }
      container = getContainingWrapper(container);
    }

    if (count == 0) return null;

    TypeParam[] params = new TypeParam[count];
    container = wrapper;
    int start = params.length;
    while (container != null) {
      TypeParam[] cparams = container.getTypeParams();
      if (cparams != null) {
        start -= cparams.length;
        for (int i = 0; i < cparams.length; i++) {
          params[start + i] = cparams[i];
        }
      }
      container = getContainingWrapper(container);
    }
    if (start != 0) throw new RuntimeException("Oops, internal error, didn't completely fill typeparams array");
    return params;
  }

  // Static methods and static inner classes do not inherit their container's type params, but
  // nonstatic things do.
  private static GenericWrapper getContainingWrapper(GenericWrapper wrapper) {
    if (Modifier.isStatic(wrapper.getModifiers())) {
      return null;
    } else {
      return wrapper.getContainingWrapper();
    }
  }
}
