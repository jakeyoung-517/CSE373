package search.analyzers;

import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import search.models.Webpage;

import java.net.URI;

/**
 * This class is responsible for computing the 'page rank' of all available webpages.
 * If a webpage has many different links to it, it should have a higher page rank.
 * See the spec for more details.
 */
public class PageRankAnalyzer {
    private IDictionary<URI, Double> pageRanks;

    /**
     * Computes a graph representing the internet and computes the page rank of all
     * available webpages.
     *
     * @param webpages  A set of all webpages we have parsed.
     * @param decay     Represents the "decay" factor when computing page rank (see spec).
     * @param epsilon   When the difference in page ranks is less than or equal to this number,
     *                  stop iterating.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges.
     */
    public PageRankAnalyzer(ISet<Webpage> webpages, double decay, double epsilon, int limit) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        //
        // You should uncomment these lines when you're ready to begin working
        // on this class.

        // Step 1: Make a graph representing the 'internet'
        IDictionary<URI, ISet<URI>> graph = this.makeGraph(webpages);

        // Step 2: Use this graph to compute the page rank for each webpage
        this.pageRanks = this.makePageRanks(graph, decay, limit, epsilon);

        // Note: we don't store the graph as a field: once we've computed the
        // page ranks, we no longer need it!
    }

    /**
     * This method converts a set of webpages into an unweighted, directed graph,
     * in adjacency list form.
     *
     * You may assume that each webpage can be uniquely identified by its URI.
     *
     * Note that a webpage may contain links to other webpages that are *not*
     * included within set of webpages you were given. You should omit these
     * links from your graph: we want the final graph we build to be
     * entirely "self-contained".
     */
    private IDictionary<URI, ISet<URI>> makeGraph(ISet<Webpage> webpages) {
        IDictionary<URI, ISet<URI>> graph = new ChainedHashDictionary<>();
        IDictionary<URI, IList<URI>> messyGraph = new ChainedHashDictionary<>();
        for (Webpage thisPage : webpages) {
            URI thisPageURI = thisPage.getUri();
            IList<URI> thisPageLinks = thisPage.getLinks();
            messyGraph.put(thisPageURI, thisPageLinks);
        }

        for (KVPair<URI, IList<URI>> pageToLinks : messyGraph) {
            URI thisPage = pageToLinks.getKey();
            IList<URI> links = pageToLinks.getValue();
            ISet<URI> linkSet = new ChainedHashSet<>();
            for (URI link : links) {
                if (messyGraph.containsKey(link) && !link.equals(thisPage)) {
                    linkSet.add(link); // this if ensures the graph is self contained & doesn't self-loop
                }
            }
            graph.put(thisPage, linkSet);
        }
        return graph;
    }

    /**
     * Computes the page ranks for all webpages in the graph.
     *
     * Precondition: assumes 'this.graphs' has previously been initialized.
     *
     * @param decay     Represents the "decay" factor when computing page rank (see spec).
     * @param epsilon   When the difference in page ranks is less than or equal to this number,
     *                  stop iterating.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges.
     */
    private IDictionary<URI, Double> makePageRanks(IDictionary<URI, ISet<URI>> graph,
                                                   double decay,
                                                   int limit,
                                                   double epsilon) {
        // Step 1: The initialize step should go here
        int totalPages = graph.size();
        IDictionary<URI, Double> pageRank = new ChainedHashDictionary<>();
        for (KVPair<URI, ISet<URI>> page : graph) {
            pageRank.put(page.getKey(), 1.0 / totalPages);
        }

        for (int i = 0; i < limit; i++) {
            // Step 2: The update step should go here
            IDictionary<URI, Double> oldPageRank = new ChainedHashDictionary<>();
            for (KVPair<URI, ISet<URI>> page : graph) {
                URI thisPage = page.getKey();
                oldPageRank.put(thisPage, pageRank.get(thisPage));
                pageRank.put(thisPage, 0.0);
            }
            for (KVPair<URI, ISet<URI>> page : graph) {
                URI thisPage = page.getKey();
                double thisPageOldRank = oldPageRank.get(thisPage);
                ISet<URI> thisPageLinks = page.getValue();
                if (!thisPageLinks.isEmpty()) {
                    double increase = decay * thisPageOldRank / thisPageLinks.size();
                    for (URI link : thisPageLinks) {
                        pageRank.put(link, pageRank.get(link) + increase);
                    }
                } else {
                    double increase = decay * thisPageOldRank / pageRank.size();
                    for (KVPair<URI, Double> linkToOldScore : oldPageRank) {
                        URI link = linkToOldScore.getKey();
                        pageRank.put(link, pageRank.get(link) + increase);
                    }
                }
            }
            for (KVPair<URI, Double> linkToOldScore : oldPageRank) {
                URI link = linkToOldScore.getKey();
                double probabilityFixer = (1.0 - decay) / pageRank.size();
                pageRank.put(link, pageRank.get(link) + probabilityFixer);
            }

            // Step 3: the convergence step should go here.
            // Return early if we've converged.
            boolean converged = true;
            for (KVPair<URI, Double> linkToScore : pageRank) {
                URI link = linkToScore.getKey();
                if (Math.abs(linkToScore.getValue() - oldPageRank.get(link)) > epsilon) {
                    converged = false;
                }
                oldPageRank.put(link, pageRank.get(link));
            }
            if (converged) {
                return pageRank;
            }

        }
        return pageRank;
    }

    /**
     * Returns the page rank of the given URI.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public double computePageRank(URI pageUri) {
        // Implementation note: this method should be very simple: just one line!
        return pageRanks.get(pageUri);
    }
}
