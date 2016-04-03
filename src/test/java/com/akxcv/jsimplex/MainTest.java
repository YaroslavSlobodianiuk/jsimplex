package com.akxcv.jsimplex;

import com.akxcv.jsimplex.exception.FunctionNotLimitedException;
import com.akxcv.jsimplex.problem.Problem;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileFilter;
import java.io.IOException;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class MainTest {
    
    private static String FIXTURES_DIR = "src" + File.separator + "test" + File.separator + "fixtures" + File.separator;
    private static String OUTPUTS_DIR = "src" + File.separator + "test" + File.separator + "outputs" + File.separator;
    
    private static Method getUserFileInput;
    private static Method createProblem;
    private static Input dummyInput;
    private static Problem dummyProblem;

    @BeforeClass
    public static void setUp() throws Exception {
        getUserFileInput = Main.class.getDeclaredMethod("getUserFileInput", String.class);
        getUserFileInput.setAccessible(true);

        createProblem = Main.class.getDeclaredMethod("createProblem", Input.class);
        createProblem.setAccessible(true);

        dummyInput = new Input(null, null);
        dummyProblem = new Problem(null, null, null);
    }

    @Test
	public void defaultTest() throws IOException, InvocationTargetException, IllegalAccessException, FunctionNotLimitedException {
        for (File file : getDirectoryFileList("default")) {
            Problem problem = createProblem(getFixturePath("default", file.getName()));
            assertEquals(problem.solve().toString().trim(), getOutput("default", file.getName()).trim());
        }
	}

    @Test(expected = FunctionNotLimitedException.class)
    public void exceptionTest() throws FunctionNotLimitedException, InvocationTargetException, IllegalAccessException, IOException {
        for (File file : getDirectoryFileList("exception")) {
            Problem problem = createProblem(getFixturePath("exception", file.getName()));
            problem.solve();
        }
    }

    @Test
    public void csvTest() throws IOException, InvocationTargetException, IllegalAccessException, FunctionNotLimitedException {
        for (File file : getDirectoryFileList("default")) {
            Problem problem = createProblem(getFixturePath("default", file.getName()));
            assertEquals(problem.solve().toString(false, false, true).trim(), getOutput("csv", file.getName()).trim());
        }
    }

    @Test
    public void integerTest() throws IOException, InvocationTargetException, IllegalAccessException, FunctionNotLimitedException {
        for (File file : getDirectoryFileList("default")) {
            Problem problem = createProblem(getFixturePath("default", file.getName()));
            assertEquals(problem.solve().toString(false, true, false).trim(), getOutput("integer", file.getName()).trim());
        }
    }

    private String getFixturePath(String dir, String fileName) {
        return FIXTURES_DIR + dir + File.separator + fileName;
    }

    private String getOutput(String dir, String fileName) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(OUTPUTS_DIR + dir + File.separator + fileName));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    private Problem createProblem(String filePath) throws InvocationTargetException, IllegalAccessException {
        return (Problem) createProblem.invoke(dummyProblem, (Input) getUserFileInput.invoke(dummyInput, filePath));
    }
    
    private File[] getDirectoryFileList(String dir) {
        File fixturesDir = new File(FIXTURES_DIR + dir);
        return fixturesDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
    }

}
