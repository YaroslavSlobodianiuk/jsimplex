import org.junit.Test;
import org.junit.ComparisonFailure;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class MainTest {

	@Test
	public void fileNotFound() {
		assertEquals("File not found: idontexist.txt", Main.perform("idontexist.txt"));
	}

	@Test
	public void output1() throws ComparisonFailure {
		assertEquals(output("output1.txt"), Main.perform(fixture("input1.txt")));
	}

	@Test
	public void output2() throws ComparisonFailure {
		assertEquals(output("output2.txt"), Main.perform(fixture("input2.txt")));
	}

	@Test
	public void output3() throws ComparisonFailure {
		assertEquals(output("output3.txt"), Main.perform(fixture("input3.txt")));
	}

	@Test
	public void output4() throws ComparisonFailure {
		assertEquals(output("output4.txt"), Main.perform(fixture("input4.txt")));
	}

	private String fixture(String fileName) {
		return "src/test/fixtures/" + fileName;
	}

	private String output(String fileName) {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get("src/test/outputs/" + fileName));
			return new String(encoded, StandardCharsets.UTF_8);
		} catch (IOException e) {
			return "";
		}
	}

}
