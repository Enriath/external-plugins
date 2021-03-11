package io.hydrox.transmog;

import io.hydrox.transmog.config.V1Parser;
import io.hydrox.transmog.config.V2Parser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TransmogPresetParserTest
{
	private static final String V1_CONFIG = "2643,19697,null,24802,null,24804,-1,10083,24680,-1";
	private static final List<String> V1_SLOT_VALUES = Arrays.asList("2643","19697","null","24802","null","24804","-1","10083","24680","-1");
	private static final String V2_CONFIG = "\"25500,monke,10828,1052,1704,2503,-1,2497,null,7460,3105,-1";
	private static final List<String> V2_SLOT_VALUES = Arrays.asList("10828","1052","1704","2503","-1","2497","null","7460","3105","-1");

	@Test
	public void testV1Parser()
	{
		V1Parser parser = new V1Parser();
		parser.parse(V1_CONFIG);
		assertEquals(V1_SLOT_VALUES, parser.getSlotValues());
		assertEquals(-1, parser.getIcon());
		assertEquals("", parser.getName());
	}

	@Test
	public void testV2Parser()
	{
		V2Parser parser = new V2Parser();
		parser.parse(V2_CONFIG);
		assertEquals(V2_SLOT_VALUES, parser.getSlotValues());
		assertEquals(25500, parser.getIcon());
		assertEquals("monke", parser.getName());

		// Check deny V1
		parser = new V2Parser();
		parser.parse(V1_CONFIG);
		assertEquals("", parser.getName());
		assertEquals(-1, parser.getIcon());
		assertNull(parser.getSlotValues());
	}

	@Test
	public void testV1ToV2Migration()
	{
		V1Parser v1 = new V1Parser();
		v1.parse(V1_CONFIG);
		V2Parser v2 = new V2Parser();
		v2.migrate(v1);

		assertEquals(V1_SLOT_VALUES, v2.getSlotValues());
		assertEquals(-1, v2.getIcon());
		assertEquals("", v2.getName());
	}

	@Test
	public void testV2ToV1Migration()
	{
		V2Parser v2 = new V2Parser();
		v2.parse(V2_CONFIG);
		V1Parser v1 = new V1Parser();
		v1.migrate(v2);

		assertEquals(V2_SLOT_VALUES, v1.getSlotValues());
		assertEquals(-1, v1.getIcon());
		assertEquals("", v1.getName());
	}
}
