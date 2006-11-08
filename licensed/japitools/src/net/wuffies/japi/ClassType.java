///////////////////////////////////////////////////////////////////////////////
// Japize - Output a machine-readable description of a Java API.
// Copyright (C) 2000,2002,2003,2004,2005  Stuart Ballard <stuart.a.ballard@gmail.com>
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

/**
 * Represents a class, interface, enum or annotation.
 */
public class ClassType extends NonArrayRefType {
  private String name;
  private ClassWrapper classWrapper;
  private RefType[] typeArguments;

  public ClassType(String name) {
    this(name, null);
  }
  public ClassType(ClassWrapper wrapper) {
    this(wrapper, null);
  }
  public ClassType(String name, RefType[] typeArguments) {
    this.name = name;
    if (typeArguments != null && typeArguments.length == 0) {
      this.typeArguments = null;
    } else {
      this.typeArguments = typeArguments;
    }
  }
  public ClassType(ClassWrapper wrapper, RefType[] typeArguments) {
    this(wrapper.getName(), typeArguments);
    classWrapper = wrapper;
    checkArgs();
  }
  private ClassType(String name, ClassWrapper wrapper, RefType[] typeArguments) {
    this.name = name;
    this.classWrapper = wrapper;
    this.typeArguments = typeArguments;
  }
  private void checkArgs() {
    TypeParam[] params = TypeParam.getAllTypeParams(classWrapper);
    if (params == null && typeArguments != null) {
      throw new RuntimeException("Cannot supply type arguments to a non-generic class (" + name + ")!");
    }
    // This check may be wrong because it appears to be possible to construct a case where a type is half-bound,
    // using an unbound inner class of a generic containing class. Since I hope this isn't supposed to be legal,
    // I'm leaving this check as is until I can verify it.
    if (params != null && typeArguments != null && params.length != typeArguments.length) {
      throw new RuntimeException("Cannot supply " + typeArguments.length + " type arguments to " +
                                 name + " that expects " + params.length + " parameters!");
    }
  }

  public String getName() {
    return name;
  }
  public String getJavaRepr(GenericWrapper wrapper) {
    String repr = name;
    if (typeArguments != null) {
      repr += "<";
      for (int i = 0; i < typeArguments.length; i++) {
        if (i > 0) repr += ",";
        repr += typeArguments[i].getTypeSig(wrapper);
      }
      repr += ">";
    }
    return repr;
  }
  public ClassWrapper getWrapper() {
    if (classWrapper == null) {
      classWrapper = ClassFile.forName(name);
      checkArgs();
    }
    return classWrapper;
  }
  public RefType[] getTypeArguments() {
    return typeArguments;
  }
  public String getTypeSig(GenericWrapper wrapper) {
    StringBuffer sb = new StringBuffer("L" + name.replace('.', '/'));
    RefType[] args = getTypeArguments();
    if (args != null && args.length > 0) {
      sb.append('<');
      for (int i = 0; i < args.length; i++) {
        if (i > 0) sb.append(',');
        if (args[i] != null) sb.append(args[i].getTypeSig(wrapper));
      }
      sb.append('>');
    }
    sb.append(';');
    return sb.toString();
  }
  public String getNonGenericTypeSig() {
    return "L" + name.replace('.', '/') + ";";
  }
  public Type getNonGenericType() {
    if (typeArguments == null) return this;

    return new ClassType(name, classWrapper, null);
  }

  public String toStringImpl() {
    String result = "Class:" + getName();
    if (typeArguments != null) {
      result += "<";
      for (int i = 0; i < typeArguments.length; i++) {
        if (i > 0) result += ",";
        result += typeArguments[i];
      }
      result += ">";
    }
    return result;
  }

  public Type bind(ClassType t) {
    debugStart("Bind", "to " + t);
    try {
      RefType[] typeArgs = getTypeArguments();

      if (typeArgs == null) return this;

      RefType[] boundArgs = new RefType[typeArgs.length];
      boolean isRaw = true;
      for (int i = 0; i < typeArgs.length; i++) {
        boundArgs[i] = (RefType) typeArgs[i].bind(t);
        if (boundArgs[i] != null) isRaw = false;
      }
      // If none of the args ended up bound to anything, it's effectively a raw type.
      if (isRaw) boundArgs = null;
      
      return new ClassType(name, classWrapper, boundArgs);
    } finally {
      debugEnd();
    }
  }

  public void resolveTypeParameters() {
    if (typeArguments != null) {
      for (int i = 0; i < typeArguments.length; i++) {
        typeArguments[i] = (RefType)resolveTypeParameter(typeArguments[i]);
      }
    }
  }
}
