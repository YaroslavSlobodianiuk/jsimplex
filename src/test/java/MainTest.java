import org.junit.Test;
import org.junit.ComparisonFailure;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;
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
	public void outputTest() throws IOException {
		File fixturesDir = new File("src/test/fixtures");
	  File[] fixtures = fixturesDir.listFiles();

	  if (fixtures != null)
	    for (File fixture : fixtures) {
	      assertEquals(Main.perform(fixture.getPath()), output(fixture.getName()));
	    }
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
