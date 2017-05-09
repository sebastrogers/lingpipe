import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class classify {
	
	public static void main(String[] args) throws IOException {

		DynamicLMClassify classify = new DynamicLMClassify();
		
		List<String> categories = new ArrayList<String>();
		
		categories.add("frase com preconceito");
		categories.add("frase sobre preconceito");
		
		
		
		classify.trainClassifyDirectory("train", categories, 2);
		
		/**
		 * XValidatingObjectCorpus<Classified<CharSequence>> corpus = new
		 * XValidatingObjectCorpus<Classified<CharSequence>>(NUM_FOLDS);
		 * 
		 * for (String category : CATEGORIES) {
		 * 
		 * Classification c = new Classification(category); File trainCatDir =
		 * new File(TRAINING_DIR,category); for (File trainingFile :
		 * trainCatDir.listFiles()) { String text =
		 * Files.readFromFile(trainingFile,"ISO-8859-1"); Classified
		 * <CharSequence> classified = new Classified<CharSequence>(text,c);
		 * corpus.handle(classified); }
		 * 
		 * File testCatDir = new File(TESTING_DIR,category); for (File testFile
		 * : testCatDir.listFiles()) { String text =
		 * Files.readFromFile(testFile,"ISO-8859-1"); Classified
		 * <CharSequence> classified = new Classified<CharSequence>(text,c);
		 * corpus.handle(classified); } }
		 */
		String classe = classify.classify("ser negro no brasil é difícil");
		
		System.out.println(classe);
		
	}

}
