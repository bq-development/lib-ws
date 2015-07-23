package io.corbel.lib.ws.digest;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Alexander De Leon
 * 
 */
public class DefaultDigester implements Digester {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultDigester.class);

	private final String algorithm;
	private final String encoding;

	DefaultDigester(String algorithm) {
		this(algorithm, Charset.defaultCharset().toString());
	}

	DefaultDigester(String algorithm, String encoding) {
		this.algorithm = algorithm;
		this.encoding = encoding;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public String getEncoding() {
		return encoding;
	}

	@Override
	public String digest(String text) {
        MessageDigest md=null;
		try {
			String textToDigest = text;
			md = MessageDigest.getInstance(algorithm);
			md.update(textToDigest.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			// This can not occur
			LOG.error(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			// This can not occur
			LOG.error(e.getMessage());
		}

		return Hex.encodeHexString(md.digest());
	}
}
