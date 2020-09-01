package com.t4m.web;

import com.t4m.conf.GlobalProperties;
import com.t4m.serializer.T4MProjectInfoSerializer;
import com.t4m.serializer.T4MSerializer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AllTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@Order(1)
	@DisplayName("Check whether the test project exists.")
	void contextLoads() {
		assertNotNull(System.getenv("T4M_HOME"));
		File file = new File(
				System.getenv("T4M_HOME") + File.separator + "t4m-extractor" + File.separator + "src" + File.separator +
						"test" + File.separator + "resources" + File.separator + "JSimulation");
		assertTrue(file.exists());
	}

	@Test
	@Order(2)
	void createNewProject() throws Exception {
		File file = new File(
				System.getenv("T4M_HOME") + File.separator + "t4m-extractor" + File.separator + "src" + File.separator +
						"test" + File.separator + "resources" + File.separator + "JSimulation");
		assertTrue(file.exists());
		mockMvc.perform(MockMvcRequestBuilders.post("/operation/new").param("projectPath", file.getAbsolutePath())
		                                      .param("projectCreatTime", "")
		                                      .param("excludedPath", GlobalProperties.DEFAULT_EXCLUDED_PATH)
		                                      .param("dependencyPath", GlobalProperties.DEFAULT_DEPENDENCY_PATH))
		       .andExpect(MockMvcResultMatchers.status().isOk());//添加期望，如果返回结果是ok的

	}

	@AfterAll
	static void deleteProject() throws Exception {
		T4MSerializer serializer = new T4MProjectInfoSerializer();
		serializer.delete(GlobalProperties.getCurrentProjectIdentifier());
	}

	@Nested
	class BasicControllerTest {

		@Test
		void overview() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/basic")).andExpect(
					MockMvcResultMatchers.status().isOk()).andExpect(
					model().attribute("currentProjectInfo", notNullValue())).andExpect(
					model().attribute("preProjectInfo", notNullValue())).andExpect(
					model().attribute("currentProjectIdentifier", notNullValue())).andExpect(
					model().attribute("timeRecords", hasSize(1))).andExpect(
					model().attribute("moduleRecords", hasSize(1))).andExpect(
					model().attribute("packageRecords", hasSize(1))).andExpect(
					model().attribute("javaFileRecords", hasSize(1))).andExpect(
					model().attribute("classRecords", hasSize(1))).andExpect(
					model().attribute("allClassRecords", hasSize(1)));
		}

		@Test
		void selectModuleRecord() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/basic/table/module")).andExpect(
					MockMvcResultMatchers.status().isOk());
		}

		@Test
		void selectPackageRecord() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/basic/table/package")).andExpect(
					MockMvcResultMatchers.status().isOk());
		}

		@Test
		void selectClassRecord() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/basic/table/class")).andExpect(
					MockMvcResultMatchers.status().isOk());
		}

	}

	@Nested
	class CohesionControllerTest {

		@Test
		void overview() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/cohesion")).andExpect(
					MockMvcResultMatchers.status().isOk());
		}

		@Test
		void selectClassRecord() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/cohesion/table/class")).andExpect(
					MockMvcResultMatchers.status().isOk());
		}

		@Test
		void selectTableChartRecordForClass() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/cohesion/table/chart/class")
			                                      .param("qualifiedName", "com.simulation.core.foo.ComplexClassA"))
			       .andExpect(MockMvcResultMatchers.status().isOk());
		}
	}

	@Nested
	class ComplexityControllerTest {

		@Test
		void overview() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/complexity")).andExpect(
					MockMvcResultMatchers.status().isOk());
		}

		@Test
		void selectMethodRecord() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/complexity/table/method")
			                                      .param("classQualifiedName", "com.simulation.core.foo.ComplexClassA"))
			       .andExpect(MockMvcResultMatchers.status().isOk());
		}

		@Test
		void selectClassRecord() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/complexity/table/class")).andExpect(
					MockMvcResultMatchers.status().isOk());
		}

		@Test
		void selectTableChartRecordForClass() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/complexity/table/chart/class")
			                                      .param("qualifiedName", "com.simulation.core.foo.ComplexClassA"))
			       .andExpect(MockMvcResultMatchers.status().isOk());
		}
	}

	@Nested
	class CouplingControllerTest {

		@Test
		void overview() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/coupling")).andExpect(
					MockMvcResultMatchers.status().isOk());
		}

		@Test
		void selectPackageRecord() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/coupling/table/package")).andExpect(
					MockMvcResultMatchers.status().isOk());
		}

		@Test
		void selectTableChartRecordForPackage() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/coupling/table/chart/package")
			                                      .param("qualifiedName", "com.simulation.core.foo")).andExpect(
					MockMvcResultMatchers.status().isOk());
		}

		@Test
		void selectClassRecord() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/coupling/table/class")
			                                      .param("pkgQualifiedName", "com.simulation.core.foo")).andExpect(
					MockMvcResultMatchers.status().isOk());
		}

		@Test
		void selectTableChartRecordForClass() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/coupling/table/chart/package")
			                                      .param("qualifiedName", "com.simulation.core.foo.ComplexClassA"))
			       .andExpect(MockMvcResultMatchers.status().isOk());
		}
	}

	@Nested
	class InheritanceControllerTest {

		@Test
		void overview() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/inheritance")).andExpect(
					MockMvcResultMatchers.status().isOk());
		}

		@Test
		void selectClassRecord() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/inheritance/table/class")).andExpect(
					MockMvcResultMatchers.status().isOk());
		}

		@Test
		void selectTableChartRecordForClass() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/inheritance/table/chart/class")
			                                      .param("qualifiedName", "com.simulation.core.foo.ComplexClassA"))
			       .andExpect(MockMvcResultMatchers.status().isOk());
		}
	}

	@Nested
	class SLOCControllerTest {

		@Test
		void overview() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/sloc")).andExpect(MockMvcResultMatchers.status().isOk());
		}

		@Test
		void selectModuleRecord() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/sloc/table/module")).andExpect(
					MockMvcResultMatchers.status().isOk());
		}

		@Test
		void selectPackageRecord() throws Exception {
			mockMvc.perform(
					MockMvcRequestBuilders.post("/dashboard/sloc/table/package").param("name", "JSimulation/submodule1"))
			       .andExpect(MockMvcResultMatchers.status().isOk());
		}

		@Test
		void selectSubPackageRecord() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.post("/dashboard/sloc/table/subpackage")
			                                      .param("name", "com.simulation.core.foo")).andExpect(
					MockMvcResultMatchers.status().isOk());
		}

		@Test
		void selectTableChartRecord() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.post("/dashboard/sloc/table/chart")
			                                      .param("name", "com.simulation.core.foo").param("level", "package"))
			       .andExpect(MockMvcResultMatchers.status().isOk());
		}
	}
}