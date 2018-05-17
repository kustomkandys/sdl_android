package com.smartdevicelink.api;

import android.content.Context;
import android.util.Log;

import com.smartdevicelink.proxy.interfaces.ISdl;
import com.smartdevicelink.proxy.rpc.SdlFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * <strong>FileManager</strong> <br>
 *
 * Note: This class must be accessed through the SdlManager. Do not instantiate it by itself. <br>
 *
 * The SDLFileManager uploads files and keeps track of all the uploaded files names during a session. <br>
 *
 * We need to add the following struct: SDLFile<br>
 *
 * It is broken down to these areas: <br>
 *
 * 1. Getters <br>
 * 2. Deletion methods <br>
 * 3. Uploading Files / Artwork
 */
public class FileManager extends BaseSubManager {

	private static String TAG = "File Manager";
	private ArrayList<String> remoteFiles;
	private WeakReference<Context> context;

	FileManager(ISdl internalInterface, Context context) {

		// setup
		super(internalInterface);
		this.context = new WeakReference<>(context);

		// prepare manager - dont set state to ready until we have list of files
		retrieveRemoteFiles();
	}

	// GETTERS

	public ArrayList<String> getRemoteFileNames() {

		if (state != ManagerState.READY){
			// error and dont return list
		}
		// return list (this is synchronous at this point)
		return null;
	}

	private void retrieveRemoteFiles(){
		// hold list in remoteFiles class var\

		// on callback set manager to ready state
		transitionToState(ManagerState.READY);
	}

	// DELETION

	public void deleteRemoteFileWithName(String fileName, CompletionListener listener){

	}

	public void deleteRemoteFilesWithNames(ArrayList<String> fileNames, CompletionListener listener){

	}

	// UPLOAD FILES / ARTWORK

	public void uploadFile(SdlFile file, CompletionListener listener){

	}

	public void uploadFiles(ArrayList<SdlFile> files, CompletionListener listener){

	}

	// HELPERS

	/**
	 * Helper method to take resource files and turn them into byte arrays
	 * @param resource Resource file id.
	 * @return Resulting byte array.
	 */
	private byte[] contentsOfResource(int resource) {
		InputStream is = null;
		try {
			is = context.get().getResources().openRawResource(resource);
			ByteArrayOutputStream os = new ByteArrayOutputStream(is.available());
			final int bufferSize = 4096;
			final byte[] buffer = new byte[bufferSize];
			int available;
			while ((available = is.read(buffer)) >= 0) {
				os.write(buffer, 0, available);
			}
			return os.toByteArray();
		} catch (IOException e) {
			Log.w(TAG, "Can't read icon file", e);
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
