package com.t4m.serializer;

import com.t4m.extractor.entity.ProjectInfo;
import com.t4m.extractor.util.FileUtil;
import com.t4m.extractor.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Yuxiang Liao on 2020-06-25 00:09.
 */
public class T4MProjectInfoSerializer implements T4MSerializer {

	public static final Logger LOGGER = LoggerFactory.getLogger(T4MProjectInfoSerializer.class);
	private static String dbPath = PropertyUtil.getProperty("OBJECT_DB_PATH");

	@Override
	public void serializeTo(ProjectInfo targerObj, String outputFileName) {
		//创建一个ObjectOutputStream输出流
		if (FileUtil.checkAndMakeDirectory(dbPath)) {
			String absDBFilePath = dbPath + File.separator + outputFileName;
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(absDBFilePath))) {
				oos.writeObject(targerObj);
			} catch (Exception e) {
				LOGGER.error("Error happen when serializing [{}] to [{}]", targerObj, absDBFilePath, e);
			}
		} else {
			LOGGER.error("Cannot find directory [{dbPath}] when serializing projectInfo object (createDate: {}).",
			             targerObj.getCreateDate());
		}
	}

	@Override
	public ProjectInfo deserializeFrom(String outputFileName) {
		//创建一个ObjectInputStream输入流
		if (FileUtil.checkAndMakeDirectory(dbPath)) {
			String absDBFilePath = dbPath + File.separator + outputFileName;
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(absDBFilePath))) {
				return (ProjectInfo) ois.readObject();
			} catch (Exception e) {
				LOGGER.error("Error happen when deserializing object from [{}]", absDBFilePath, e);
			}
		} else {
			LOGGER.error("Cannot find directory [{dbPath}] when deserializing the object from file {}", outputFileName);
		}
		return null;
	}

	@Override
	public List<ProjectInfo> deserializeAll() {
		File dbDir = new File(dbPath);
		List<ProjectInfo> projectInfoList = new ArrayList<>();
		if (FileUtil.checkAndMakeDirectory(dbDir)) {
			File[] files = dbDir.listFiles();
			for (File objFile : files) {
				try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(objFile))) {
					projectInfoList.add((ProjectInfo) ois.readObject());
				} catch (Exception e) {
					LOGGER.error("Error happen when deserializing object from [{}]", objFile,e);
				}
			}
		} else {
			LOGGER.info("There is no file in {}", dbPath);
		}
		projectInfoList.sort(Comparator.comparing(ProjectInfo::getCreateDate));
		return projectInfoList;
	}

}
