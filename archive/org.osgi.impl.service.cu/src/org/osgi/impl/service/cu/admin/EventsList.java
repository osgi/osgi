/*
 * $Id$
 * 
 * Copyright (c) OSGi Alliance (2000, 2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */
package org.osgi.impl.service.cu.admin;

/**
 * This class is used to hold all the events which would be 
 * asynchronously delivered.
 * 
 * @version $Revision$
 */
class EventsList {
  
  private EventData listHead;
  private EventData listTail;
  private int listSize;
  
  /**
   * @return true if the list is empty, false - otherwise
   */
  boolean isEmpty() {
    return listSize == 0;
  }
  
  /**
   * Add an {@link EventData} at the end of the list.
   *
   * @param data event data to be added
   */
  void addLast(EventData data) {
    if (listTail == null) {
      listHead = listTail = data;
      
      listSize = 1;
    } else {
      listTail.next = data;
      listTail = data;
      
      ++listSize;
    }
  }
  
  
  /**
   * Get and remove the first event's data.
   * 
   * @return first events data
   */
  EventData removeFirst() {
    EventData result = listHead;
    
    if (listHead != null) {
      listHead = listHead.next;
      
      --listSize;
      
      if (listTail == result) {
        listTail = null;
      }
      
      result.next = null;
    }   
    
    return result;  
  }
  
  /**
   * @return number of elements in the list
   */
  int getSize() {
    return listSize;
  }
  
}