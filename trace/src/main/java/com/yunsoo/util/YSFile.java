package com.yunsoo.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by:   Lijian
 * Created on:   2016-03-11
 * Descriptions:
 */
public class YSFile {
    public static final String EXT_PKS = "PKS"; //product keys file
    public static final String EXT_TF = "TF"; //task file

    private static final String VER_1_0 = "1.0";
    private static final String DEFAULT_VER = VER_1_0;
    private static final Pattern REGEX_START_LINE = Pattern.compile("^YS\\[\\s*[A-Z]+\\s*:\\s*[^:,\\s\\]]+(\\s*,\\s*[A-Z]+\\s*:\\s*[^:,\\s\\]]+)+\\s*\\]");
    private static final Pattern REGEX_HEADER_LINE = Pattern.compile("^[^:\\s]+\\s*:\\s*[^\\r\\n]+");

    private String EXT;

    private String VER;

    private List<String> comments;

    private Map<String, String> headers;

    private byte[] content;


    public String getEXT() {
        return EXT;
    }

    public String getVER() {
        return VER;
    }

    public List<String> getComments() {
        return comments;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content == null ? new byte[0] : content;
    }

    public YSFile(String EXT) {
        assert EXT != null : "EXT must not be null";
        this.EXT = EXT;
        this.VER = DEFAULT_VER;
        this.comments = new ArrayList<>();
        this.headers = new HashMap<>();
        this.setContent(new byte[0]);
    }

    private YSFile(String EXT, String VER, byte[] content) {
        this.EXT = EXT;
        this.VER = VER;
        this.comments = new ArrayList<>();
        this.headers = new HashMap<>();
        this.setContent(content);
    }

    public void addComments(String comments) {
        if (comments != null) {
            if (comments.contains("\r\n")) {
                Collections.addAll(this.comments, comments.split("\r\n"));
            } else {
                this.comments.add(comments);
            }
        }
    }

    public void putHeader(String name, String value) {
        if (name != null) {
            this.headers.put(name, value);
        }
    }

    public String getHeader(String name) {
        return this.headers.get(name);
    }


    public byte[] toBytes() {
        byte[] header = headerLines();
        byte[] comments = commentsLines();
        byte[] sl = startLine(header);
        byte[] result = new byte[sl.length + comments.length + header.length + content.length];
        System.arraycopy(sl, 0, result, 0, sl.length);
        System.arraycopy(comments, 0, result, sl.length, comments.length);
        System.arraycopy(header, 0, result, sl.length + comments.length, header.length);
        System.arraycopy(content, 0, result, sl.length + comments.length + header.length, content.length);
        return result;
    }

    @Override
    public String toString() {
        return new String(this.toBytes(),Charset.forName("UTF-8"));
    }

    private byte[] startLine(byte[] header) {
        String hash = hash(header);
        return String.format("YS[EXT: %s, VER: %s, HASH: %s]\r\n", EXT, VER, hash).getBytes(Charset.forName("UTF-8"));
    }

    private byte[] commentsLines() {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<comments.size();i++){
            sb.append('#').append(comments.get(i)).append("\r\n");
        }
        return sb.toString().getBytes(Charset.forName("UTF-8"));
    }

    private byte[] headerLines() {
        StringBuilder sb = new StringBuilder();
        String[] names = headers.keySet().toArray(new String[headers.size()]);
        Arrays.sort(names);
        for (String name : names) {
            if (name != null) {
                sb.append(escapeHeaderName(name)).append(": ").append(escapeHeaderValue(headers.get(name))).append("\r\n");
            }
        }
        sb.append("\r\n"); //empty line at the end
        return sb.toString().getBytes(Charset.forName("UTF-8"));
    }

    private String hash(byte[] header) {
        byte[] src = new byte[header.length + content.length];
        System.arraycopy(header, 0, src, 0, header.length);
        System.arraycopy(content, 0, src, header.length, content.length);
        return HexStringUtils.encode(HashUtils.sha1(src));
    }

    private boolean validateHash(String hash) {
        String realHash = hash(headerLines());
        return realHash.equalsIgnoreCase(hash);
    }

    public static YSFile read(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new RuntimeException("inputStream invalid");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        out.flush();
        return YSFile.read(out.toByteArray());
    }

    public static YSFile read(byte[] data) {
        if (data == null || data.length < 15 || data[0] != 'Y' || data[1] != 'S') {
            throw new RuntimeException("data invalid");
        }
        byte[][] temp = splitHeaderAndBody(data);
        String header = new String(temp[0], Charset.forName("UTF-8"));
        String[] headerArray = header.split("\r\n");

        StartLine startLine = parseStartLine(headerArray[0]);
        if (startLine == null) {
            throw new RuntimeException("data invalid, [invalid start line]");
        }
        YSFile ysFile = new YSFile(startLine.getEXT(), startLine.getVER(), temp[1]);
        for (int i = 1; i < headerArray.length; i++) {
            if (headerArray[i].length() > 0) {
                if (headerArray[i].charAt(0) == '#') {
                    ysFile.addComments(headerArray[i].substring(1));
                } else if (REGEX_HEADER_LINE.matcher(headerArray[i]).matches()) {
                    String[] kv = headerArray[i].split(": *", 2);
                    ysFile.putHeader(unescapeHeaderName(kv[0].trim()), unescapeHeaderValue(kv[1].trim()));
                } else {
                    throw new RuntimeException(String.format("data invalid, [invalid header line: \"%s\"]", headerArray[i]));
                }
            }
        }
        if (!ysFile.validateHash(startLine.getHASH())) {
            throw new RuntimeException("data invalid, [invalid hash]");
        }
        return ysFile;
    }


    private static String escapeHeaderName(String value) {
        if (value == null) {
            return null;
        }
        value = value.replace("$", "$d"); //first step
        return value.replace(":", "$c").replace(" ", "$s").replace("\r", "$r").replace("\n", "$n");
    }

    private static String unescapeHeaderName(String value) {
        if (value == null) {
            return null;
        }
        value = value.replace("$c", ":").replace("$s", " ").replace("$r", "\r").replace("$n", "\n");
        return value.replace("$d", "$"); //last step
    }

    private static String escapeHeaderValue(String value) {
        if (value == null) {
            return null;
        }
        value = value.replace("$", "$d"); //first step
        return value.replace("\r", "$r").replace("\n", "$n");
    }

    private static String unescapeHeaderValue(String value) {
        if (value == null) {
            return null;
        }
        value = value.replace("$r", "\r").replace("$n", "\n");
        return value.replace("$d", "$"); //last step
    }

    private static byte[][] splitHeaderAndBody(byte[] data) {
        int headerLength = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] == '\r' && i + 3 < data.length) {
                if (data[i + 3] != '\n') {
                    i += 2;
                    continue;
                }
                if (data[i + 2] == '\r' && data[i + 1] == '\n') {
                    //found 2 continuous crlf
                    headerLength = i + 4;
                    break;
                }
            }
        }
        if (headerLength == 0) {
            throw new RuntimeException("data invalid");
        } else {
            byte[] header = new byte[headerLength];
            byte[] body = new byte[data.length - headerLength];
            System.arraycopy(data, 0, header, 0, headerLength);
            System.arraycopy(data, headerLength, body, 0, body.length);
            return new byte[][]{header, body};
        }
    }

    private static StartLine parseStartLine(String startLine) {
        if (!REGEX_START_LINE.matcher(startLine).matches()) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        String[] values = startLine.substring(startLine.indexOf('[') + 1, startLine.indexOf(']')).split(", *");
        for (String value : values) {
            String[] kv = value.split(": *", 2);
            if (kv.length == 2) {
                kv[0] = kv[0].trim();
                kv[1] = kv[1].trim();
                if (kv[0].length() > 0) {
                    map.put(kv[0], kv[1]);
                }
            }
        }
        String ext = map.get("EXT");
        String ver = map.get("VER");
        String hash = map.get("HASH");
        if (ext != null && ext.length() > 0 && ver != null && ver.length() > 0 && hash != null && hash.length() > 0) {
            return new StartLine(ext, ver, hash);
        }
        return null;
    }

    private static class StartLine {

        private String EXT;

        private String VER;

        private String HASH;

        public String getEXT() {
            return EXT;
        }

        public String getVER() {
            return VER;
        }

        public String getHASH() {
            return HASH;
        }

        public StartLine(String EXT, String VER, String HASH) {
            this.EXT = EXT;
            this.VER = VER;
            this.HASH = HASH;
        }
    }

}