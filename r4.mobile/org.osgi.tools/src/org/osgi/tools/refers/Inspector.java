/*
 * $Header$
 * 
 * Copyright (c) The OSGi Alliance (2005). All Rights Reserved.
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

package org.osgi.tools.refers;
/* Inspector.java by Mark D. LaDue */

/* June 24, 1997 */

/* Copyright (c) 1997 Mark D. LaDue
   You may study, use, modify, and distribute this example for any purpose.
   This example is provided WITHOUT WARRANTY either expressed or implied.  */

/* This Java application analyzes the entries in the constant pool and locates
   the code arrays in a Java class file. Each entry in the constant pool
   yields the following information:

   Index	Tag	Reference(s)/Value(s)
   -----	---	---------------------

   where "Index" is its position within the class file's constant pool,
   "Tag" is the official tag number for that type of entry, and
   "Reference(s)/Value(s)" contains the constant pool information
   according to the entry's type.  (See Lindholm and Yellin's "The Java
   Virtual Machine Specification" for details.)  For each code array in
   the class file, its starting byte, its total length, and the name of
   the method in which it occurs are given.  Combining this information
   with the information yielded by the humble "javap" utility gives one
   sufficient information to hack the code arrays in Java class files. */

import java.io.*;

class Inspector {
    public static void main(String[] argv) {
        int fpointer = 8; // Where are we in the class file?
        int cp_entries = 1; // How big is the constant pool?
        int Code_entry = 1; // Where is the entry that denotes "Code"?
        int num_interfaces = 0; // How many interfaces does it use?
        int num_fields = 0; // How many fields are there?
        int num_f_attributes = 0; // How many attributes does a field have?
        int num_methods = 0; // How many methods are there?
        int num_m_attributes = 0; // How many attributes does a method have?
        int[] tags; // Tags for the constant pool entries
        int[] read_ints1; // References for some constant pool entries
        int[] read_ints2; // References for some constant pool entries
        long[] read_longs; // Values for some constant pool entries
        float[] read_floats; // Values for some constant pool entries
        double[] read_doubles; // Values for some constant pool entries
        StringBuffer[] read_strings; // Strings in some constant pool entries
        int[] method_index;
        long[] code_start;
        long[] code_length;

// How on earth do I use this thing?
        if (argv.length != 1) {
            System.out.println("Try \"java Inspector class_file.class\"");
            System.exit(1);
        }

// Start by opening the file for reading
        try {
            RandomAccessFile victim = new RandomAccessFile(argv[0], "r");

// Skip the magic number and versions and start looking at the class file
            victim.seek(fpointer);

// Determine how many entries there are in the constant pool
            cp_entries = victim.readUnsignedShort();
            fpointer += 2;

// Set up the arrays of useful information about the constant pool entries
            tags = new int[cp_entries];
            read_ints1 = new int[cp_entries];
            read_ints2 = new int[cp_entries];
            read_longs = new long[cp_entries];
            read_floats = new float[cp_entries];
            read_doubles = new double[cp_entries];
            read_strings = new StringBuffer[cp_entries];

//Initialize these arrays
            for (int cnt = 0; cnt < cp_entries; cnt++) {
                tags[cnt] = -1;
                read_ints1[cnt] = -1;
                read_ints2[cnt] = -1;
                read_longs[cnt] = -1;
                read_floats[cnt] = -1;
                read_doubles[cnt] = -1;
                read_strings[cnt] = new StringBuffer();
             }

// Look at each entry in the constant pool and save the information in it
            for (int i = 1; i < cp_entries; i++) {
                tags[i] = victim.readUnsignedByte();
                fpointer++;
                int skipper = 0;
                int start = 0;
                int test_int = 0;
                switch (tags[i]) {
                    case 3: read_ints1[i] = victim.readInt();
                            fpointer += 4;
                            break;
                    case 4: read_floats[i] = victim.readFloat();
                            fpointer += 4;
                            break;
                    case 5: read_longs[i] = victim.readLong();
                            fpointer += 8;
                            i++;
                            break;
                    case 6: read_doubles[i] = victim.readDouble();
                            fpointer += 8;
                            i++;
                            break;
                    case 7:
                    case 8: read_ints1[i] = victim.readUnsignedShort();
                            fpointer += 2;
                            break;
                    case 9:
                    case 10:
                    case 11:
                    case 12: read_ints1[i] = victim.readUnsignedShort();
                             fpointer += 2;
                             victim.seek(fpointer);
                             read_ints2[i] = victim.readUnsignedShort();
                             fpointer += 2;
                             break;

// This is the critical case - determine an entry in the constant pool where
// the string "Code" is found so we can later identify the code attributes
// for the class's methods
                    case 1: skipper = victim.readUnsignedShort();
                            start = fpointer;
                            fpointer += 2;
                            victim.seek(fpointer);
                            for (int cnt = 0; cnt < skipper; cnt++) {
                                int next = victim.readUnsignedByte();
                                switch (next) {
                                    case 9: read_strings[i].append("\\" + "t");
                                            break;
                                    case 10: read_strings[i].append("\\" + "n");
                                             break;
                                    case 11: read_strings[i].append("\\" + "v");
                                             break;
                                    case 13: read_strings[i].append("\\" + "r");
                                             break;
                                    default: read_strings[i].append((char)next);
                                             break;
                                }
                                victim.seek(++fpointer);
                            }
                            victim.seek(start);
                            if (skipper == 4) {
                                fpointer = start + 2;
                                victim.seek(fpointer);
                                test_int = victim.readInt();
                                if (test_int == 1131373669) {Code_entry = i;}
                                fpointer = fpointer + skipper;
                            }
                            else {fpointer = start + skipper + 2;}
                            break;
                }
                victim.seek(fpointer); 
            }

// Skip ahead and see how many interfaces the class implements
            fpointer += 6;
            victim.seek(fpointer);
            num_interfaces = victim.readUnsignedShort();
// Bypass the interface information
            fpointer = fpointer + 2*(num_interfaces) + 2;
            victim.seek(fpointer);
// Determine the number of fields
            num_fields = victim.readUnsignedShort();
// Bypass the field information
            fpointer += 2;
            victim.seek(fpointer);
            for (int j=0; j<num_fields; j++) {
                fpointer += 6;
                victim.seek(fpointer);
                num_f_attributes = victim.readUnsignedShort();
                fpointer = fpointer + 8*(num_f_attributes) + 2;
                victim.seek(fpointer);
            }
// Determine the number of methods
            num_methods = victim.readUnsignedShort();
            fpointer += 2;

// Set up the arrays of information about the class's methods
            method_index = new int[num_methods];
            code_start = new long[num_methods];
            code_length = new long[num_methods];

//Initialize these arrays
            for (int cnt = 0; cnt < num_methods; cnt++) {
                method_index[cnt] = -1;
                code_start[cnt] = -1;
                code_length[cnt] = -1;
             }

// For each method determine the index of its name and locate its code array
            for (int k=0; k<num_methods; k++) {
                fpointer += 2;
                victim.seek(fpointer);
                method_index[k] = victim.readUnsignedShort();
                fpointer += 4;
                victim.seek(fpointer);

// Determine the number of attributes for the method
                num_m_attributes = victim.readUnsignedShort();
                fpointer += 2;

// Test each attribute to see if it's code
                for (int m=0; m<num_m_attributes; m++) {
                    int Code_test = victim.readUnsignedShort();
                    fpointer += 2;

// If it is, record the location and length of the code array
                    if (Code_test == Code_entry){
                        int att_length = victim.readInt();
                        int next_method = fpointer + att_length + 4;
                        fpointer += 8;
                        victim.seek(fpointer);
                        code_length[k] = victim.readInt();
                        code_start[k] = fpointer + 5;
                        fpointer = next_method;
                        victim.seek(fpointer);
                    }

// Otherwise just skip it and go on to the next method
                    else {
                        fpointer = fpointer + victim.readInt() + 4;
                        victim.seek(fpointer);
                    }
                }
            }

// Print the information about the Constant Pool
            System.out.println("There are " + (cp_entries - 1) + " + 1 entries in the Constant Pool:\n");

            System.out.println("Index\t" + "Tag\t" + "Reference(s)/Value(s)\t");
            System.out.println("-----\t" + "---\t" + "---------------------\t");
            for (int i = 0; i < cp_entries; i++) {
                switch (tags[i]) {
                    case 1: System.out.println(i + "\t" + tags[i] + "\t" + read_strings[i].toString());
                            break;
                    case 3: System.out.println(i + "\t" + tags[i] + "\t" + read_ints1[i]);
                            break;
                    case 4: System.out.println(i + "\t" + tags[i] + "\t" + read_floats[i]);
                            break;
                    case 5: System.out.println(i + "\t" + tags[i] + "\t" + read_longs[i]);
                            break;
                    case 6: System.out.println(i + "\t" + tags[i] + "\t" + read_doubles[i]);
                            break;
                    case 7:
                    case 8: System.out.println(i + "\t" + tags[i] + "\t" + read_ints1[i]);
                            break;
                    case 9:
                    case 10:
                    case 11:
                    case 12: System.out.println(i + "\t" + tags[i] + "\t" + read_ints1[i] + " " + read_ints2[i]);
                            break;
                }
            }
            System.out.println();

// Print the information about the methods
            System.out.println("There are " + num_methods + " methods:\n");
            for (int j = 0; j < num_methods; j++) {
                System.out.println("Code array in method " + read_strings[method_index[j]].toString() + " of length " + code_length[j] + " starting at byte " + code_start[j] + ".");
            }
            System.out.println();

// All the changes are made, so close the file and move along
            victim.close();
        } catch (IOException ioe) {}
    }
}

