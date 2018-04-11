package com.cisco.client.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.cisco.client.model.ImageInformation;
import com.cisco.client.model.PPTGridData;
import com.cisco.client.model.PPTInformation;
import com.cisco.client.model.RequestResult;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class Client {

	@Value("${server.uri}")
	private String serverURI;

	@Value("${pptx.download-path}")
	private String pptPath;

	@Value("${images.image-path}")
	private String imagesPath;

	@RequestMapping("file")
	public ModelAndView file() throws IOException {
		String status = httpClientCommonGet(serverURI + "/ppts");
		ObjectMapper oMp = new ObjectMapper();
		RequestResult result = oMp.readValue(status, RequestResult.class);
		ModelAndView mv = new ModelAndView("/file");
		mv.addObject("pptInfors", result.getPptInfos());
		return mv;
	}

	@RequestMapping(value = "/ppt/grid/data")
	public PPTGridData getPPTGridData(HttpServletRequest request) throws IOException {
		String current = request.getParameter("current");
		String rowCount = request.getParameter("rowCount");
		PPTGridData gridData = new PPTGridData();
		String status = httpClientCommonGet(serverURI + "/ppts");
		ObjectMapper oMp = new ObjectMapper();
		RequestResult result = oMp.readValue(status, RequestResult.class);
		List<PPTInformation> list = result.getPptInfos();
		String total = String.valueOf(list.size());
		List<PPTInformation> showList = new ArrayList<PPTInformation>();
		int start = (Integer.parseInt(current) - 1) * (Integer.parseInt(rowCount));
		int end = (Integer.parseInt(current) - 1) * (Integer.parseInt(rowCount)) + (Integer.parseInt(rowCount));
		if (Integer.parseInt(current) != 1 && 1 == (Integer.parseInt(current) % Integer.parseInt(rowCount))) {
			start = Integer.parseInt(current);
			end = Integer.parseInt(current) + Integer.parseInt(rowCount);
		}
		if (list.size() < end) {
			end = list.size();
		}
		if (Integer.parseInt(rowCount) == -1) {
			start = 0;
			end = list.size();
		}
		for (int i = start; i < end; i++) {
			showList.add(list.get(i));
		}
		gridData.setCurrent(current);
		gridData.setRowCount(rowCount);
		gridData.setRows(showList);
		gridData.setTotal(total);
		return gridData;
	}

	private String httpClientCommonGet(String url) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String status = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			System.out.println("executing request " + httpGet.getRequestLine());
			CloseableHttpResponse response2 = httpclient.execute(httpGet);
			try {
				System.out.println("----------------------------------------");
				System.out.println(response2.getStatusLine());
				HttpEntity resEntity = response2.getEntity();
				if (resEntity != null) {
					status = EntityUtils.toString(resEntity);
					System.out.println("Response content length: " + resEntity.getContentLength());
				}
				EntityUtils.consume(resEntity);
			} finally {
				response2.close();
			}
			return status;
		} finally {
			httpclient.close();
		}

	}

	@RequestMapping(value = "/preview/{id}")
	public ModelAndView preview(@PathVariable String id) throws IOException {
		String status = httpClientCommonGet(serverURI + "/ppt/" + id);
		ObjectMapper oMp = new ObjectMapper();
		RequestResult result = oMp.readValue(status, RequestResult.class);
		List<ImageInformation> imagesList = result.getPptInfo().getImageInfoList();
		Map<Integer, String> orderIdMap = new LinkedHashMap<Integer, String>();
		for (int i = 1; i <= imagesList.size(); i++) {
			orderIdMap.put(i, imagesList.get(i - 1).getId());
		}
		ModelAndView mv = new ModelAndView("/preview");
		mv.addObject("orderIdMap", orderIdMap);
		mv.addObject("pptId", id);
		mv.addObject("firstPage", 1);
		return mv;
	}

	@RequestMapping(value = "/preview/{pId}/image/{imgId}", method = RequestMethod.GET)
	public String downloadImageById(@PathVariable String pId, @PathVariable String imgId)
			throws ClientProtocolException, IOException {
		String status = httpClientCommonGet(serverURI + "/ppt/" + pId);
		ObjectMapper oMp = new ObjectMapper();
		RequestResult result = oMp.readValue(status, RequestResult.class);
		List<ImageInformation> imagesList = result.getPptInfo().getImageInfoList();
		Map<Integer, String> orderIdMap = new LinkedHashMap<Integer, String>();
		for (int i = 1; i <= imagesList.size(); i++) {
			orderIdMap.put(i, imagesList.get(i - 1).getId());
		}

		File file = httpClientDownload(
				serverURI + "/download/ppt/" + pId + "/image/" + orderIdMap.get(Integer.valueOf(imgId)),
				imagesPath + File.separator + pId + File.separator);
		return (pId + File.separator + file.getName()).replace("\\", "/");
	}

	// @RequestMapping(value = "aaaaaaaa")
	// public ModelAndView download(@PathVariable String id) throws
	// ClientProtocolException, IOException {
	// File file = new File(imagesPath + File.separator + id + File.separator);
	// File[] fileArry = file.listFiles();
	// if (fileArry == null) {
	// httpClientDownload("http://10.224.217.12:9080/converter/download/ppt/" + id +
	// "/images",
	// imagesPath + File.separator + id + File.separator);
	// }
	// file = new File(imagesPath + File.separator + id + File.separator);
	// fileArry = file.listFiles();
	// Map<Integer, String> map = new HashMap<Integer, String>();
	// for (File f : fileArry) {
	// if (f.getName().toLowerCase().endsWith("zip")) {
	// ZipFile zipFile = new ZipFile(f);
	// map = unZip(zipFile, id);
	// zipFile.close();
	// f.delete();
	// } else if (f.getName().toLowerCase().endsWith("svg")) {
	// map.put(Integer.valueOf(f.getName().split("_")[0]),
	// (id + File.separator + f.getName()).replace("\\", "/"));
	// }
	// }
	// List<Entry<Integer, String>> list = new ArrayList<Map.Entry<Integer,
	// String>>(map.entrySet());
	// Collections.sort(list, new Comparator<Map.Entry<Integer, String>>() {
	//
	// @Override
	// public int compare(Entry<Integer, String> o1, Entry<Integer, String> o2) {
	// return o1.getKey().compareTo(o2.getKey());
	// }
	// });
	// LinkedHashMap<Integer, String> linkMap = new LinkedHashMap<Integer,
	// String>();
	// for (Entry<Integer, String> e : list) {
	// linkMap.put(e.getKey(), e.getValue());
	// System.out.println(e.getKey() + " " + e.getValue());
	// }
	// ModelAndView mv = new ModelAndView("/preview");
	// mv.addObject("iMap", linkMap);
	// return mv;
	// }

	@RequestMapping("/delete/{id}")
	public ModelAndView deleteFile(@PathVariable String id) throws IOException {
		String status = httpClientCommonGet(serverURI + "/delete/ppt/" + id);
		deleteAllById(pptPath + id + File.separator);
		deleteAllById(imagesPath + id + File.separator);
		return new ModelAndView("redirect:/file");
	}

	private void deleteAllById(String parentPath) {
		File imageFile = new File(parentPath);
		if (imageFile.exists() && imageFile.isDirectory()) {
			File[] images = imageFile.listFiles();
			if (images.length > 0) {
				for (File image : images) {
					image.delete();
				}
				imageFile.delete();
			} else {
				imageFile.delete();
			}
		}
	}

	@RequestMapping("upload")
	public List<PPTInformation> upload(@RequestParam("file") MultipartFile file) throws IOException {
		// if (file == null) {
		// return "文件不能为空。";
		// }

		CloseableHttpClient httpclient = HttpClients.createDefault();
		InputStream in = file.getInputStream();
		try {
			HttpPost httppost = new HttpPost(serverURI + "/upload");
			StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);

			HttpEntity reqEntity = MultipartEntityBuilder.create()
					.addBinaryBody("file", in, ContentType.MULTIPART_FORM_DATA, file.getOriginalFilename())
					.addPart("comment", comment).build();
			httppost.setEntity(reqEntity);

			System.out.println("executing request " + httppost.getRequestLine());
			CloseableHttpResponse response2 = httpclient.execute(httppost);
			String status = null;
			try {
				System.out.println("----------------------------------------");
				System.out.println(response2.getStatusLine());
				HttpEntity resEntity = response2.getEntity();
				if (resEntity != null) {
					status = EntityUtils.toString(resEntity);

					System.out.println("Response content length: " + resEntity.getContentLength());
				}
				EntityUtils.consume(resEntity);
			} finally {
				response2.close();
			}
			// ObjectMapper oMp = new ObjectMapper();
			// JsonNode jNode = oMp.readTree(status);
			// jNode.findValuesAsText("id");

			String ll = httpClientCommonGet(serverURI + "/ppts");
			ObjectMapper oMp = new ObjectMapper();
			RequestResult result = oMp.readValue(ll, RequestResult.class);
			return result.getPptInfos();
		} finally {
			httpclient.close();
			in.close();
		}

	}

	@RequestMapping("/download/{pptId}")
	private void getPPTFileById(@PathVariable String pptId, HttpServletResponse response)
			throws ClientProtocolException, IOException {
		httpClientDownload(serverURI + "/download/ppt/" + pptId, pptPath + pptId + File.separator);

		String status = httpClientCommonGet(serverURI + "/ppt/" + pptId);
		ObjectMapper oMp = new ObjectMapper();
		RequestResult result = oMp.readValue(status, RequestResult.class);
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;fileName=" + result.getPptInfo().getFileNewName());

		File file = new File(pptPath + pptId + File.separator + result.getPptInfo().getFileOrignName());

		FileInputStream fis = null;
		OutputStream os = null;

		try {
			os = response.getOutputStream();
			fis = new FileInputStream(file);
			FileCopyUtils.copy(fis, os);

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (fis != null) {
				fis.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private File httpClientDownload(String url, String filePath) throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		File file = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			System.out.println("executing request " + httpGet.getRequestLine());
			CloseableHttpResponse response2 = httpclient.execute(httpGet);
			try {
				System.out.println("----------------------------------------");
				System.out.println(response2.getStatusLine());
				HttpEntity resEntity = response2.getEntity();
				if (resEntity != null) {
					file = new File(filePath + getFileName(response2.getFirstHeader("Content-Disposition")));
					downloadPPT(file, resEntity);
					System.out.println("Response content length: " + resEntity.getContentLength());
				}
				EntityUtils.consume(resEntity);
			} finally {
				response2.close();
			}
		} finally {
			httpclient.close();
		}
		return file;
	}

	private void downloadPPT(File file, HttpEntity resEntity) throws IOException {
		FileOutputStream ot = null;
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		ot = new FileOutputStream(file);
		resEntity.writeTo(ot);
		ot.flush();
		ot.close();
	}

	private Map<Integer, String> unZip(ZipFile zipFile, String pptId) throws ZipException, IOException {
		byte[] buffer = new byte[1024];
		Map<Integer, String> map = new HashMap<Integer, String>();
		for (Enumeration entries = zipFile.getEntries(); entries.hasMoreElements();) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			File file = new File(imagesPath + pptId + File.separator + entry.getName());
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			InputStream in = zipFile.getInputStream(entry);
			OutputStream out = new FileOutputStream(file);
			int len = 0;
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			in.close();
			out.flush();
			out.close();
			map.put(Integer.valueOf(entry.getName().split("_")[0]),
					(pptId + File.separator + entry.getName()).replace("\\", "/"));
		}
		zipFile.close();
		return map;
	}

	private String getFileName(Header contentHeader) {
		String fileName = null;
		if (contentHeader != null) {
			HeaderElement[] values = contentHeader.getElements();
			if (values.length == 1) {
				NameValuePair param = values[0].getParameterByName("filename");
				if (param != null) {
					fileName = param.getValue();
				}
			}
		}
		return fileName;
	}

}
