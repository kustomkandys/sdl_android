package com.smartdevicelink.test.rpc.datatypes;

import com.smartdevicelink.proxy.rpc.SdlFile;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.test.Test;

import junit.framework.TestCase;

import java.net.URI;

/**
 * Created by brettywhite on 5/17/18.
 */
public class SdlFileTests extends TestCase {

	private SdlFile msg;

	@Override
	public void setUp(){
		msg = new SdlFile();

		msg.setSdlFileName(Test.GENERAL_STRING);
		msg.setFileData(Test.GENERAL_BYTE_ARRAY);
		msg.setFilePath(Test.GENERAL_URI);
		msg.setFileType(Test.GENERAL_FILETYPE);
		msg.setPersistentFile(Test.GENERAL_BOOLEAN);
	}

	/**
	 * Tests the expected values of the RPC message.
	 */
	public void testValues () {
		// Test Values
		String fileName = msg.getSdlFileName();
		byte[] data = msg.getFileData();
		URI uri = msg.getFilePath();
		FileType fileType = msg.getFileType();
		Boolean persistent = msg.getPersistentFile();

		// Valid Tests
		assertEquals(Test.MATCH, Test.GENERAL_STRING, fileName);
		assertEquals(Test.MATCH, Test.GENERAL_BYTE_ARRAY, data);
		assertEquals(Test.MATCH, Test.GENERAL_URI, uri);
		assertEquals(Test.MATCH, Test.GENERAL_FILETYPE, fileType);
		// Test.GENERAL_BOOLEAN == true
		assertTrue(persistent);

		// Invalid/Null Tests
		SdlFile msg = new SdlFile();
		assertNotNull(Test.NOT_NULL, msg);

		assertNull(Test.NULL, msg.getSdlFileName());
		assertNull(Test.NULL, msg.getFileData());
		assertNull(Test.NULL, msg.getFilePath());
		assertNull(Test.NULL, msg.getFileType());
		assertNull(Test.NULL, msg.getPersistentFile());
	}

}
