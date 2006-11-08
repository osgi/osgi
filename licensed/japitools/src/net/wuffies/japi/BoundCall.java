///////////////////////////////////////////////////////////////////////////////
// Japize - Output a machine-readable description of a Java API.
// Copyright (C) 2000,2002,2003,2004,2005,2006  Stuart Ballard <stuart.a.ballard@gmail.com>
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

class BoundCall implements CallWrapper, Comparable {
  private CallWrapper base;
  private Type returnType;
  private Type[] parameterTypes;
  private NonArrayRefType[] exceptionTypes;
  private TypeParam[] typeParams;
  private GenericWrapper containingWrapper;
  private boolean visible14 = true;
  private boolean visible15 = true;

  private BoundCall(CallWrapper base, GenericWrapper containingWrapper,
                    Type returnType, Type[] parameterTypes,
                    NonArrayRefType[] exceptionTypes, TypeParam[] typeParams,
                    boolean visible14, boolean visible15) {
    this.base = base;
    this.containingWrapper = containingWrapper;
    this.returnType = returnType;
    this.parameterTypes = parameterTypes;
    this.exceptionTypes = exceptionTypes;
    this.typeParams = typeParams;
    if (!(visible14 || visible15)) throw new RuntimeException("Can't be excluded from *everywhere*!");
    this.visible14 = visible14;
    this.visible15 = visible15;
  }
  public BoundCall(CallWrapper base, GenericWrapper containingWrapper) {
    this(base, containingWrapper, base.getReturnType(), base.getParameterTypes(),
         base.getExceptionTypes(), base.getTypeParams(), true, true);
  }

  public Type getReturnType() {
    return returnType;
  }
  public Type[] getParameterTypes() {
    return parameterTypes;
  }
  public NonArrayRefType[] getExceptionTypes() {
    return exceptionTypes;
  }
  public TypeParam[] getTypeParams() {
    return typeParams;
  }
  public GenericWrapper getContainingWrapper() {
    return containingWrapper;
  }
  public boolean isVisible14() {
    return visible14;
  }
  public boolean isVisible15() {
    return visible15;
  }

  public BoundCall bind(ClassType t) {
    if (!visible15 || t == null) return this;

    Type[] newParameterTypes = new Type[parameterTypes.length];
    for (int i = 0; i < parameterTypes.length; i++) {
      newParameterTypes[i] = parameterTypes[i].bindWithFallback(t);
    }
    NonArrayRefType[] newExceptionTypes = new NonArrayRefType[exceptionTypes.length];
    for (int i = 0; i < exceptionTypes.length; i++) {
      newExceptionTypes[i] = (NonArrayRefType) exceptionTypes[i].bindWithFallback(t);
    }
    TypeParam[] newTypeParams = null;
    if (typeParams != null) {
      newTypeParams = new TypeParam[typeParams.length];
      for (int i = 0; i < typeParams.length; i++) {
        newTypeParams[i] = (TypeParam) typeParams[i].bindWithFallback(t);
      }
    }
    BoundCall result = new BoundCall(base, containingWrapper, returnType.bindWithFallback(t),
                                    newParameterTypes, newExceptionTypes, newTypeParams,
                                    visible14, visible15);
    if (!getNonGenericSig().equals(result.getNonGenericSig())) {
      // shouldn't happen - !visible15 was handled above...
      if (!result.visible15) throw new RuntimeException("Can't be excluded from *everywhere*!");
      result.visible14 = false;
    }
    return result;
  }
  public BoundCall bind14() {
    if (!visible14) return null;

    Type[] newParameterTypes = new Type[parameterTypes.length];
    for (int i = 0; i < parameterTypes.length; i++) {
      newParameterTypes[i] = parameterTypes[i].getNonGenericType();
    }
    NonArrayRefType[] newExceptionTypes = new NonArrayRefType[exceptionTypes.length];
    for (int i = 0; i < exceptionTypes.length; i++) {
      newExceptionTypes[i] = (NonArrayRefType) exceptionTypes[i].getNonGenericType();
    }
    return new BoundCall(base, containingWrapper, returnType.getNonGenericType(),
                         newParameterTypes, newExceptionTypes, null, visible14, false);
  }

  public int getModifiers() {return base.getModifiers();}
  public boolean isDeprecated() {return base.isDeprecated();}
  public String getName() {return base.getName();}
  public Object getDefaultValue() {return base.getDefaultValue();}
  public ClassWrapper getDeclaringClass() {return base.getDeclaringClass();}
  public boolean isInheritable() {return base.isInheritable();}

  private static String getNonGenericSig(CallWrapper call) {
		String sig = call.getName() + "(";
		Type[] parameterTypes = call.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) 
		{
        if (i > 0) sig += ",";
		    sig += parameterTypes[i].getNonGenericTypeSig();
		}
		sig += ")";
    return sig;
  }
  public String getNonGenericSig() {
    return getNonGenericSig(this);
  }

  public int compareTo(CallWrapper call) {
    int result = getNonGenericSig().compareTo(getNonGenericSig(call));
    if (result != 0) return result;
    if (call instanceof BoundCall) {
      if (visible15 && !((BoundCall) call).visible15) return 1;
      if (!visible15 && ((BoundCall) call).visible15) return -1;
    }
    return getReturnType().getNonGenericTypeSig().compareTo(call.getReturnType().getNonGenericTypeSig());
  }
  public int compareTo(Object o) {
    return compareTo((CallWrapper) o);
  }

  public String toString() {
    return containingWrapper + "." + getNonGenericSig();
  }
}