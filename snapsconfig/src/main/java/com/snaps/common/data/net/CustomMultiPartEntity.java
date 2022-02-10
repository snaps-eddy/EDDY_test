package com.snaps.common.data.net;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

public class CustomMultiPartEntity extends MultipartEntity {

	private ProgressListener listener;
	private static long total = 0;

	public CustomMultiPartEntity(final ProgressListener listener) {
		super();
		this.listener = listener;
	}

	public CustomMultiPartEntity(final HttpMultipartMode mode, final ProgressListener listener) {
		super(mode);
		this.listener = listener;
	}

	public CustomMultiPartEntity(HttpMultipartMode mode, final String boundary, final Charset charset, final ProgressListener listener) {
		super(mode, boundary, charset);
		this.listener = listener;
	}

	public void setTotalCount(long value) {
		total = value;
	}

	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream, this.listener));
	}

	public static interface ProgressListener {
		void transferred(long num, long total);
	}

	public static class CountingOutputStream extends FilterOutputStream {

		private final ProgressListener listener;
		private long transferred;

		public CountingOutputStream(final OutputStream out, final ProgressListener listener) {
			super(out);
			this.listener = listener;
			this.transferred = 0;
		}

		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
			this.transferred += len;
			if (this.listener != null)
				this.listener.transferred(this.transferred, total);
		}

		public void write(int b) throws IOException {
			out.write(b);
			this.transferred++;
			if (this.listener != null)
				this.listener.transferred(this.transferred, total);
		}
	}
}
