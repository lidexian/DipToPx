package com.yy.android.gamenews.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.duowan.gamenews.bean.Manifest;
import com.duowan.gamenews.bean.ManifestItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yy.android.gamenews.GameNewsApplication;
import com.yy.android.gamenews.R;

public class FileUtil {

	// public static DiscCacheAware discCache;

	// public static void setDiscCache(Context context) {
	//
	// // if (discCacheFileNameGenerator == null) {
	// // discCacheFileNameGenerator =
	// // DefaultConfigurationFactory.createFileNameGenerator();
	// // }
	// // discCache = DefaultConfigurationFactory.createDiscCache(context,
	// // DefaultConfigurationFactory.createFileNameGenerator(), 0, 0);
	// File individualCacheDir = StorageUtils
	// .getIndividualCacheDirectory(context);
	// discCache = new LimitedAgeDiscCache(individualCacheDir, 3600 * 24 * 7);
	//
	// }

	public final static String BASE_PATH = "/duowan/gamenews/";

	public static String getBaseDir() {
		File baseDir = Environment.getExternalStorageDirectory();
		String basePath = baseDir.getPath() + BASE_PATH;
		File dir = new File(basePath);
		if (false == dir.exists()) {
			dir.mkdirs();
		}
		return dir.getPath();
	}

	public static String saveImage(String url) {
		if (!Util.isSDExists()) {
			Toast.makeText(
					GameNewsApplication.getInstance(),
					GameNewsApplication.getInstance().getResources()
							.getString(R.string.sd_absent), Toast.LENGTH_SHORT)
					.show();
			return null;
		}
		File fromFile = ImageLoader.getInstance().getDiscCache().get(url);
		if (!fromFile.exists()) {
			return null;
		}
		if (!fromFile.isFile()) {
			return null;
		}
		if (!fromFile.canRead()) {
			return null;
		}
		String toFileName = String.format("%s/%s.jpg", getBaseDir(),
				fromFile.getName());
		File toFile = new File(toFileName);
		if (toFile.exists()) {
			toFile.delete();
		}

		try {
			FileInputStream fosfrom = new FileInputStream(fromFile);
			FileOutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
			return toFileName;
		} catch (Exception ex) {
			Log.e("readfile", ex.getMessage());
		}
		return null;
	}
	public static Manifest parseManifest(byte[] input) throws JSONException {
		if (input == null) {
			return null;
		}
		Manifest manifest = new Manifest();
		JSONObject json = new JSONObject(new String(input));
		manifest.setVersion(json.getString("version"));
		JSONArray jsonArray = json.getJSONArray("data");
		Map<String, ManifestItem> data = new HashMap<String, ManifestItem>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject item = jsonArray.getJSONObject(i);
			ManifestItem cache = new ManifestItem();
			cache.setUrl(item.getString("url"));
			cache.setVersion(item.getString("version"));
			data.put(cache.getUrl(), cache);
		}
		manifest.setData(data);
		return manifest;
	}

	public static String toString(Manifest manifest) throws JSONException {
		JSONObject json = new JSONObject();
		// json.put("version", manifest.getVersion());
		JSONArray data = new JSONArray();
		for (ManifestItem manifestItem : manifest.getData().values()) {
			JSONObject item = new JSONObject();
			item.put("url", manifestItem.getUrl());
			item.put("version", manifestItem.getVersion());
			data.put(item);
		}
		json.put("data", data);

		return json.toString();
	}

	public static byte[] readFile(File file) throws IOException {
		InputStream in = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			in = new FileInputStream(file);
			stream(in, out);
			return out.toByteArray();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void writeFile(File file, byte[] data) throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(data);
			out.close();
			out = null;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void copyFile(File from, File to) throws IOException {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(from);
			out = new FileOutputStream(to);
			stream(in, out);
			out.close();
			out = null;
			in.close();
			in = null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void save(InputStream in, File file) throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			stream(in, out);
			out.close();
			out = null;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static byte[] download(String url) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		download(url, out);
		return out.toByteArray();
	}

	public static void download(String url, File file) throws IOException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			download(url, out);
			out.close();
			out = null;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void download(String url, OutputStream out)
			throws IOException {
		HttpURLConnection conn = null;
		try {
			URL u = new URL(url);
			conn = (HttpURLConnection) u.openConnection();
			if (conn.getResponseCode() != 200) {
				throw new IOException(
						"HttpURLConnection.getResponseCode() returns "
								+ conn.getResponseCode());
			}
			stream(conn.getInputStream(), out);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	public static void stream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buf = new byte[8192];
		int len;
		while ((len = in.read(buf)) != -1) {
			out.write(buf, 0, len);
		}
	}

	public static boolean deleteDirectory(String filePath) {
		if (null == filePath) {
			return false;
		}

		File file = new File(filePath);

		if (file == null || !file.exists()) {
			return false;
		}

		if (file.isDirectory()) {
			File[] list = file.listFiles();

			for (int i = 0; i < list.length; i++) {

				if (list[i].isDirectory()) {
					deleteDirectory(list[i].getAbsolutePath());
				} else {
					list[i].delete();
				}
			}
		}

		file.delete();
		return true;
	}
}