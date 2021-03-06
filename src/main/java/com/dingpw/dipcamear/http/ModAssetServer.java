/*
 * Copyright (C) 2011-2012 GUIGUI Simon, fyhertz@gmail.com
 * 
 * This file is part of Spydroid (http://code.google.com/p/spydroid-ipcamera/)
 * 
 * Spydroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */


package com.dingpw.dipcamear.http;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import org.apache.http.*;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Locale;


/**
 * 
 * Serves the content of assets/www 
 *
 */
public class ModAssetServer implements HttpRequestHandler {

	public static final String PATTERN = "*";
	
	/** The list of MIME Media Types supported by the server. */
	public static String[] mimeMediaTypes = new String[] {
		"htm",	"text/html", 
		"html",	"text/html", 
		"gif",	"image/gif",
		"jpg",	"image/jpeg",
		"png",	"image/png", 
		"js",	"text/javascript",
		"json",	"text/json",
		"css",	"text/css"
	};

	private final LightHttpServer mServer;
	private final AssetManager mAssetManager;

	public ModAssetServer(LightHttpServer server) {
		super();
		mServer = server;
		mAssetManager = mServer.context.getAssets();
	}

    @Override
	public void handle(HttpRequest request, HttpResponse response,final HttpContext context) throws HttpException, IOException {
		AbstractHttpEntity body = null;

		final String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
		if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
			throw new MethodNotSupportedException(method + " method not supported"); 
		}

		final String url = URLDecoder.decode(request.getRequestLine().getUri());
		if (request instanceof HttpEntityEnclosingRequest) {
			HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
			byte[] entityContent = EntityUtils.toByteArray(entity);
		}

		final String location = "www"+(url.equals("/")?"/index.htm":url);
		response.setStatusCode(HttpStatus.SC_OK);

		try {

			// Compares the Last-Modified date header (if present) with the If-Modified-Since date
			if (request.containsHeader("If-Modified-Since")) {
				try {
					Date date = DateUtils.parseDate(request.getHeaders("If-Modified-Since")[0].getValue());
					if (date.compareTo(mServer.date)<=0) {
						// The file has not been modified
						response.setStatusCode(HttpStatus.SC_NOT_MODIFIED);
						return;
					}
				} catch (DateParseException e) {
					e.printStackTrace();
				}
			}

			// We determine if the asset is compressed
			try {
				AssetFileDescriptor afd = mAssetManager.openFd(location);

				// The asset is not compressed
				FileInputStream fis = new FileInputStream(afd.getFileDescriptor());
				fis.skip(afd.getStartOffset());
				body = new InputStreamEntity(fis, afd.getDeclaredLength());
			} catch (FileNotFoundException e) {

				// The asset may be compressed
				// AAPT compresses assets so first we need to uncompress them to determine their length
				InputStream stream =  mAssetManager.open(location,AssetManager.ACCESS_STREAMING);
				ByteArrayOutputStream buffer = new ByteArrayOutputStream(64000);
				byte[] tmp = new byte[4096]; int length = 0;
				while ((length = stream.read(tmp)) != -1) buffer.write(tmp, 0, length);
				body = new InputStreamEntity(new ByteArrayInputStream(buffer.toByteArray()), buffer.size());
				stream.close();

			}

			body.setContentType(getMimeMediaType(url)+"; charset=UTF-8");
			response.addHeader("Last-Modified", DateUtils.formatDate(mServer.date));

		} catch (IOException e) {
			// File does not exist
			response.setStatusCode(HttpStatus.SC_NOT_FOUND);
			body = new EntityTemplate(new ContentProducer() {
				public void writeTo(final OutputStream outstream) throws IOException {
					OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8"); 
					writer.write("<html><body><h1>");
					writer.write("File ");
					writer.write("www"+url);
					writer.write(" not found");
					writer.write("</h1></body></html>");
					writer.flush();
				}
			});
			body.setContentType("text/html; charset=UTF-8");
		}

		response.setEntity(body);

	}

	private String getMimeMediaType(String fileName) {
		String extension = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
		for (int i=0;i<mimeMediaTypes.length;i+=2) {
			if (mimeMediaTypes[i].equals(extension)) 
				return mimeMediaTypes[i+1];
		}
		return mimeMediaTypes[0];
	}

}
