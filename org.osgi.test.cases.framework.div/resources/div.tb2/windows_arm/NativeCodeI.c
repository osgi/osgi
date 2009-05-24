/**
 * Copyright (c) 2000 Ericsson Radio Systems AB. All Rights Reserved.
 * 
 * Ericsson Radio Systems AB grants Open Services Gateway Initiative (OSGi)
 * an irrevocable, perpetual, non-exclusive, worldwide, paid-up right and
 * license to reproduce, display, perform, prepare and have prepared
 * derivative works based upon and distribute and sublicense this material
 * and derivative works thereof as set out in the OSGi MEMBER AGREEMENT as
 * of January 24 2000, for use in accordance with Section 2.2 of the BY-LAWS
 * of the OSGi MEMBER AGREEMENT.
 */

#include <jni.h>
#include "../NativeCode.h"

JNIEXPORT void JNICALL Java_org_osgi_test_cases_div_tb2_NativeCode_count
  (JNIEnv *e, jobject o, jint c)
{
  int i;

  for(i=0;i<c;i++);
}

