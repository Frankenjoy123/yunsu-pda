package com.yunsoo.network;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * Created by Dake Wang on 2015/6/4.
 */
public class HttpPatch extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "PATCH";

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

    public HttpPatch(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    public HttpPatch(final URI uri) {
        super();
        setURI(uri);
    }

    public HttpPatch() {
        super();
    }
}
