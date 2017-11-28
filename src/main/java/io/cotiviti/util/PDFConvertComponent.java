/**
 * Copyright (C) 2015 Cotiviti Labs (nexgen.admin@cotiviti.io)
 *
 * The software code contained herein is the property of Cotiviti Corporation
 * and its subsidiaries and affiliates (collectively, “Cotiviti”).
 * Access to this software code is being provided to you in the course of your
 * employment or affiliation with Cotiviti and may be used solely in the scope
 * and course of your work for Cotiviti, and is for internal Cotiviti use only.
 * Any unauthorized use, disclosure, copying, distribution, destruction of this
 * software code, or the taking of any unauthorized action in reliance on this
 * software code, is strictly prohibited.
 * If this information is viewed in error, immediately discontinue use of the
 * application.  Anyone using this software code and the applications will be
 * subject to monitoring for improper use, system maintenance and security
 * purposes, and is advised that if such monitoring reveals possible criminal
 * activity or policy violation, Cotiviti personnel may provide the evidence of
 * such monitoring to law enforcement or other officials, and the user may be
 * subject to disciplinary action by Cotiviti, up to and including termination
 * of employment.
 *
 * Use of this software code and any applications and information therein
 * constitutes acknowledgement of and consent to this notice
 */
package io.cotiviti.util;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import org.codehaus.plexus.component.annotations.Component;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import org.xml.sax.helpers.*;

import com.snowtide.PDF;
import com.snowtide.pdf.Document;
import com.snowtide.pdf.OutputTarget;
import com.snowtide.pdf.Page;
import com.snowtide.pdf.VisualOutputTarget;

import pdfts.examples.XMLOutputTarget;

//import com.itextpdf.text.*;
//import com.itextpdf.text.io.RandomAccessSourceFactory;
//import com.itextpdf.text.pdf.*;
//import com.itextpdf.text.pdf.PRTokeniser.TokenType;
//import com.itextpdf.text.pdf.parser.PdfContentReaderTool;
//import com.itextpdf.text.pdf.util.SmartPdfSplitter;

import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;

/**
 * Uses pdfxstream to transform pdf into formatted text or xml
 * @author mykel.alvis
 *
 */
@Component(hint = "pdf-convert", role = PDFConvertComponent.class, instantiationStrategy = "per-instance", isolatedRealm = true)
public class PDFConvertComponent {
  static StreamResult streamResult;
  static TransformerHandler handler;
  static AttributesImpl atts;

  public static void main(String[] args) throws IOException {

    try {

      File f = new File("target/test-classes/hello.pdf");
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < 9; ++i) {
        sb.append(savePDFXML(f, i));
        sb.append("\n\n\n");
      }
      CotivitiIOUtilities.writeString(Paths.get("target/myout.xml"), sb.toString());
    } catch (

    Exception e) {
      Exception b = e;
    }
  }

  public static void initXML() throws ParserConfigurationException, TransformerConfigurationException, SAXException {
    SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();

    handler = tf.newTransformerHandler();
    Transformer serializer = handler.getTransformer();
    serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
    serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    serializer.setOutputProperty(OutputKeys.INDENT, "yes");
    handler.setResult(streamResult);
    handler.startDocument();
    atts = new AttributesImpl();
    handler.startElement("", "", "Cotiviti", atts);
  }

  public static void process(String s) throws SAXException {
    String[] elements = s.split("\\|");
    atts.clear();
    handler.startElement("", "", "Message", atts);
    handler.characters(elements[0].toCharArray(), 0, elements[0].length());
    handler.endElement("", "", "Message");
  }

  public static void closeXML() throws SAXException {
    handler.endElement("", "", "Cotiviti");
    handler.endDocument();
  }

  public static String savePDFText(File pdfFile, int pageNumber) throws IOException {
    Document pdf = PDF.open(pdfFile);
    Page page = pdf.getPage(pageNumber);
    StringWriter writer = new StringWriter();
    page.pipe(new VisualOutputTarget(writer));
    pdf.close();
    writer.flush();
    writer.close();
    return writer.toString();
  }
  public static String savePDFXML(File pdfFile, int pageNumber) throws IOException {
    XMLOutputTarget target = new XMLOutputTarget();
    Document pdf = PDF.open(pdfFile);
    Page page = pdf.getPage(pageNumber);
    StringWriter writer = new StringWriter();
    page.pipe(target);
    pdf.close();
    writer.flush();
    writer.close();
    return target.getXMLAsString();
  }
}
