package com.t4m.extractor;

import com.t4m.extractor.entity.ProjectInfo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

/**
 * Created by Yuxiang Liao on 2020-06-14 23:34.
 */
public class ProjectInfoProvider implements ArgumentsProvider {

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
		ProjectInfo projectInfo1 = new ProjectInfo();
		projectInfo1.setRootPath("/Users/liao/myProjects/IdeaProjects/sonarqube");
		projectInfo1.setProjectName("TestProject1-sonarqube");

		ProjectInfo projectInfo2 = new ProjectInfo();
		projectInfo2.setRootPath("/Users/liao/myProjects/IdeaProjects/comp5911m/refactor");
		projectInfo2.setProjectName("TestProject2-refactor");

		return Stream.of(projectInfo1, projectInfo2).map(Arguments::of);
	}
}
