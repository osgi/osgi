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

public abstract class Type {
  private static final boolean DEBUG = false;

  /**
   * Return the type signature (as described in the japi file spec). An exception
   * is thrown if any type parameters appear that are not on g or one of its
   * containers.
   */
  public String getTypeSig(GenericWrapper wrapper) {
    return getNonGenericTypeSig();
  }

  /**
   * Return the 1.4-compatible non-generic type signature
   */
  public abstract String getNonGenericTypeSig();

  /**
   * Strip all generic information and return a plain old type.
   */
  public abstract Type getNonGenericType();

  /**
   * Used by ClassFile to resolve TypeParams
   */
  public abstract void resolveTypeParameters();

  public static Type resolveTypeParameter(Type t) {
    if (t == null) return null;
    if (t instanceof ClassFile.UnresolvedTypeParam) 
    {
        return ((ClassFile.UnresolvedTypeParam)t).resolve();
    } 
    else 
    {
        t.resolveTypeParameters();
        return t;
    }
  }

  /**
   * Return a copy of this type with all type parameters on t bound to the value
   * of t's corresponding type argument. Type parameters on t that are unbound,
   * because t was specified as a raw type, will result in null. That opens up
   * the possibility that bind() will itself return null. If that's unacceptable,
   * consider using bindWithFallback() instead.
   */
  public abstract Type bind(ClassType t);

  /**
   * Return the result of bind(t), except that in the case of an unbound type
   * parameter (where bind() would return null), return the result of binding the
   * primary constraint instead (which is a ClassType and cannot itself result in
   * null when bound). (The implementation in Type throws if bind() returns null;
   * this method is overridden in TypeParam which is the only subclass that
   * actually returns null from bind()).
   */
  public Type bindWithFallback(ClassType t) {
    Type result = bind(t);
    if (result == null) throw new NullPointerException("bind() resulted in null, unhandled");
    return result;
  }

  /**
   * Construct a type based on a non-generic signature string, eg Z, [[I, Ljava/lang/String;.
   */
  public static Type fromNonGenericSig(String sig) {
    if (sig.length() == 1)
      return PrimitiveType.fromSig(sig.charAt(0));

    switch (sig.charAt(0)) 
    {
      case '[':
        return new ArrayType(fromNonGenericSig(sig.substring(1)));
      case 'L':
        return new ClassType(sig.substring(1, sig.length() - 1).replace('/', '.'));
      default:
        throw new RuntimeException("Illegal type: " + sig);
    }
  }

  // From here on down is debugging stuff. The toString method checks to see whether it's calling
  // itself more than 15 times nested, in which case there's probably a circular data structure.
  // That might be okay - class Enum<T extends Enum<T>> is an example - but you don't want a
  // StackOverflowException trying to toString() it. toString() also returns "" if debugging is
  // disabled. Subclasses must override toStringImpl instead of toString().
  private static int nest;
  public final String toString() {
    // toString on these things is only used for debugging; shorting out the code otherwise
    // avoids running redundant code calculating the parameter to the debug method.
    if (!DEBUG) return "";
    nest++;
    try {
      if (nest > 15) return "LOOOOP";
      else return toStringImpl();
    } finally {
      nest--;
    }
  }
  public String toStringImpl() {
    return super.toString();
  }

  // You can wrap a method in "debugStart(...); try { ... } finally {debugEnd();}" to get
  // some debugging output if debugging is enabled.
  private static String indent = "";
  protected void debugStart(String label, String msg) {
    if (DEBUG) {
      System.err.println(indent + label + ": " + this + " - " + msg);
      indent += "+ ";
    }
  }
  protected void debugEnd() {
    if (DEBUG) {
      indent = indent.substring(2);
    }
  }
}
