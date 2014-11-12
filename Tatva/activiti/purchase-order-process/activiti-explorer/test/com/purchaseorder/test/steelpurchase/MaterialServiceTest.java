/**
 * 
 */
package com.purchaseorder.test.steelpurchase;

import static org.junit.Assert.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.purchaseorder.service.steelpurchase.MaterialService;

/**
 * @author pct53
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:context/purchaseOrder-servlet.xml",
					   "classpath:context/applicationContext.xml",
					   "classpath:context/spring-security.xml"})
public class MaterialServiceTest {
	
	String jsonObject = null;
	Principal principal = new Principal() {
		
		@Override
		public String getName() {
			return "Parth";
		}
	};
	
	String materialCode = null;
	String materialLamination = null;
	String materialTreatment = null;
	String materialThickness = null;
	String materialWidth = null;
	String materialLength = null;
	

	@Autowired
	MaterialService materialService;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception 
	{
		materialCode = "WBC00000299";
		materialLamination = "FQ";
		materialTreatment = "PR";
		materialThickness = "10.5";
		materialWidth = "500";
		materialLength = "10.4";
		
		jsonObject = "{\"materialCode\":\"" + materialCode + "\"," +
					"\"materialLamination\":\"" + materialLamination + "\"," +
					"\"materialTreatment\":\"" + materialTreatment + "\"," +
					"\"materialThickness\":\"" + materialThickness + "\"," +
					"\"materialWidth\":\"" + materialWidth + "\"," +
					"\"materialLength\":\"" + materialLength + "\"}";
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.purchaseorder.service.steelpurchase.MaterialService#addMaterial(java.lang.String, java.security.Principal)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testAddMaterial() 
	{
		Map<String, String> resultMap = (HashMap<String, String>)materialService.addMaterial(jsonObject, principal);
		String successMessage = resultMap.get("success");
		String errorMessage = resultMap.get("error");
		String materialId = resultMap.get("materialId");
		
		if(StringUtils.isNotEmpty(successMessage))
		{
			assertEquals("Material inserted successfully", successMessage);
			
			Map<String, String> materialMap = (HashMap<String, String>)materialService.getMaterial(materialId, principal);
			
			assertEquals("Material Code is not as expected", materialCode, materialMap.get("materialCode"));
			assertEquals("Material Lamination is not as expected", materialLamination, materialMap.get("materialLamination"));
			assertEquals("Material Treatment is not as expected", materialTreatment, materialMap.get("materialTreatment"));
			assertEquals("Material Thickness is not as expected", materialThickness, materialMap.get("materialThickness"));
			assertEquals("Material Width is not as expected", materialWidth, materialMap.get("materialWidth"));
			assertEquals("Material Length is not as expected", materialLength, materialMap.get("materialLength"));
		}
		else if(StringUtils.isNotEmpty(errorMessage))
		{
			assertEquals("Material Code already exists", errorMessage);
		}
		else
		{
			assertTrue(false);
		}
	}

	/**
	 * Test method for {@link com.purchaseorder.service.steelpurchase.MaterialService#getMaterial(java.lang.String, java.security.Principal)}.
	 */
	@Test
	public void testGetMaterial() 
	{
		String materialId = "1";
		
		Object obj = materialService.getMaterial(materialId, principal);
		
		Map<String, String> resultMap = (HashMap<String, String>)obj;
		assertNull("Error in getting material details", resultMap.get("error"));
		assertEquals("Material is not as expected", materialId, resultMap.get("materialId"));
	}

	/**
	 * Test method for {@link com.purchaseorder.service.steelpurchase.MaterialService#getAllMaterials(java.security.Principal)}.
	 */
	@Test
	public void testGetAllMaterials() 
	{
		Object obj = materialService.getAllMaterials(principal);
		
		if(!(obj instanceof List))
		{
			assertTrue("Not found expected result", false);
		}
	}
	
	/**
	 * Test method for {@link com.purchaseorder.service.steelpurchase.MaterialService#updateMaterial(java.lang.String, java.lang.String, java.security.Principal)}.
	 */
	@Test
	public void testUpdateMaterial() 
	{
		materialCode = "WBC0045";
		materialLamination = "FQ1";
		materialTreatment = "PR1";
		materialThickness = "10.55";
		materialWidth = "501";
		materialLength = "10.45";
		
		jsonObject = "{\"materialCode\":\"" + materialCode + "\"," +
				"\"materialLamination\":\"" + materialLamination + "\"," +
				"\"materialTreatment\":\"" + materialTreatment + "\"," +
				"\"materialThickness\":\"" + materialThickness + "\"," +
				"\"materialWidth\":\"" + materialWidth + "\"," +
				"\"materialLength\":\"" + materialLength + "\"}";
		
		String materialId = "1";
		Map<String, String> resultMap = (HashMap<String, String>)materialService.updateMaterial(jsonObject, materialId, principal);
		String successMessage = resultMap.get("success");
		String errorMessage = resultMap.get("error");
				
		assertEquals("Material updated successfully", successMessage);
		
		Map<String, String> materialMap = (HashMap<String, String>)materialService.getMaterial(materialId, principal);
		
		assertEquals("Material Code is not as expected", materialCode, materialMap.get("materialCode"));
		assertEquals("Material Lamination is not as expected", materialLamination, materialMap.get("materialLamination"));
		assertEquals("Material Treatment is not as expected", materialTreatment, materialMap.get("materialTreatment"));
		assertEquals("Material Thickness is not as expected", materialThickness, materialMap.get("materialThickness"));
		assertEquals("Material Width is not as expected", materialWidth, materialMap.get("materialWidth"));
		assertEquals("Material Length is not as expected", materialLength, materialMap.get("materialLength"));
		
		assertNull("Error raised during Material update", errorMessage);
		
		String fieldConstrains = resultMap.get("Fields Constraints");
		
		assertNull("Error in input parameters", fieldConstrains);
	}
	
	/**
	 * Test method for {@link com.purchaseorder.service.steelpurchase.MaterialService#deleteMaterial(java.lang.String, java.security.Principal)}.
	 */
	@Test
	public void testDeleteMaterial() 
	{
		String materialId = "1";
		Map<String, String> resultMap = (HashMap<String, String>)materialService.deleteMaterial(materialId, principal);
		
		assertNull("Material is not deleted successfully", materialService.getMaterial(materialId, principal));
		
		String successMessage = resultMap.get("sucess");
		String errorMessage = resultMap.get("error");
				
		assertNotNull(successMessage);
		
		assertNull("Error raised during Material delete", errorMessage);
	}

}
