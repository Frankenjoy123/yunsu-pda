package com.yunsu.network;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;

import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.yunsu.exception.LocalGeneralException;
import com.yunsu.exception.NetworkNotAvailableException;
import com.yunsu.exception.ServerAuthException;
import com.yunsu.exception.ServerGeneralException;

import com.yunsu.util.Constants;

public class RestClient {
    private ArrayList<NameValuePair> params;
    private ArrayList<NameValuePair> headers;
    private String url;
    private int responseCode;
    private String message;
    private String response;
    private Boolean isJsonContent;
    private Boolean forceToFresh = false;
    private Boolean filePost = false;

    public boolean isFilePost() {
        return filePost;
    }

    public void setIsFilePost(Boolean value) {
        filePost = value;
    }

    public void SetIsJsonContent(Boolean value) {
        isJsonContent = value;
    }

    public void setForceToRefresh(Boolean value) {
        forceToFresh = value;
    }

    public String getResponse() {
        return response;
    }

    public String getErrorMessage() {
        return message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public RestClient(String url) {

        this.url = url;
        params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
    }

    public void AddParam(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
    }

    public void AddHeader(String name, String value) {
        headers.add(new BasicNameValuePair(name, value));

    }

    public JSONObject Execute(RequestMethod method) throws Exception {
        JSONObject result = null;
        HttpUriRequest request = null;

        NetworkManager networkManager = NetworkManager.getInstance();
        if (!networkManager.isNetworkConnected())
            throw new NetworkNotAvailableException(networkManager.getNoNetworkErrorMessage());
        switch (method) {
            case GET: {

                // add parameters
                String combinedParams = "";
                if (!params.isEmpty()) {
                    combinedParams += "?";
                    for (NameValuePair p : params) {
                        String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
                        if (combinedParams.length() > 1) {
                            combinedParams += "&" + paramString;
                        } else {
                            combinedParams += paramString;
                        }
                    }
                }

                request = new HttpGet(url + combinedParams);
                // add headers
                for (NameValuePair h : headers) {
                    request.addHeader(h.getName(), h.getValue());
                }

                break;
            }
            case POST: {
                HttpPost httpPost = new HttpPost(url);
                // add headers
                for (NameValuePair h : headers) {
                    httpPost.addHeader(h.getName(), h.getValue());
                }
                if (!params.isEmpty()) {
                    if (isJsonContent) {
                        httpPost.setEntity(new StringEntity(params.get(0).getValue(), HTTP.UTF_8));
                        Log.e("TODO", params.get(0).getValue());
                    } else if (filePost) {
                        String sFilePath = params.get(0).getValue();
//                         httpPost.setEntity(new FileEntity(new File(sFilePath), "binary/octet-stream"));
                        httpPost.setEntity(new FileEntity(new File(sFilePath), "text/plain; charset=\"UTF-8\""));
//                        File file = new File(sFilePath);
//                        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//                        FileBody fileBody = new FileBody(file);
//                        entity.addPart("file", fileBody);
//                        httpPost.setEntity(entity);
//                        httpPost.setHeader(entity.getContentType());

                    } else {
                        httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    }
                }

                request = httpPost;

                break;
            }
            case PUT: {
                HttpPut httpPut = new HttpPut(url);
                for (NameValuePair h : headers) {
                    httpPut.addHeader(h.getName(), h.getValue());
                }
                if (!params.isEmpty()) {
                    if (isJsonContent) {
                        httpPut.setEntity(new StringEntity(params.get(0).getValue(), HTTP.UTF_8));
                    } else {
                        httpPut.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    }
                }

                request = httpPut;
                break;
            }
            case DELETE: {
                HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);
                for (NameValuePair h : headers) {
                    httpDelete.addHeader(h.getName(), h.getValue());
                }
                if (!params.isEmpty()) {
                    if (isJsonContent) {
                        httpDelete.setEntity(new StringEntity(params.get(0).getValue(), HTTP.UTF_8));
                    } else {
                        httpDelete.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    }
                }

                request = httpDelete;
                break;
            }
            case PATCH: {
                HttpPatch httpPatch = new HttpPatch(url);
                for (NameValuePair h : headers) {
                    httpPatch.addHeader(h.getName(), h.getValue());
                }
                if (!params.isEmpty()) {
                    if (isJsonContent) {
                        httpPatch.setEntity(new StringEntity(params.get(0).getValue(), HTTP.UTF_8));
                    } else {
                        httpPatch.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    }
                }

                request = httpPatch;

                break;
            }

        }

        result = executeRequest(request);

        if (result == null)
            throw new ServerGeneralException("Invalid data", 404);

        return result;
    }

    private JSONObject executeRequest(HttpUriRequest request) throws Exception {
        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
        // Set the default socket timeout in milliseconds which is the timeout
        // for waiting for data.
        String requestMethod = request.getMethod();
        if (requestMethod == "GET") {
            HttpConnectionParams.setSoTimeout(httpParameters, 20000);
            if (!forceToFresh) {
                setGetRequestLastModified(request);
            }
        } else {
            HttpConnectionParams.setSoTimeout(httpParameters, 240000);
        }

        HttpClient httpclient = new DefaultHttpClient(httpParameters);
        try {
            HttpResponse response = httpclient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            String reason = response.getStatusLine().getReasonPhrase();
            if (statusCode == HttpStatus.SC_NOT_MODIFIED) {
                return null;
            }
            if (statusCode < 300) {
                JSONObject jsonObject = new JSONObject();

                if (response.getEntity() == null) {
                    // no content
                    return jsonObject;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
                        "UTF-8"));

                StringBuilder builder = new StringBuilder();
                for (String line = null; (line = reader.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }
                // no content
                if (builder.length() == 0) {
                    return jsonObject;
                }

                JSONTokener tokener = new JSONTokener(builder.toString());
                Object value = tokener.nextValue();


                if (value instanceof JSONArray) {
                    jsonObject.put("array", (JSONArray) value);
                } else if (value instanceof JSONObject) {
                    jsonObject = (JSONObject) value;
                }

                if (requestMethod == "GET" && response.getLastHeader("Last-Modified") != null) {
                    cacheGetRequestLastModified(request.getURI().toString(), response.getLastHeader("Last-Modified")
                            .getValue());
                }
                return jsonObject;

            } else {

                switch (statusCode) {
                    case HttpStatus.SC_UNAUTHORIZED:
                        String path = request.getURI().getPath();
//                        if (path != null && path.contains(RegisterService.REGISTER_URL)) {
//                            throw new ServerAuthException("登录失败，请重新登录", false);
//                        } else {
                            throw new ServerAuthException(reason, true);
//                        }

                    case HttpStatus.SC_FORBIDDEN:
                    case HttpStatus.SC_NOT_FOUND:
                    default:
                        Log.e("tttttttttttttttt", "ffdfdfudf" + statusCode + "  --" + request.getURI().toString());
                        throw new ServerGeneralException("服务器发生了小状态，等等再试", statusCode);
                }

            }
        } catch (Exception e) {
            Log.w("SendRequest", "Error " + e.getMessage() + " while send request with " + url);
            Log.w("StackTrace", "Error " + e.getStackTrace());
            if (e instanceof ServerGeneralException || e instanceof ServerAuthException) {
                throw e;
            } else {
                throw new LocalGeneralException("请求失败");
            }
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
    }

    private void cacheGetRequestLastModified(String url, String lastModified) {
        CacheService.getCacheServiceInstance().refreshCache(url + Constants.Request.LastModified, lastModified);
    }

    private void setGetRequestLastModified(HttpUriRequest request) {
        CacheService cacheService = CacheService.getCacheServiceInstance();
        String lastModified = cacheService.getObjectFromCache(request.getURI().toString()
                + Constants.Request.LastModified, true);
        if (lastModified != null) {
            request.setHeader(Constants.Request.LastModified, lastModified);
        }
    }

    public enum RequestMethod {
        GET, POST, PUT, DELETE, PATCH
    }

}
