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

class BoundField implements FieldWrapper, Comparable {
  private FieldWrapper base;
  private Type type;

  private BoundField(FieldWrapper base, Type type) {
    this.base = base;
    this.type = type;
  }
  public BoundField(FieldWrapper base) {
    this(base, base.getType());
  }

  public Type getType() {
    return type;
  }

  public BoundField bind(ClassType t) {
    if (t == null) return this;
    return new BoundField(base, type.bindWithFallback(t));
  }

  public int getModifiers() {return base.getModifiers();}
  public boolean isDeprecated() {return base.isDeprecated();}
  public String getName() {return base.getName();}
  public boolean isPrimitiveConstant() {return base.isPrimitiveConstant();}
  public Object getPrimitiveValue() {return base.getPrimitiveValue();}
  public ClassWrapper getDeclaringClass() {return base.getDeclaringClass();}
  public boolean isEnumField() {return base.isEnumField();}

  public int compareTo(FieldWrapper f) {
    return getName().compareTo(f.getName());
  }
  public int compareTo(Object o) {
    return compareTo((FieldWrapper) o);
  }
}
