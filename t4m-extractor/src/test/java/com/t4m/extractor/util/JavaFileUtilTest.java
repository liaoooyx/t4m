package com.t4m.extractor.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JavaFileUtilTest {

	@Test
	void readJavaSource() {
		String javaSource =
				"package com.t4m.extractor.util;\n" + "\n" + "import com.t4m.extractor.scanner.ClassScanner;\n" +
						"import org.slf4j.Logger;\n" + "import org.slf4j.LoggerFactory;\n" + "\n" +
						"import java.io.File;\n" + "import java.io.FileInputStream;\n" +
						"import java.io.FileNotFoundException;\n" + "import java.io.IOException;\n" + "\n" + "/**\n" +
						" * Created by Yuxiang Liao on 2020-06-17 06:01.\n" + " */\n" +
						"public class JavaFileUtil {\n" + "\n" +
						"\tpublic static final Logger LOGGER = LoggerFactory.getLogger(JavaFileUtil.class);\n" + "\n" +
						"\t/**\n" + "\t * 读取Java源文件内容，以字符串返回。默认文件编码为UTF-8\n" + "\t */\n" +
						"\tpublic static String readJavaSource(String absolutePath) {\n" + "\t\t//TODO 考虑文件编码的影响\n" +
						"\t\tString encoding = \"UTF-8\";\n" + "\t\tFile file = new File(absolutePath);\n" +
						"\t\tLong filelength = file.length();\n" +
						"\t\tbyte[] filecontent = new byte[filelength.intValue()];\n" +
						"\t\ttry (FileInputStream in = new FileInputStream(file)) {\n" +
						"\t\t\tin.read(filecontent);\n" + "\t\t\treturn new String(filecontent, encoding);\n" +
						"\t\t} catch (FileNotFoundException e) {\n" +
						"\t\t\tLOGGER.error(\"Cannot find {}. [{}]\", absolutePath, e.toString(), e);\n" +
						"\t\t} catch (IOException e) {\n" +
						"\t\t\tLOGGER.error(\"Error happened when retrieving file content. [{}]\", e.toString(), e);\n" +
						"\t\t}\n" + "\t\treturn null;\n" + "\t}\n" + "\n" +
						"\tpublic static void main(String[] args) {\n" + "\t\tString path =\n" +
						"\t\t\t\t\"/Users/liao/myProjects/IdeaProjects/t4m/t4m-extractor/src/main/java/com/t4m/extractor/util/JavaFileUtil.java\";\n" +
						"\t\tString javaSource = JavaFileUtil.readJavaSource(path);\n" +
						"\t\tSystem.out.println(javaSource);\n" + "\t}\n" + "}\n";
		String path =
				"/Users/liao/myProjects/IdeaProjects/t4m/t4m-extractor/src/main/java/com/t4m/extractor/util/JavaFileUtil.java";
		String output = JavaFileUtil.readJavaSource(path);
		assertEquals(javaSource, output);
	}
}