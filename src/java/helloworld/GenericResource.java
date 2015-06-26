/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package helloworld;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * REST Web Service
 *
 * @author ylu633
 */
@Path("generic")
public class GenericResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GenericResource
     */
    public GenericResource() {
    }

    /**
     * Retrieves representation of an instance of helloworld.GenericResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/html")
    public String getHtml() throws UnsupportedEncodingException {
        //TODO return proper representation object
                OntModel ontmodel=ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        //Pellet
        
        //OntModel ontmodel=ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC );
        FileManager.get().readModel(ontmodel, "./main/resources/data/"+"cloudowl.rdf");
        
        //List<Rule> rules=Rule.rulesFromURL("./src/main/resources/data/"+"rules.txt");
        Reasoner reasoner=new GenericRuleReasoner(Rule.rulesFromURL("./main/resources/data/"+"rules.txt"));
        InfModel inf=ModelFactory.createInfModel(reasoner, ontmodel);
        String PIZZA_NS = "http://www.semanticweb.org/yuqianlu/ontologies/2013/10/manuservice#";
        String prefix = "prefix manuservice: <" + PIZZA_NS + ">\n" +
                        "prefix rdfs: <" + RDFS.getURI() + ">\n" +
                        "prefix owl: <" + OWL.getURI() + ">\n";
        String querystring=prefix+
                "select ?x ?y where {?x manuservice:very_good ?y." +
                   "}";
                Query query=QueryFactory.create(querystring);
                //Jena
                
                QueryExecution qexec=QueryExecutionFactory.create(query, inf);
                //Pellet DL query
                //QueryExecution qexec = SparqlDLExecutionFactory.create(query, ontmodel);
                String s="";
                try{
                ResultSet results=qexec.execSelect();
                
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                //ResultSetFormatter.out(ps, results,query);
                ResultSetFormatter.outputAsJSON(ps, results);
                s=new String(baos.toByteArray(),"UTF-8");
                }
                finally{
                    qexec.close();
                }
        return "<html><body><h1>"+s+"</body></h1></html>";
    }

    /**
     * PUT method for updating or creating an instance of GenericResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("text/html")
    public void putHtml(String content) {
    }
}
