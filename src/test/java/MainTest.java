import org.junit.Test;
import java.io.IOException;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class MainTest {

	private String FIXTURES_FOLDER = "src/test/fixtures/";
	private String OUTPUTS_FOLDER = "src/test/outputs/";

	@Test
	public void fileNotFound() {
		assertEquals("Файл не найден: idontexist.txt", Main.perform("idontexist.txt"));
	}

	@Test
	public void defaultTest() throws IOException {
		testDirectory("default");
	}

	@Test
	public void integerTest() throws IOException {
		testDirectory("integer", true);
	}

	private void testDirectory(String dir, boolean integer) throws IOException {
		File fixturesDir = new File(FIXTURES_FOLDER + dir);
	  	File[] fixtures = fixturesDir.listFiles();

	  	if (fixtures != null)
			for (File fixture : fixtures)
				testFixture(dir, fixture, integer);
	}

	private void testDirectory(String dir) throws IOException {
		testDirectory(dir, false);
	}

	private void testFixture(String dir, File fixture, boolean integer) throws IOException {
		SimplexTable simplexTable = new SimplexTable(Main.createSimplexTable(fixture.getPath()));
		assertEquals(output(dir, fixture.getName()).trim(), Main.solve(simplexTable).toString(integer).trim());
	}

	private String output(String dir, String fileName) {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(OUTPUTS_FOLDER + dir + "/" + fileName));
			return new String(encoded, StandardCharsets.UTF_8);
		} catch (IOException e) {
			return "";
		}
	}

}
