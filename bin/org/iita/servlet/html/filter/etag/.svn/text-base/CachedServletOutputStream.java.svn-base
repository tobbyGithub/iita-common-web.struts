/**
 * iita-common-web.struts Sep 14, 2009
 */
package org.iita.servlet.html.filter.etag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

/**
 * @author mobreza
 *
 */
public class CachedServletOutputStream extends ServletOutputStream {

	private ByteArrayOutputStream cache;

	/**
	 * @param cache
	 */
	public CachedServletOutputStream(ByteArrayOutputStream cache) {
		this.cache=cache;
	}

	/**
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		this.cache.write(b);
	}
	
	/**
	 * @see java.io.OutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] b) throws IOException {
		this.cache.write(b);
	}
	
	/**
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		this.cache.write(b, off, len);
	}
	
	/**
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
		this.cache.flush();
	}
}
