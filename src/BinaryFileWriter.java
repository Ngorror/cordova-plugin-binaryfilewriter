/**
 * PhoneGap BinaryFileWriter plugin for Android
 *
 *
 * @author Antonio Hernandez <ahernandez@emergya.com>
 *
 */

package com.phonegap.plugins.binaryfilewriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import org.apache.cordova.FileUtils;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.channels.FileChannel;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;


/**
 * This plugin allows to write binary data to a file.
 */
public class BinaryFileWriter extends CordovaPlugin {

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        try {

            if (action.equals("writeBinaryArray")) {
                long fileSize = this.writeBinaryArray(args.getString(0), args.getJSONArray(1), args.getInt(2));
				callbackContext.success();
				return true;
            } else {
                callbackContext.error("MISSING_writeBinaryArray");
                PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                callbackContext.sendPluginResult(r);
                return false;
            }
            
        } catch (FileNotFoundException e) {
			callbackContext.error("NOT_FOUND_ERR");
			PluginResult r = new PluginResult(PluginResult.Status.ERROR);
			callbackContext.sendPluginResult(r);
			return false;
        } catch (JSONException e) {
			callbackContext.error("NO_MODIFICATION_ALLOWED_ERR");
			PluginResult r = new PluginResult(PluginResult.Status.ERROR);
			callbackContext.sendPluginResult(r);
			return false;
        } catch (IOException e) {
			callbackContext.error("INVALID_MODIFICATION_ERR");
			PluginResult r = new PluginResult(PluginResult.Status.ERROR);
			callbackContext.sendPluginResult(r);
			return false;
        }
	}

    /**
     * Write the contents of a binary array to a file.
     *
     * @param filename			The name of the file.
     * @param data				The contents of the file (Array of bytes).
     * @param offset			The position to begin writing the file.
     * @throws FileNotFoundException, IOException, JSONException
     */
    public long writeBinaryArray(String filename, JSONArray data, int offset) throws FileNotFoundException, IOException, JSONException {

        filename = stripFileProtocol(filename);

        boolean append = false;
        if (offset > 0) {
            truncateFile(filename, offset);
            append = true;
        }

        byte[] rawData = new byte[data.length()];
        for (int i = 0; i < data.length(); i++) {
             rawData[i] = (byte)data.getInt(i);
        }

        ByteArrayInputStream in = new ByteArrayInputStream(rawData);
        FileOutputStream out = new FileOutputStream(filename, append);
        byte buff[] = new byte[rawData.length];
        in.read(buff, 0, buff.length);
        out.write(buff, 0, rawData.length);
        out.flush();
        out.close();

        return data.length();
    }

    /**
     * This method removes the "file://" from the passed in filePath
     *
     * @param filePath to be checked.
     * @return
     */
    private String stripFileProtocol(String filePath) {
        if (filePath.startsWith("file://")) {
            filePath = filePath.substring(7);
        }
        return filePath;
    }

    /**
     * Truncate the file to size
     *
     * @param filename
     * @param size
     * @throws FileNotFoundException, IOException
     */
    private long truncateFile(String filename, long size) throws FileNotFoundException, IOException {
        filename = stripFileProtocol(filename);

        RandomAccessFile raf = new RandomAccessFile(filename, "rw");

        if (raf.length() >= size) {
               FileChannel channel = raf.getChannel();
               channel.truncate(size);
               return size;
        }

        return raf.length();
    }

}