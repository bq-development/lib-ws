/*
 * Copyright (C) 2013 StarTIC
 */
package com.bq.oss.lib.ws.digest;

import com.google.common.hash.Hashing;
import org.apache.commons.codec.binary.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author Alexander De Leon
 * 
 */
public class DigesterFactory {

	private static final String codeFormatUTF8 = "UTF-8";
	private static final String codeFormatUTF16 = "UTF-16";

	public static Digester sha512(String secret) {
		Digester digester = new DefaultDigester("SHA-512");
		return (null != secret) ? new SignedDigester(digester, secret) : digester;
	}

	public static Digester sha512() {
		return sha512(null);
	}

	public static Digester sha384(String secret) {
		Digester digester = new DefaultDigester("SHA-384");
		return (null != secret) ? new SignedDigester(digester, secret) : digester;
	}

	public static Digester sha384() {
		return sha384(null);
	}

	public static Digester sha256(String secret) {
		Digester digester = new DefaultDigester("SHA-256");
		return (null != secret) ? new SignedDigester(digester, secret) : digester;
	}

	public static Digester sha256() {
		return sha256(null);
	}

	public static Digester sha1(String secret) {
		Digester digester = new DefaultDigester("SHA-1");
		return (null != secret) ? new SignedDigester(digester, secret) : digester;
	}

	public static Digester sha1() {
		return sha1(null);
	}

	public static Digester md5(String secret) {
		Digester digester = new DefaultDigester("MD5");
		return (null != secret) ? new SignedDigester(digester, secret) : digester;
	}

	public static Digester md5() {
		return md5(null);
	}

	public static Digester sha512Utf8(String secret) {
		Digester digester = new DefaultDigester("SHA-512", codeFormatUTF8);
		return (null != secret) ? new SignedDigester(digester, secret) : digester;
	}

	public static Digester sha512Utf8() {
		return sha512Utf8(null);
	}

	public static Digester sha384Utf8(String secret) {
		Digester digester = new DefaultDigester("SHA-384", codeFormatUTF8);
		return (null != secret) ? new SignedDigester(digester, secret) : digester;
	}

	public static Digester sha384Utf8() {
		return sha384Utf8(null);
	}

	public static Digester sha256Utf8(String secret) {
		Digester digester = new DefaultDigester("SHA-256", codeFormatUTF8);
		return (null != secret) ? new SignedDigester(digester, secret) : digester;
	}

	public static Digester sha256Utf8() {
		return sha256Utf8(null);
	}

	public static Digester sha1Utf8(String secret) {
		Digester digester = new DefaultDigester("SHA-1", codeFormatUTF8);
		return (null != secret) ? new SignedDigester(digester, secret) : digester;
	}

	public static Digester sha1Utf8() {
		return sha1Utf8(null);
	}

	public static Digester sha512Utf16(String secret) {
		Digester digester = new DefaultDigester("SHA-512", codeFormatUTF16);
		return (null != secret) ? new SignedDigester(digester, secret) : digester;
	}

	public static Digester sha512Utf16() {
		return sha512Utf16(null);
	}

	public static Digester sha384Utf16(String secret) {
		Digester digester = new DefaultDigester("SHA-384", codeFormatUTF16);
		return (null != secret) ? new SignedDigester(digester, secret) : digester;
	}

	public static Digester sha384Utf16() {
		return sha384Utf16(null);
	}

	public static Digester sha256Utf16(String secret) {
		Digester digester = new DefaultDigester("SHA-256", codeFormatUTF16);
		return (null != secret) ? new SignedDigester(digester, secret) : digester;
	}

	public static Digester sha256Utf16() {
		return sha256Utf16(null);
	}

	public static Digester sha1Utf16(String secret) {
		Digester digester = new DefaultDigester("SHA-1", codeFormatUTF16);
		return (null != secret) ? new SignedDigester(digester, secret) : digester;
	}

	public static Digester sha1Utf16() {
		return sha1Utf16(null);
	}

	public static Digester md5Utf16(String secret) {
		Digester digester = new DefaultDigester("MD5", codeFormatUTF16);
		return (null != secret) ? new SignedDigester(digester, secret) : digester;
	}

	public static Digester md5Utf16() {
		return md5Utf16(null);
	}

	public static Digester murmur3_32(String secret) {
		Digester digester = new GuavaHashFunctionDigester(Hashing.murmur3_32());
		return (null != secret) ? new SignedDigester(digester, secret) : digester;
	}

	public static Digester murmur3_32() {
		return murmur3_32(null);
	}

	public static Digester murmur3_128(String secret) {
		Digester digester = new GuavaHashFunctionDigester(Hashing.murmur3_128());
		return (null != secret) ? new SignedDigester(digester, secret) : digester;
	}

	public static Digester murmur3_128() {
		return murmur3_128(null);
	}

	public Digester md5Utf8(String secret) {
		Digester digester = new DefaultDigester("MD5", codeFormatUTF8);
		return (null != secret) ? new SignedDigester(digester, secret) : digester;
	}

	public Digester md5Utf8() {
		return md5Utf8(null);
	}

    public static String generateSalt() {
        try{
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            return new String(Base64.encodeBase64(salt));
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            //That exception never gonna happen
        }
        return null;
    }
}
