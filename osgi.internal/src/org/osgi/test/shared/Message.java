/** 
 * OSGi Test Suite Implementation. OSGi Confidential.
 * (C) Copyright Ericsson Radio Systems AB. 2000.
 * This source code is owned by Ericsson Radio Systems AB, and is being distributed to OSGi 
 * MEMBERS as MEMBER LICENSED MATERIALS under the terms of section 3.2 of the OSGi MEMBER AGREEMENT. 
 */
package org.osgi.test.shared;

import java.io.*;

/**
 * A message class with a type and content used to exchange messages via the
 * Link class. The message class is used for notifications and for
 * request/response type of exchanges.
 */
public class Message implements Serializable {
	public final static long	serialVersionUID	= 1;
	int							_type;						// Type code
	Object						_content;					// payload
	int							_requestID			= 0;	// ID of
	// request
	int							_responseID			= 0;	// ID of
	// request in
	// response
	transient Link				_link;						// Pointer to
	// owning link
	// class
	transient Message			_reply;					// Associated reply
	// when request
	transient Thread			_thread;					// Thread waiting
	// for us when
	// canceled.
	static int					_nextID				= 97;	// Transaction
	// id counter
	transient Message			_source;					// Source message

	/**
	 * Default constructor.
	 * 
	 * @param type type of message
	 * @param data payload
	 */
	Message(int type, Object data) {
		_type = type;
		_content = data;
		_requestID = _nextID++;
	}

	public Message(byte[] buffer) throws Exception {
		try {
			//System.out.println( "<init> " + buffer.length);
			if (buffer.length < 17)
				throw new IllegalArgumentException("byte less than 17 bytes "
						+ buffer.length);
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(
					buffer, 0, 12));
			_type = in.readInt();
			_requestID = in.readInt();
			_responseID = in.readInt();
			in.close();
			ObjectInputStream oin = new ObjectInputStream(
					new ByteArrayInputStream(buffer, 12, buffer.length - 12));
			_content = oin.readObject();
			oin.close();
		}
		catch (Exception e) {
		}
	}

	public byte[] asBytes() {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(bout);
			out.writeInt(_type);
			out.writeInt(_requestID);
			out.writeInt(_responseID);
			ObjectOutputStream oout = new ObjectOutputStream(out);
			oout.writeObject(_content);
			oout.close();
			out.close();
			bout.close();
			byte data[] = bout.toByteArray();
			//System.out.println( "asBytes " + data.length );
			return data;
		}
		catch (IOException e) {
			e.printStackTrace(); // memory stream
		}
		return null;
	}

	/**
	 * Get the pay load of the message
	 */
	public Object getContent() {
		return _content;
	}

	/**
	 * Get the type of the message
	 */
	public int getType() {
		return _type;
	}

	/**
	 * Reply to this message. This will create a new message send to our link
	 * with the responseID set to our transaction id.
	 */
	public void reply(int what, Object data) throws IOException {
		Message reply = new Message(what, data);
		reply._source = this;
		reply._responseID = _requestID;
		_link.send(reply);
	}

	/**
	 * Get a response to this message.
	 * 
	 * When a response arrives, we will get notified.
	 * 
	 * @param timeout # of millis to wait
	 */
	synchronized Message getResponse(int timeout) {
		if (_reply == null) {
			try {
				wait(timeout);
			}
			catch (InterruptedException e) {
			}
		}
		return _reply;
	}

	/**
	 * If the link goes down, this can be used to cancel waiting threads.
	 */
	public synchronized void cancel() {
		if (_thread != null)
			_thread.interrupt();
	}

	/**
	 * When the Link class discovers a reply it will locate this message and set
	 * the reply. We will then notify the waiter.
	 */
	synchronized void replyReceived(Message reply) {
		_reply = reply;
		notify();
	}

	/**
	 * Set our link.
	 */
	void setLink(Link link) {
		_link = link;
	}

	public String toString() {
		return "Message(" + _type + "," + _content + ",rqid=" + _requestID
				+ ",rspid=" + _responseID + "," + _source + ")";
	}
}
