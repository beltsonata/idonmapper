package idonmapper;

import java.io.*;
import java.net.*;
import java.util.*;
//import org.w3c.dom.*;
import net.htmlparser.jericho.*;



/**
 * Retrieves suggestions from Google Sets based upon 1-4 strings.
 */
public class SetRetriever
{
    private final String URL_STRING_TEMPLATE = "http://labs.google.com/sets?hl=en",
                         ENCODING = "utf-8",
                         BTN = "&btn=",
                         LARGE_SET = "Large+Set",
                         SMALL_SET = "Small+Set",
                         EQUALS = "=",
                         QUERY = "&q";
                    
    private ArrayList<String> ideasList;
    private ArrayList<String> resultsList;
    protected boolean isLargeSet;             
    private URL url;
    private Source source;
    
    /*
     * Multiple constructors in for flexibility
     * when creating new SetRetrievers.
     */
    public SetRetriever(){}

    public SetRetriever(HashSet<String> ideas, boolean isLargeSet)
    {
    	this(new ArrayList<String>(ideas), isLargeSet);
    }

    public SetRetriever(ArrayList<String> ideas, boolean isLargeSet){}

    public SetRetriever(final String s, boolean isLargeSet)
    {
        this(new ArrayList<String>() 
        {{
            add(s);
        }}, isLargeSet);
    }

    /*
     * Retrieves a set from Google Sets based on the contents of @list.
     */
    public ArrayList<String> requestSet(String... list)
    {
        resultsList = null;

        if(list.length > 5)
        {
            throw new IllegalStateException("SetRetriever: List too"
                                            + "big");
        }
                
        ideasList = encodeUserConcepts(list);
        this.resultsList = new ArrayList<String>();
        String urlString = createURLString(ideasList);
        url = createURL(urlString);
        source = getSourceFromURL(url);
        getResultsFromSource();
        return resultsList;
    }
    
    /*
     * Scrapes and stores the Google 
     * Sets results from the URL
     */ 
    private void getResultsFromSource()
    {
        ArrayList<Element> suggs = new ArrayList<Element>();
        List<Element> tableElements = source.getAllElements("table");
        
        if(tableElements == null)
        {
            /*
             * Failed to get any results from
             * Google Sets. Probably means
             * the query was bad.
             */                         
            return;
        }
        
        for(Element e : tableElements)
        {
            Segment seg = e.getContent();
            suggs.addAll(seg.getAllElements("size", "-1", true));
        }
        
        for(Element e : suggs)
        {
            try
            {
                StartTag st = e.getFirstStartTag("a");
                Element content = st.getElement();
                resultsList.add(content.getContent().toString());            
            }
            catch(NullPointerException n)
            {
                /*
                 * Failed to get any results from
                 * Google Sets Probably means
                 * the query was bad.
                 */
                return;
            }        
        }
    }
    
    /*
     * Returns a Source object from
     * the Google Sets query URL
     */ 
    private Source getSourceFromURL(URL url)
    {
        try
        {
            return new Source(url);
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }

   
    
    /*
     * Encodes the user's idea(s) into utf-8 format prior to sending
     * to Google Sets.
     */
    private ArrayList<String> encodeUserConcepts(String... ideas)
    {
        ArrayList<String> encodedStrings = 
                                new ArrayList<String>();
        for(String s : ideas)
        {
            try
            {
                encodedStrings.add(URLEncoder.encode(s, ENCODING));
            }
            catch(UnsupportedEncodingException e)
            {
                errorExit(e);
            }
        }
        return encodedStrings;
    }
 
    /*
     * Constructs a URL string based upon the user's list.
     * 
     * Assumes that the list is already encoded to the same
     * as that specified in the SetRetriever
     */
    private String createURLString(ArrayList<String> list)
    {
        String urlString = URL_STRING_TEMPLATE;

        for(int i = 0; i < list.size(); ++i)
        {
            urlString = urlString.concat(
                        toQueryString(list.get(i), (i+1)));
        }
        urlString = urlString.concat(BTN);
        //if(isLargeSet)
        //{
        urlString = urlString.concat(LARGE_SET);
        //}
        /*else
        {
            urlString = urlString.concat(SMALL_SET);
        }*/
        return urlString;
    }
    
    /*
     * Appends the html query token to a string
     * and concatenates it to String @s
     */ 
    private String toQueryString(String s, Integer n)
    {
        assert((n > 0) && (n <= 5));
        
        String q = QUERY;
        q = q.concat(n.toString());
        q = q.concat(EQUALS);
        q = q.concat(s);
        return q;
    }

    /*
     * Creates the URL used to query Google Sets
     */ 
    private URL createURL(String s)
    {
        try
        {
            return new URL(s);
        }
        catch(MalformedURLException e)
        {
            System.out.println("URL could not be recognised: " + s);
            errorExit(e);
        }
        return null;
    }

    /*
     * Utility method to print out an array list
     */ 
    public void printList(ArrayList<String> list)
    {
        for(String s : list)
        {
            System.out.println(s);
        }
    }

    /**
     * Main public access method, 
     * to allow for debugging / generic
     * usage.
     */
    public static void main(String args[])
    {
        if(args.length == 1)
        {
            System.out.println("Please enter up to 5 words to send to "
                                + "Google Sets.");
            System.exit(-1);
        }
        
        SetRetriever sr = new SetRetriever();
        ArrayList<String> res = sr.requestSet(args);
        for(String s : res)
        {
            System.out.println(s);
        }
    }
    private void errorExit(Exception e)
    {
        e.printStackTrace();
        System.exit(-1);
    }
}
