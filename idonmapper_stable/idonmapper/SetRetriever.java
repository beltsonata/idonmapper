/**
 * Copyright Sean Talbot 2010.
 * 
 * This file is part of idonmapper.
 * idonmapper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * idonmapper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with idonmapper.  If not, see <http://www.gnu.org/licenses/>. 
 */


package idonmapper;

import java.io.*;
import java.net.*;
import java.util.*;
import net.htmlparser.jericho.*;

/**
 * Retrieves suggestions from Google Sets based upon 1-4 strings.
 */
public class SetRetriever
{
    private final static String URL_STRING_TEMPLATE
                                  = "http://labs.google.com/sets?hl=en",
                         ENCODING = "utf-8",
                         BTN = "&btn=",
                         LARGE_SET = "Large+Set",
                         SMALL_SET = "Small+Set",
                         EQUALS = "=",
                         QUERY = "&q";
                    
    private List<String> resultsList;
    private static final int MAX_IDEAS = 5;
    protected boolean isLargeSet;  
    private Source source;
    
    /*
     * Multiple constructors in for flexibility
     * when creating new SetRetrievers.
     */
     
    /**
     * Creates a new retriver.
     */  
    public SetRetriever(){}

    /**
     * Creates a new retriever from a set of ideas.
     */ 
    public SetRetriever(final Set<String> ideas, final boolean isLargeSet)
    {
    	this(new ArrayList<String>(ideas), isLargeSet);
    }

    public SetRetriever(final List<String> ideas, final boolean isLargeSet){}

    public SetRetriever(final String s, final boolean isLargeSet)
    {
        this(new ArrayList<String>() 
        {{
            add(s);
        }}, isLargeSet);
    }

    /*
     * Retrieves a set from Google Sets based on the contents of @list.
     */
    public List<String> requestSet(final String... list)
    {
        resultsList = null;

        if(list.length > MAX_IDEAS)
        {
            throw new IllegalStateException("SetRetriever: List too "
                                            + "big");
        }
        final List<String> ideasList = encodeUserConcepts(list);
        resultsList = new ArrayList<String>();
        final String urlString = createURLString(ideasList);
        final URL url = createURL(urlString);
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
        if(source == null)
        {
            // Failed to get a response from G Sets
            return;
        }
        
        final List<Element> suggs = new ArrayList<Element>();
        final List<Element> tableElements = source.getAllElements("table");
        
        if(tableElements == null)
        {
            /*
             * Failed to get any results from
             * Google Sets. Probably means
             * the query was bad.
             */                         
            return;
        }
        
        for(final Element e : tableElements)
        {
            final Segment seg = e.getContent();
            suggs.addAll(seg.getAllElements("size", "-1", true));
        }
        
        for(final Element e : suggs)
        {
            try
            {
                final StartTag st = e.getFirstStartTag("a");
                final Element content = st.getElement();
                resultsList.add(content.getContent().toString());            
            }
            catch(final NullPointerException n)
            {
                /*
                 * Failed to get any results from
                 * Google Sets Probably means
                 * the query was bad, or the server was busy.
                 */
                return;
            }        
        }
    }
    
    /*
     * Returns a Source object from
     * the Google Sets query URL
     */ 
    private Source getSourceFromURL(final URL url)
    {
        try
        {
            return new Source(url);
        }
        catch(final IOException e)
        {
            System.out.println(e.getMessage());
            //e.printStackTrace();
        }
        return null;
    }
    
    /*
     * Encodes the user's idea(s) into utf-8 format prior to sending
     * to Google Sets.
     */
    private List<String> encodeUserConcepts(final String... ideas)
    {
        final List<String> encodedStrings = new ArrayList<String>();
        for(final String s : ideas)
        {
            try
            {
                encodedStrings.add(URLEncoder.encode(s, ENCODING));
            }
            catch(final UnsupportedEncodingException e)
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
    private String createURLString(final List<String> list)
    {
        String urlString = URL_STRING_TEMPLATE;

        for(int i = 0; i < list.size(); ++i)
        {
            urlString = urlString.concat(
                        toQueryString(list.get(i), (i+1)));
        }
        urlString = urlString.concat(BTN);
        urlString = urlString.concat(LARGE_SET);
        return urlString;
    }
    
    /*
     * Appends the html query token to a string
     * and concatenates it to String @s
     */ 
    private String toQueryString(final String s, final Integer n)
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
    private URL createURL(final String s)
    {
        try
        {
            return new URL(s);
        }
        catch(final MalformedURLException e)
        {
            System.out.println("URL could not be recognised: " + s);
            errorExit(e);
        }
        return null;
    }

    /*
     * Utility method to print out an array list
     */ 
    public void printList(final List<String> list)
    {
        for(final String s : list)
        {
            System.out.println(s);
        }
    }

    /**
     * Main public access method, 
     * to allow for debugging / generic
     * usage.
     */
    public static void main(final String args[])
    {
        final List<String> res;
        
        if(args.length == 1)
        {
            System.out.println("Please enter up to 5 words to send to "
                                + "Google Sets.");
            System.exit(-1);
        }
        
        final SetRetriever sr = new SetRetriever();
        res = sr.requestSet(args);
        
        for(final String s : res)
        {
            System.out.println(s);
        }
    }
    private void errorExit(final Exception e)
    {
        e.printStackTrace();
        System.exit(-1);
    }
}
