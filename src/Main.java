import java.io.File;
import java.util.Arrays;
import java.util.List;

import entity.Set;
import id3.Id3;
import id3.TestID3;
import parser.Parser;

public class Main
{
	private static List<String> arguments;
	private static String learnFilepath;
	private static String testFilepath;
	private static String parseFormat = null;
	private static boolean metrics = false;
	private static boolean informationGain = true;

	private static final String[] BREAST = new String[] { "-t", "breastcancer_ucna.csv", "-T",
			"breastcancer_testna.csv", "-i", "-m", "informationgain", "-p", "," };
	private static final String[] CAR = new String[] { "-t", "car_ucna.csv", "-T", "car_testna.csv", "-i", "-m",
			"informationgain", "-p", "," };
	private static final String[] DEVELOPING = new String[] { "-t", "processMe", "-T", "processMe", "-i", "-m",
			"informationgain", "-p", ";" };

	public static void main(String[] args)
	{
		if (args == null || args.length < 6)
			args = DEVELOPING;

		parseArgs(args);
		File learnFile = new File(learnFilepath);
		File testFile = new File(testFilepath);
		Set learnSet = new Set();
		Parser parser = new Parser(learnSet, learnFile);
		parser.parse(parseFormat);
		Id3 id3 = new Id3(informationGain, learnSet.getAttributeNames());
		try
		{
			id3.startId3(learnSet.getAllParameterEntries());
			Set testSet = new Set();
			parser = new Parser(testSet, testFile);
			parser.parse(parseFormat);
			TestID3 tid3 = new TestID3();
			tid3.testID3(id3.getDecisionTree(), testSet.getAllParameterEntries(), testSet.getAttributeNames());
			outputConfusionMatrix(tid3.getConfusionMatrix(), tid3.getClasses());
			if (metrics)
				outputMetrics(tid3);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void outputMetrics(TestID3 tid3)
	{
		System.out.printf("Metrics:%nAccuracy: %f%nF-Score: %f%nPrecision: %f%nRecall: %f%n", tid3.accuracy(),
				tid3.FScore(), tid3.precision(), tid3.recall());

	}

	private static void outputConfusionMatrix(int[][] confusionMatrix, List<String> list)
	{
		System.out.println("Confusion matrix:");
		int spacing = 10;
		for (String value : list)
		{
			System.out.printf("%-" + spacing + "s  ", value);
		}
		System.out.println(" <-- predicted class");
		int count = 0;
		for (int[] values : confusionMatrix)
		{
			for (int value : values)
			{
				System.out.printf(" %-" + spacing + "d ", value);
			}
			System.out.println(" " + list.get(count++));
		}
	}

	private static void parseArgs(String[] args)
	{
		arguments = Arrays.asList(args);

		if (arguments.contains("-t"))
			learnFilepath = arguments.get(arguments.indexOf("-t") + 1);
		if (arguments.contains("-T"))
			testFilepath = arguments.get(arguments.indexOf("-T") + 1);
		if (arguments.contains("-i"))
			metrics = true;
		if (arguments.contains("-m"))
		{
			String tmp = arguments.get(arguments.indexOf("-m") + 1);
			if (tmp.equals("entropy") || tmp.equals("entropija"))
				informationGain = false;
			else
				informationGain = true;
		}
		if (arguments.contains("-p"))
			parseFormat = arguments.get(arguments.indexOf("-p") + 1);

	}
}
