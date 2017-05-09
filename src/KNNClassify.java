
import java.io.File;
import java.io.IOException;
import java.util.List;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.ConfusionMatrix;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.classify.RankedClassification;

import com.aliasi.classify.KnnClassifier;
import com.aliasi.classify.NaiveBayesClassifier;
import com.aliasi.classify.RankedClassifier;
import com.aliasi.classify.RankedClassifierEvaluator;
import com.aliasi.corpus.ObjectHandler;
import com.aliasi.lm.TokenizedLM;
import com.aliasi.tokenizer.LowerCaseTokenizerFactory;
import com.aliasi.tokenizer.RegExTokenizerFactory;
import com.aliasi.tokenizer.TokenFeatureExtractor;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Compilable;
import com.aliasi.util.FeatureExtractor;
import com.aliasi.util.Files;

public class KNNClassify{

	 ObjectHandler<Classified<CharSequence>> classifier; //classificador

	
    static final TokenizerFactory SPACE_TOKENIZER_FACTORY
    = new RegExTokenizerFactory("\\S+");


    static final TokenizerFactory NORM_TOKENIZER_FACTORY
    = normTokenizerFactory();

    FeatureExtractor<CharSequence> featureExtractor
    = new TokenFeatureExtractor(NORM_TOKENIZER_FACTORY);
	
    static TokenizerFactory normTokenizerFactory() {
    	TokenizerFactory factory = SPACE_TOKENIZER_FACTORY;  //espaço em branco
    	factory = new LowerCaseTokenizerFactory(factory); //minúsculo
    	return factory;
    }
  

	/**
	 * Creates classifier from a directory
	 * @param trainDirectory	Directory that contains data to train
	 * @param categories		Categories of classifier
	 * @param k					Number of neighbors
	 * @return					True if the classifier was created
	 */
	@SuppressWarnings("unchecked")
	public boolean trainClassifyDirectory(String trainDirectory, List<String> categories, int k) throws IOException{

		classifier = new KnnClassifier(featureExtractor, k); // cria o classificador - lista de palavras como features e k vizinhos
		
		/** NaiveBayesClassifier
		String[] categoriesVector = new String[categories.size()]; 	
		categoriesVector = categories.toArray(categoriesVector);
				
		classifier = new NaiveBayesClassifier(categoriesVector, SPACE_TOKENIZER_FACTORY);
		*/
		
		/** DynamicLMClassifier
		TokenizedLM token = new TokenizedLM(SPACE_TOKENIZER_FACTORY, 1);
		TokenizedLM token1 = new TokenizedLM(NORM_TOKENIZER_FACTORY, 2);
		TokenizedLM token2 = new TokenizedLM(NORM_TOKENIZER_FACTORY, 3);		
		TokenizedLM[] tokenArray = {token,token1,token2};
		
		classifier = new DynamicLMClassifier<>(categoriesVector, tokenArray);
		 */
		for(int i=0; i<categories.size(); ++i) { // para cada categoria
			File classDir = new File(trainDirectory,categories.get(i)); // pega o diretório dela
			if (!classDir.isDirectory()) {
				String msg = "Could not find training directory="
					+ classDir;
				System.out.println(msg); 
				throw new IllegalArgumentException(msg);
			}

			String[] trainingFiles = classDir.list(); // pega os arquivos de uma categoria
			for (int j = 0; j < trainingFiles.length; ++j) { //para cada arquivo
				File file = new File(classDir,trainingFiles[j]); 
				String text = Files.readFromFile(file,"ISO-8859-1"); //abre 
				System.out.println("Training on " + categories.get(i) + "/" + trainingFiles[j]);
				Classification classification
				= new Classification(categories.get(i));
				Classified<CharSequence> classified
				= new Classified<CharSequence>(text,classification); // texto classificação
				classifier.handle(classified); // adiciona no classificador
			}
		}
		return true;
	}

	/**
	 * Classify a text
	 * @param text	Text to be classifies
	 * @return		Text Category
	 */
	@SuppressWarnings("unchecked")
	public String classify(String text){

		RankedClassifier<CharSequence> compiledClassifier; // para classificar texto usando rank
		String bestCategory = null;
		
		try {
			compiledClassifier = (RankedClassifier<CharSequence>)
			AbstractExternalizable.compile((Compilable)classifier); // cria o classificador usando o classifier
			
			RankedClassification jc = compiledClassifier.classify(text);
			bestCategory = jc.bestCategory(); //retorna a melhor categoria
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bestCategory;
	}
}
