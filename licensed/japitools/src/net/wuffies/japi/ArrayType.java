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

public class ArrayType extends RefType {
  private Type elementType;
  public ArrayType(Type elementType) {
    if (elementType == null) {
      throw new NullPointerException("Cannot have an array with an unbound element type!");
    }
    this.elementType = elementType;
  }
  public Type getElementType() {
    return elementType;
  }
  public String getTypeSig(GenericWrapper wrapper) {
    return "[" + getElementType().getTypeSig(wrapper);
  }
  public String getNonGenericTypeSig() {
    return "[" + getElementType().getNonGenericTypeSig();
  }
  public Type getNonGenericType() {
    return new ArrayType(elementType.getNonGenericType());
  }
  public String toStringImpl() {
    return "Array:" + getElementType();
  }
  public Type bind(ClassType t) {
    debugStart("Bind", "to " + t);
    try {
      return new ArrayType(getElementType().bindWithFallback(t));
    } finally {
      debugEnd();
    }
  }
  public void resolveTypeParameters() {
    if (elementType instanceof RefType) {
      elementType = resolveTypeParameter((RefType)elementType);
    }
  }
}
