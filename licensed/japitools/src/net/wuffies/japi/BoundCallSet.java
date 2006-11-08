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

import java.util.HashMap;
import java.util.Iterator;

// This is sort of a map from erased-return-type to BoundCall, but it maintains
// some constraints on its members, specifically:
// - Only one call can be visible15.
// - Adding a new member with the same return type overwrites the existing one.
// - Adding a new member that is visible15 causes the existing item that is
//   visible15 to be replaced with a bind14'd version, unless that existing
//   item was !visible14 in which case it's removed instead.
// - If the erased signature of the method (*excluding* the return type) is
//   different than for the members already present, an exception is thrown.
class BoundCallSet {
  private HashMap calls = new HashMap();
  private BoundCall visible15call;

  public void add(BoundCall call) {
    if (call.isVisible15()) {
      if (visible15call != null) {
        calls.remove(visible15call.getReturnType().getNonGenericTypeSig());
        if (visible15call.isVisible14()) {
          add(visible15call.bind14());
        }
      }
      visible15call = call;
    }
    if (call.getReturnType() != null) {
      calls.put(call.getReturnType().getNonGenericTypeSig(), call);
    } else {
      calls.put("", call);
    }
  }
  public Iterator iterator() {
    return calls.values().iterator();
  }
  public int size() {
    return calls.size();
  }
}