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
import java.util.Arrays;

class BoundMemberSet {
  private HashMap fields = new HashMap();
  private HashMap calls = new HashMap();

  public BoundField[] getFields() {
    BoundField[] result = new BoundField[fields.size()];
    fields.values().toArray(result);
    Arrays.sort(result);
    return result;
  }
  public BoundCall[] getCalls() {
    BoundCall[] result = new BoundCall[callsSize()];
    int idx = 0;
    for (Iterator i = calls.values().iterator(); i.hasNext(); ) {
      BoundCallSet bcs = (BoundCallSet) i.next();
      for (Iterator j = bcs.iterator(); j.hasNext(); ) {
        BoundCall call = (BoundCall) j.next();
        result[idx++] = call;
      }
    }
    Arrays.sort(result);
    return result;
  }

  public void bindAndAdd(BoundField field, ClassType ctype) {
    fields.put(field.getName(), field.bind(ctype));
  }
  public void bindAndAdd(BoundCall call, ClassType ctype) {
    String sig = call.getNonGenericSig();
    BoundCall bcall = call.bind(ctype);
    if (!sig.equals(bcall.getNonGenericSig())) {
      add(call.bind14());
    }
    add(bcall);
  }
  public void bindAndAddAll(BoundMemberSet set, ClassType ctype) {
    BoundField[] fields = set.getFields();
    BoundCall[] calls = set.getCalls();
    for (int i = 0; i < fields.length; i++) {
      bindAndAdd(fields[i], ctype);
    }
    for (int i = 0; i < calls.length; i++) {
      bindAndAdd(calls[i], ctype);
    }
  }

  private void add(BoundCall call) {
    String sig = call.getNonGenericSig();
    BoundCallSet bcs = (BoundCallSet) calls.get(sig);
    if (bcs == null) {
      bcs = new BoundCallSet();
      calls.put(sig, bcs);
    }
    bcs.add(call);
  }
  private int callsSize() {
    int result = 0;
    for (Iterator i = calls.values().iterator(); i.hasNext(); ) {
      BoundCallSet bcs = (BoundCallSet) i.next();
      result += bcs.size();
    }
    return result;
  }
}