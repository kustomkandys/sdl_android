package com.smartdevicelink.api;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.smartdevicelink.proxy.RPCResponse;
import com.smartdevicelink.proxy.interfaces.ISdl;
import com.smartdevicelink.proxy.rpc.DeleteFile;
import com.smartdevicelink.proxy.rpc.ListFiles;
import com.smartdevicelink.proxy.rpc.ListFilesResponse;
import com.smartdevicelink.proxy.rpc.PutFile;
import com.smartdevicelink.proxy.rpc.SdlFile;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.listeners.OnRPCResponseListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * <strong>FileManager</strong> <br>
 *
 * Note: This class must be accessed through the SdlManager. Do not instantiate it by itself. <br>
 *
 * The SDLFileManager uploads files and keeps track of all the uploaded files names during a session. <br>
 *
 * It is broken down to these areas: <br>
 *
 * 1. Getters <br>
 * 2. Deletion methods <br>
 * 3. Uploading Files / Artwork
 */
public class FileManager extends BaseSubManager {

	private static String TAG = "File Manager";
	private List<String> remoteFiles;
	private Integer spaceAvailable;
	private WeakReference<Context> context;

	FileManager(ISdl internalInterface, Context context) {

		// setup
		super(internalInterface);
		this.context = new WeakReference<>(context);

		// prepare manager - dont set state to ready until we have list of files
		retrieveRemoteFiles();
	}

	// GETTERS

	public List<String> getRemoteFileNames() {
		if (state != ManagerState.READY){
			// error and don't return list
			return null;
		}
		// return list (this is synchronous at this point)
		return remoteFiles;
	}

	private void retrieveRemoteFiles(){
		// hold list in remoteFiles class var
		ListFiles listFiles = new ListFiles();
		listFiles.setOnRPCResponseListener(new OnRPCResponseListener() {
			@Override
			public void onResponse(int correlationId, RPCResponse response) {
				if(response.getSuccess()){
					// Parse out useful data
					remoteFiles = ((ListFilesResponse) response).getFilenames();
					spaceAvailable = ((ListFilesResponse) response).getSpaceAvailable();

					// on callback set manager to ready state
					transitionToState(ManagerState.READY);

				}else{
					// There was a problem with the request, the manager cannot function properly
					Log.e(TAG, "Failed to request list of uploaded files.");
					transitionToState(ManagerState.ERROR);
				}
			}
		});
		this.internalInterface.sendRPCRequest(listFiles);
	}

	// DELETION

	public void deleteRemoteFileWithName(@NonNull final String fileName, final CompletionListener listener){
		DeleteFile deleteFileRequest = new DeleteFile();
		deleteFileRequest.setSdlFileName(fileName);
		deleteFileRequest.setOnRPCResponseListener(new OnRPCResponseListener() {
			@Override
			public void onResponse(int correlationId, RPCResponse response) {
				if(response.getSuccess()){
					Log.i(TAG, "file named: "+ fileName + " deleted");
					if (listener != null) {
						listener.onComplete(true);
					}
				}else{
					Log.e(TAG, "Unable to delete file named: "+ fileName);
					if (listener != null) {
						listener.onComplete(false);
					}
				}
			}
		});

		this.internalInterface.sendRPCRequest(deleteFileRequest);
	}

	public void deleteRemoteFilesWithNames(@NonNull ArrayList<String> fileNames, CompletionListener listener){
		if (fileNames.size() > 0){
			for (String filename : fileNames){
				deleteRemoteFileWithName(filename, null);
			}
			if (listener != null) {
				Log.i(TAG, "Multiple file deletion success");
				listener.onComplete(true);
			}
		}else{
			Log.e(TAG, "You must send items in your list");
			listener.onComplete(false);
		}

	}

	// UPLOAD FILES / ARTWORK

	public void uploadFile(@NonNull SdlFile file, CompletionListener listener){

		// DATA HANDLING

		// We need to see whether file data OR a file path was sent,
		// as they are both 'optional' - but one is needed for this to work.
		byte[] fileData = file.getFileData();
		if (fileData == null){
			int resource = file.getFilePath();
			if (resource != 0){
				fileData = contentsOfResource(resource, listener);
			}else{
				// error no data or path provided
				Log.e(TAG, "No file path or data provided :/");
				if (listener != null) {
					listener.onComplete(false);
				}
			}
		}

		// CHECKING OTHER PARAMS

		// file name
		String filename = file.getSdlFileName();

		if (filename == null){
			Log.e(TAG, "We shouldn't be here - no file name provided");
			if (listener != null) {
				listener.onComplete(false);
			}
		}

		// persistence
		Boolean persistentFile = file.getPersistentFile();

		if (persistentFile == null){
			persistentFile = false;
		}

		// PUTFILE RPC

		PutFile putFileRequest = new PutFile();
		putFileRequest.setSdlFileName(filename);
		putFileRequest.setFileType(FileType.GRAPHIC_JPEG);
		putFileRequest.setPersistentFile(persistentFile);
		putFileRequest.setFileData(fileData); // can create file_data using helper method below
		putFileRequest.setOnRPCResponseListener(new OnRPCResponseListener() {

			@Override
			public void onResponse(int correlationId, RPCResponse response) {
				setListenerType(UPDATE_LISTENER_TYPE_PUT_FILE); // necessary for PutFile requests

				if(response.getSuccess()){

				}else{
					Log.i("SdlService", "Unsuccessful app icon upload.");
				}
			}
		});
	}

	public void uploadFiles(@NonNull ArrayList<SdlFile> files, CompletionListener listener){

	}

	// HELPERS

	public static Uri resourceToUri(Context context, int resID) {
		return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
				context.getResources().getResourcePackageName(resID) + '/' +
				context.getResources().getResourceTypeName(resID) + '/' +
				context.getResources().getResourceEntryName(resID) );
	}

	/**
	 * Helper method to take resource files and turn them into byte arrays
	 * @param resource Resource file id.
	 * @return Resulting byte array.
	 */
	private byte[] contentsOfResource(int resource, CompletionListener listener) {
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
			Log.w(TAG, "Can't read file", e);
			if (listener != null){
				listener.onComplete(false);
			}
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
