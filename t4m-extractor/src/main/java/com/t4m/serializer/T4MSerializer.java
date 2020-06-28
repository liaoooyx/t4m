package com.t4m.serializer;


import com.t4m.extractor.entity.ProjectInfo;

import java.io.File;
import java.util.List;

public interface T4MSerializer {

	//序列化
	public void serializeTo(ProjectInfo targerObj, String outputFileName);

	//反序列化
	public ProjectInfo deserializeFrom(String outputFileName);

	public List<ProjectInfo> deserializeAll();

}
