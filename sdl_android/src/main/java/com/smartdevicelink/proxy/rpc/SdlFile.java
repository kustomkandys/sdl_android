package com.smartdevicelink.proxy.rpc;

import android.support.annotation.NonNull;

import com.smartdevicelink.proxy.RPCStruct;
import com.smartdevicelink.proxy.rpc.enums.FileType;

import java.net.URI;

/**
 * An object that contains all of the necessary information to upload a file to a head unit.
 */
public class SdlFile extends RPCStruct {
	public static final String KEY_SDL_FILE_NAME= "sdlFileName";
	public static final String KEY_FILE_PATH= "filePath";
	public static final String KEY_FILE_DATA= "fileData";
	public static final String KEY_FILE_TYPE= "fileType";
	public static final String KEY_PERSISTENT_FILE= "persistentFile";

	public SdlFile() {}

	/**
	 * Set the file name for the file to be uploaded. This parameter is required
	 * @param fileName - the name of the file
	 */
	public void setSdlFileName(@NonNull String fileName) {
		setValue(KEY_SDL_FILE_NAME, fileName);
	}

	/**
	 * @return - the set name of the SDLFile object
	 */
	public String getSdlFileName() {
		return getString(KEY_SDL_FILE_NAME);
	}

	/**
	 * Set the file path for the file to be uploaded. This parameter is optional.
	 * If this parameter is set, you do not need to set the file data.
	 * @param filePath - the URI of the file to be uploaded.
	 */
	public void setFilePath(URI filePath) { setValue(KEY_FILE_PATH, filePath); }

	/**
	 * @return - The URI of the file
	 */
	public URI getFilePath() { return (URI) getValue(KEY_FILE_PATH); }

	/**
	 * Set the file data as a byte array. If this is set, there is no need to set the
	 * file path
	 * @param fileData - the data of the file as a byte array
	 */
	public void setFileData(byte[] fileData) { setValue(KEY_FILE_DATA, fileData); }

	/**
	 * @return - a byte array of the file data
	 */
	public byte[] getFileData() { return (byte[]) getValue(KEY_FILE_DATA); }

	/**
	 * Set the file type of the file being uploaded. If not set, we will check to
	 * make sure it is one of the {@link FileType} types and set it for you.
	 * @param fileType - the {@link FileType} of the file being uploaded.
	 */
	public void setFileType(FileType fileType) { setValue(KEY_FILE_TYPE, fileType); }

	/**
	 * @return - the {@link FileType} - of the uploaded file
	 */
	public FileType getFileType() { return (FileType) getValue(KEY_FILE_TYPE); }

	/**
	 * Set whether or not the file should persist on disk between car ignition cycles.
	 * @param persistentFile - True or False
	 */
	public void setPersistentFile(Boolean persistentFile) { setValue(KEY_PERSISTENT_FILE, persistentFile); }

	/**
	 * @return - A Boolean of whether the file should persist on disk between car ignition cycles.
	 */
	public Boolean getPersistentFile() { return getBoolean(KEY_PERSISTENT_FILE); }

}
