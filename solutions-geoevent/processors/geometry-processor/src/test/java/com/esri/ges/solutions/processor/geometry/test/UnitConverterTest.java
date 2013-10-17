package com.esri.ges.solutions.processor.geometry.test;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.esri.ges.solutions.processor.geometry.UnitConverter;

public class UnitConverterTest {
	UnitConverter uc;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		uc = new UnitConverter();
	}

	@Test
	public void testUnitConverter() {
		assertNotNull("", uc);
	}

	@Test
	public void testFindWkidMeter() {
		assertEquals("", 9001, uc.findWkid("Meter"));
	}
	@Test
	public void testFindWkidKilometer() {
		assertEquals("", 9036, uc.findWkid("Kilometer"));
	}
	@Test
	public void testFindWkidFoot() {
		assertEquals("", 9003, uc.findWkid("Foot_US"));
	}
	@Test
	public void testFindWkidMile() {
		assertEquals("", 9035, uc.findWkid("Mile_US"));
	}
	@Test
	public void testFindWkidNautcalMile() {
		assertEquals("", 9030, uc.findWkid("Nautical_Mile"));
	}

	@Test
	public void testFindConnonicalNameMeters() {
		assertEquals("", "Meter", uc.findConnonicalName("Meters"));
	}
	@Test
	public void testFindConnonicalNameKilometers() {
		assertEquals("", "Kilometer", uc.findConnonicalName("Kilometers"));
	}
	@Test
	public void testFindConnonicalNameFeet() {
		assertEquals("", "Foot_US", uc.findConnonicalName("Feet"));
	}
	@Test
	public void testFindConnonicalNameMiles() {
		assertEquals("", "Mile_US", uc.findConnonicalName("Miles"));
	}
	@Test
	public void testFindConnonicalNameNauticalMiles() {
		assertEquals("", "Nautical_Mile", uc.findConnonicalName("Nautical Miles"));
	}

	@Test
	public void testConvertDoubleMeterToFeet() {
		assertEquals("", 3.28084, uc.Convert(1.0, 9001, 9003), 0.00001);
	}

	@Test
	public void testConvertDoubleStringSpatialReference() {
		//fail("Not yet implemented");
	}

}
