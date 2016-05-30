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


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;	
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import com.chenlb.mmseg4j.analysis.MaxWordAnalyzer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/** Index all text files under a directory.
 * <p>
 * This is a command-line application demonstrating simple Lucene indexing.
 * Run it with no command-line arguments for usage information.
 */
public class IndexFiles {
  
  static String handledContentsPath = "handledContents.txt";
  static String docsPath = "CrawlerContent.txt";
  static String fbOwnerName;
  static String fbUrl;
  
  public IndexFiles() {}

  /** Index all text files under a directory. 
 * @throws Exception */
  public static void main(String fbName) throws Exception {
    String indexPath = "index";
    boolean create = true;
    fbOwnerName = fbName;
    
    final Path docDir = Paths.get(fbOwnerName+docsPath);
    if (!Files.isReadable(docDir)) {
      System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
      System.exit(1);
    }
    
    Date start = new Date();
    try {
      System.out.println("Indexing to directory '" + indexPath + "'...");
      Directory dir = FSDirectory.open(Paths.get(indexPath));
      Analyzer analyzer = new MaxWordAnalyzer();
      IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
      if (!create) {
        // Create a new index in the directory, removing any
        // previously indexed documents:
        iwc.setOpenMode(OpenMode.CREATE);
      } else {
        // Add new documents to an existing index:
        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
      }

      IndexWriter writer = new IndexWriter(dir, iwc);
      indexDocs(writer, docDir);
      
      writer.close();
      Date end = new Date();
      System.out.println(end.getTime() - start.getTime() + " total milliseconds");

      //IndexReader r = DirectoryReader.open(dir);
      Directory readerDir = FSDirectory.open(Paths.get(indexPath));
      //System.out.println("Paths.get(indexPath)="+Paths.get(indexPath));
      DirectoryReader r = DirectoryReader.open(readerDir);
      //System.out.println( r.toString());
      
      r.close();
      
    } catch (IOException e) {
      System.out.println(" caught a " + e.getCause() +
       "\n with message: " + e.getMessage());
    }
  }

  static void indexDocs(final IndexWriter writer, Path path) throws IOException {
    if (Files.isDirectory(path)) {
      Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          try {
            indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
          } catch (IOException ignore) {
            // don't index files that can't be read.
          }
          return FileVisitResult.CONTINUE;
        }
      });
    } else {
      indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
    }
  }

  /** Indexes a single document */
  static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
    try (InputStream stream = Files.newInputStream(file)) {
        handleContents(stream,writer,file,lastModified);
    }
    //showTermsAndFrequency();
  }
  
  static void handleContents(InputStream stream,IndexWriter writer, Path file, long lastModified) throws IOException {
	  BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
	  String read = "";
      
	  try{
	      FileWriter fileWriter = new FileWriter(fbOwnerName+handledContentsPath);
	        
	      while((read = br.readLine())!= null){
	      	  if(read.contains("網址擁有者名:")){
	      	      fbOwnerName = read.replace("網址擁有者名:", "").trim();
	      	  }
	      	  else if(read.contains("網址:")){
	      		  fbUrl = read.replace("網址:", "").trim();
	      	  }
	      	  else{
	      		  if(read.trim()!="" && read.trim()!=null){
	      	          fileWriter.write(read.trim()+"\r\n");
	      		  } 
	      	  }
	      }
	      fileWriter.flush();
	      fileWriter.close();
	      newDocument(writer,file,lastModified);
	      br.close();
	   }catch(Exception e){
	    	e.printStackTrace();
	    }
  }
  
  static void newDocument(IndexWriter writer, Path file, long lastModified)throws IOException {
      Document doc = new Document();
      Field pathField = new StringField("path",file.toString(), Field.Store.YES);
      doc.add(pathField);
      doc.add(new LongField("modified", lastModified, Field.Store.NO));
      doc.add(new StringField("fbName", fbOwnerName, Field.Store.YES));
      doc.add(new StringField("fbUrl", fbUrl, Field.Store.YES));
      try (InputStream contentsStream = new FileInputStream(fbOwnerName+handledContentsPath)) {
          doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(contentsStream, StandardCharsets.UTF_8))));
          
          writer.addDocument(doc);
//          if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
//              //System.out.println("adding " + file);
//              writer.addDocument(doc);
//          } 
//          else {
//              System.out.println("updating " + file);
//              writer.updateDocument(new Term("path", file.toString()), doc);
//          }
      }
  }
}
