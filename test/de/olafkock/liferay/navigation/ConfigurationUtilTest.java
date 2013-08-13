package de.olafkock.liferay.navigation;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testReadEmptyValue() {
		List<GroupConfig> config = ConfigurationUtil.decode(new String[] {});
		assertEquals(0, config.size());
		config = ConfigurationUtil.decode(null);
		assertEquals(0, config.size());
	}

	@Test
	public void testReadSingleValidValue() {
		List<GroupConfig> configs = ConfigurationUtil.decode(new String[] {"1234,public"});
		assertEquals(1, configs.size());
		GroupConfig config = configs.get(0);
		assertEquals(1234L, config.getGroupId());
		assertEquals(false, config.isPrivate());
	}

	public void testReadTwoValidValues() {
		List<GroupConfig> configs = ConfigurationUtil.decode(new String[] {"1234,public", "4321,private"});
		assertEquals(2, configs.size());
		GroupConfig config = configs.get(0);
		assertEquals(1234L, config.getGroupId());
		assertEquals(false, config.isPrivate());
		config = configs.get(1);
		assertEquals(4321L, config.getGroupId());
		assertEquals(true,  config.isPrivate());
	}

	public void readTwoIllegalValues() {
		List<GroupConfig> configs = ConfigurationUtil.decode(new String[] {"1234,honk", "not-a-number,private"});
		assertEquals(0, configs.size());
	}
	public void readMixedLegalIllegalValues() {
		List<GroupConfig> configs = ConfigurationUtil.decode(new String[] {"1234,honk", "1234,public", "not-a-number,private", "4321,private"});
		assertEquals(0, configs.size());
		GroupConfig config = configs.get(0);
		config = configs.get(0);
		assertEquals(1234L, config.getGroupId());
		assertEquals(false, config.isPrivate());
		config = configs.get(1);
		assertEquals(4321L, config.getGroupId());
		assertEquals(true,  config.isPrivate());
	}

}
