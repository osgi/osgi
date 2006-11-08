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

public interface FieldWrapper extends Wrapper, Comparable {
  String getName();
  Type getType();
  boolean isPrimitiveConstant();
  Object getPrimitiveValue();
  ClassWrapper getDeclaringClass();

  /**
   * True if this field is one of the special fields of an enum type that defines the
   * values of the enumeration. FIXME: Is this actually something that can be got from
   * the modifiers?
   */
  boolean isEnumField();
}
