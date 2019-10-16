package search.analyzers;

import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import search.models.Webpage;

import java.net.URI;

/**
 * This class is responsible for computing how "relevant" any given document is
 * to a given search query.
 *
 * See the spec for more details.
 */
public class TfIdfAnalyzer {
    // This field must contain the IDF score for every single word in all
    // the documents.
    private IDictionary<String, Double> idfScores;
    // This field must contain the TF-IDF vector for each webpage you were given
    // in the constructor.
    //
    // We will use each webpage's page URI as a unique key.
    private IDictionary<URI, IDictionary<String, Double>> documentTfIdfVectors;
    private IDictionary<URI, Double> documentTfIdfNorms;

    // Feel free to add extra fields and helper methods.

    public TfIdfAnalyzer(ISet<Webpage> webpages) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        //
        // You should uncomment these lines when you're ready to begin working
        // on this class.

        this.idfScores = this.computeIdfScores(webpages);
        this.documentTfIdfVectors = this.computeAllDocumentTfIdfVectors(webpages);

        IDictionary<URI, Double> vecNorms = new ChainedHashDictionary<>();
        for (KVPair<URI, IDictionary<String, Double>> pageToVec : documentTfIdfVectors) {
            vecNorms.put(pageToVec.getKey(), norm(pageToVec.getValue()));
        }
        this.documentTfIdfNorms = vecNorms;
    }

    // Note: this method, strictly speaking, doesn't need to exist. However,
    // we've included it so we can add some unit tests to help verify that your
    // constructor correctly initializes your fields.
    public IDictionary<URI, IDictionary<String, Double>> getDocumentTfIdfVectors() {
        return this.documentTfIdfVectors;
    }

    // Note: these private methods are suggestions or hints on how to structure your
    // code. However, since they're private, you're not obligated to implement exactly
    // these methods: feel free to change or modify these methods however you want. The
    // important thing is that your 'computeRelevance' method ultimately returns the
    // correct answer in an efficient manner.

    private double norm(IDictionary<String, Double> vector) {
        double output = 0.0;
        for (KVPair<String, Double> val : vector) {
            double value = val.getValue();
            output += value*value;
        }
        return Math.sqrt(output);
    }


    /**
     * Return a dictionary mapping every single unique word found
     * in every single document to their IDF score.
     */
    private IDictionary<String, Double> computeIdfScores(ISet<Webpage> pages) {
        IDictionary<URI, IDictionary<String, Double>> pageToIDF = new ChainedHashDictionary<>();
        IDictionary<String, Double> totalWordContained = new ChainedHashDictionary<>();

        int numberOfDocs = pages.size();
        // This nested loop finds the number of documents containing each word
        for (Webpage thispage : pages) {
            IDictionary<String, Double> wordsInPage = new ChainedHashDictionary<>();
            IList<String> words = thispage.getWords();
            for (String word : words) {
                if (!wordsInPage.containsKey(word)) {
                    wordsInPage.put(word, wordsInPage.getOrDefault(word, 0.0) + 1.0);
                }
            }
            for (KVPair<String, Double> word : wordsInPage) {
                totalWordContained.put(word.getKey(), totalWordContained.getOrDefault(word.getKey(), 0.0)+1);
            }
        }
        // calculates idfScore using stored contained count and number of docs
        IDictionary<String, Double> wordToScore = new ChainedHashDictionary<>();
        for (KVPair<String, Double> wordToCount : totalWordContained) {
            wordToScore.put(wordToCount.getKey(), Math.log((double) numberOfDocs / wordToCount.getValue()));
        }
        return wordToScore;
    }

    /**
     * Returns a dictionary mapping every unique word found in the given list
     * to their term frequency (TF) score.
     *
     * The input list represents the words contained within a single document.
     */
    private IDictionary<String, Double> computeTfScores(IList<String> words) {
        int totalWords = 0;
        IDictionary<String, Double> wordToTF = new ChainedHashDictionary<>();
        for (String word : words) {
            totalWords++;
            wordToTF.put(word, wordToTF.getOrDefault(word, 0.0) + 1.0);
        }
        IDictionary<String, Double> tfScores = new ChainedHashDictionary<>();
        for (KVPair<String, Double> wordToScore : wordToTF) {
            tfScores.put(wordToScore.getKey(), wordToScore.getValue() / (double) totalWords);
        }
        return tfScores;
    }

    /**
     * See spec for more details on what this method should do.
     */
    private IDictionary<URI, IDictionary<String, Double>> computeAllDocumentTfIdfVectors(ISet<Webpage> pages) {
        // Hint: this method should use the idfScores field and
        // call the computeTfScores(...) method.
        IDictionary<URI, IDictionary<String, Double>> pageToScore = new ChainedHashDictionary<>();
        for (Webpage thispage : pages) {
            IDictionary<String, Double> tfScores = computeTfScores(thispage.getWords());
            IDictionary<String, Double> scores = new ChainedHashDictionary<>();
            for (KVPair<String, Double> word : tfScores) {
                scores.put(word.getKey(), word.getValue()*idfScores.get(word.getKey()));
            }
            pageToScore.put(thispage.getUri(), scores);
        }
        return pageToScore;
    }

    /**
     * Returns the cosine similarity between the TF-IDF vector for the given query and the
     * URI's document.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public Double computeRelevance(IList<String> query, URI pageUri) {
        // Note: The pseudocode we gave you is not very efficient. When implementing,
        // this method, you should:
        //
        // 1. Figure out what information can be precomputed in your constructor.
        //    Add a third field containing that information.
        //
        // 2. See if you can combine or merge one or more loops.
        double queryScore = 0.0;

        IDictionary<String, Double> queryTFIDF = new ChainedHashDictionary<>();
        IDictionary<String, Double> tfscores = computeTfScores(query);
        for (KVPair<String, Double> word : tfscores) {
            queryTFIDF.put(word.getKey(), word.getValue()*idfScores.get(word.getKey()));
        }
        IDictionary<String, Double> docTFIDF = documentTfIdfVectors.get(pageUri);

        double numerator = 0.0;
        double queryNorm = 0.0;
        double docWordScore = 0.0;
        double queryWordScore = 0.0;
        for (KVPair<String, Double> word : queryTFIDF) {
            double number = word.getValue();
            queryNorm += number*number;

            docWordScore = docTFIDF.getOrDefault(word.getKey(), 0.0);
            queryWordScore = queryTFIDF.get(word.getKey());
            numerator += docWordScore * queryWordScore;
        }
        queryNorm = Math.sqrt(queryNorm);
        double denominator = queryNorm * documentTfIdfNorms.get(pageUri);

        if (denominator != 0) {
            return numerator / denominator;
        }
        return 0.0;
    }
}
