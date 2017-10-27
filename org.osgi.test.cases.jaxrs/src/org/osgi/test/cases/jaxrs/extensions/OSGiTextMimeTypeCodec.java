package org.osgi.test.cases.jaxrs.extensions;

import static javax.ws.rs.core.MediaType.CHARSET_PARAMETER;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

@Consumes("osgi/text")
@Produces("osgi/text")
public class OSGiTextMimeTypeCodec
		implements MessageBodyReader<String>, MessageBodyWriter<String> {

	@Override
	public long getSize(String arg0, Class< ? > arg1, Type arg2,
			Annotation[] arg3,
			MediaType arg4) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class< ? > cls, Type arg1, Annotation[] arg2,
			MediaType media) {
		return cls == String.class && media.getType().equals("osgi")
				&& media.getSubtype().equals("text");
	}

	@Override
	public void writeTo(String s, Class< ? > arg1, Type arg2, Annotation[] arg3,
			MediaType mt, MultivaluedMap<String,Object> arg5,
			OutputStream os) throws IOException, WebApplicationException {

		try (OutputStreamWriter osw = new OutputStreamWriter(os,
				mt.getParameters().getOrDefault(CHARSET_PARAMETER, "UTF-8"))) {
			osw.write("OSGi Write: ");
			osw.write(s);
		}
	}

	@Override
	public boolean isReadable(Class< ? > cls, Type arg1, Annotation[] arg2,
			MediaType media) {
		return cls == String.class && media.getType().equals("osgi")
				&& media.getSubtype().equals("text");
	}

	@Override
	public String readFrom(Class<String> arg0, Type arg1, Annotation[] arg2,
			MediaType mt, MultivaluedMap<String,String> arg4,
			InputStream in) throws IOException, WebApplicationException {
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(in, mt.getParameters()
						.getOrDefault(CHARSET_PARAMETER, "UTF-8")))) {
			return "OSGi Read: " + br.readLine();
		}
	}

}
