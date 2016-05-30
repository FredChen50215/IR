/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.CrawlerMaven;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/** Simple command-line based search demo. */
public class SearchFiles {

  //public SearchFiles() {}

  /** Simple command-line based search demo. */
  public static String search(String queryWord) throws Exception {
	String returnResults = null;
    String index = "index";
    String field = "contents";
    int repeat = 0;
    boolean raw = true;
    String queryString = null;
    int hitsPerPage = 10;
    
    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
    IndexSearcher searcher = new IndexSearcher(reader);
    Analyzer analyzer = new StandardAnalyzer();

    QueryParser parser = new QueryParser(field, analyzer);
    while (true) {
      String line = queryWord;

      if (line == null || line.length() == -1) {
        break;
      }

      line = line.trim();
      if (line.length() == 0) {
        break;
      }
      
      Query query = parser.parse(line);
      //System.out.println("Searching for: " + query.toString(field));
            
      if (repeat > 0) {                           // repeat & time as benchmark
        Date start = new Date();
        for (int i = 0; i < repeat; i++) {
          searcher.search(query, 100);
        }
        Date end = new Date();
        //System.out.println("Time: "+(end.getTime()-start.getTime())+"ms");
      }

      returnResults = doPagingSearch(queryWord, searcher, query, hitsPerPage, raw, queryString == null);
      break;
    }
    reader.close();
    return returnResults;
  }

  /**
   * This demonstrates a typical paging search scenario, where the search engine presents 
   * pages of size n to the user. The user can then go to the next page if interested in
   * the next hits.
   * 
   * When the query is executed for the first time, then only enough results are collected
   * to fill 5 result pages. If the user wants to page beyond this limit, then the query
   * is executed another time and all hits are collected.
   * 
   */
  public static String doPagingSearch(String queryWord, IndexSearcher searcher, Query query, 
                                     int hitsPerPage, boolean raw, boolean interactive) throws IOException {
 
	String returnResults;
    // Collect enough docs to show 5 pages
    TopDocs results = searcher.search(query, 5 * hitsPerPage);
    ScoreDoc[] hits = results.scoreDocs;
    
    int numTotalHits = results.totalHits;
    returnResults = numTotalHits + " total matching documents\n";
    //System.out.println(numTotalHits + " total matching documents");

    int start = 0;
    int end = Math.min(numTotalHits, hitsPerPage);
        
    if (end > hits.length) {
      //System.out.println("Only results 1 - " + hits.length +" of " + numTotalHits + " total matching documents collected.");
      //System.out.println("Collect more (y\n) ?");
      hits = searcher.search(query, numTotalHits).scoreDocs;
    }
      
    end = Math.min(hits.length, start + hitsPerPage);
      
    if(raw) returnResults += "<doc#> <similarity score>\n"; 
  	  //System.out.println("<doc#> <similarity score>");
      
    for (int i = start; i < end; i++) {
      if (raw) {                              // output raw format
          returnResults += hits[i].doc+" "+Math.rint(hits[i].score*100)/100+"\n";
        //System.out.println(hits[i].doc+" "+Math.rint(hits[i].score*100)/100);
        continue;
      }

      Document doc = searcher.doc(hits[i].doc);
      String path = doc.get("path");
      if (path != null) {
      	returnResults += (i+1) + ". " + path+"\n";
        //System.out.println((i+1) + ". " + path);
        String title = doc.get("title");
        if (title != null) {
      	  returnResults += "   Title: " + doc.get("title")+"\n";
          //System.out.println("   Title: " + doc.get("title"));
        }
      } else {
      	returnResults += (i+1) + ". " + "No path for this document"+"\n";
        //System.out.println((i+1) + ". " + "No path for this document");
      }
                  
    }
    return returnResults;
  }
}
