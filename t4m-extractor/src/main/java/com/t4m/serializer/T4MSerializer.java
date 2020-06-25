package com.t4m.serializer;


import com.t4m.extractor.entity.ProjectInfo;

import java.io.File;

public interface T4MSerializer {

	//序列化
	public void serializeTo(ProjectInfo targerObj, String outputFileName);

	//反序列化
	public ProjectInfo deserializeFrom(String outputFileName);

}
