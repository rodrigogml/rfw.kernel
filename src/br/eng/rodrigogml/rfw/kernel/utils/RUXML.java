package br.eng.rodrigogml.rfw.kernel.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWCriticalException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.logger.RFWLogger;

/**
 * Description: Esta classe utilit�ria tem a finalidade de agregar m�todos para facilitar a vida ao trabalhar com estruturas de XML.<br>
 *
 * @author Rodrigo Leit�o
 * @since 7.1.0 (11 de out de 2016)
 */
public final class RUXML {

  /**
   * Construtor privado para classe est�tica
   */
  private RUXML() {
  }

  /**
   * Transforma um XML em uma estrutura DOM para navegar entre os valores do XML.<br>
   * Este m�todo normaliza o conte�do do XML, isto �, evita que nos casos em que o XML esteja desta forma:<br>
   * <b>&lt;foo%gt;hello<br>
   * wor<br>
   * ld&lt;/foo&gt;</b><br>
   * <br>
   * Seja interpretado como:<br>
   * <b>Element foo<br>
   * <ul>
   * Text node: ""<br>
   * Text node: "Hello "<br>
   * Text node: "wor"<br>
   * Text node: "ld"<br>
   * </ul>
   * </b> <br>
   * e sim como:<br>
   *
   * <b>Element foo<br>
   * <ul>
   * Text node: "Hello world"
   * </ul>
   * </b>
   *
   * <br>
   * <br>
   * ATEN��O: No caso de m�ltiplos acessos ao mesmo XML, considere criar um �nico documento DOM e passar diretamente nos m�todos ao inv�s de for�ar o sistema a realizar o parser m�ltiplas vezes, atrapalhando a performance do sistema.
   *
   * @param xml Conte�do no formato XML.
   * @return Objeto DOM
   * @throws RFWException
   */
  public static Document parseXMLToDOMDocumentNormalized(String xml) throws RFWException {
    try {
      return parseXMLToDOMDocumentNormalized(xml.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RFWCriticalException("Falha ao realizar o Parser do XML.", e);
    }
  }

  /**
   * Transforma um XML em uma estrutura DOM para navegar entre os valores do XML.<br>
   * Este m�todo normaliza o conte�do do XML, isto �, evita que nos casos em que o XML esteja desta forma:<br>
   * <b>&lt;foo%gt;hello<br>
   * wor<br>
   * ld&lt;/foo&gt;</b><br>
   * <br>
   * Seja interpretado como:<br>
   * <b>Element foo<br>
   * <ul>
   * Text node: ""<br>
   * Text node: "Hello "<br>
   * Text node: "wor"<br>
   * Text node: "ld"<br>
   * </ul>
   * </b> <br>
   * e sim como:<br>
   *
   * <b>Element foo<br>
   * <ul>
   * Text node: "Hello world"
   * </ul>
   * </b>
   *
   * <br>
   * <br>
   * ATEN��O: No caso de m�ltiplos acessos ao mesmo XML, considere criar um �nico documento DOM e passar diretamente nos m�todos ao inv�s de for�ar o sistema a realizar o parser m�ltiplas vezes, atrapalhando a performance do sistema.
   *
   * @param bytes Conte�do no formato XML em bytes no encoding UTF-8.
   * @return Objeto DOM
   * @throws RFWException
   */
  public static Document parseXMLToDOMDocumentNormalized(byte[] bytes) throws RFWException {
    return parseXMLToDOMDocumentNormalized(new ByteArrayInputStream(bytes));
  }

  /**
   * Transforma um XML em uma estrutura DOM para navegar entre os valores do XML.<br>
   * Este m�todo normaliza o conte�do do XML, isto �, evita que nos casos em que o XML esteja desta forma:<br>
   * <b>&lt;foo%gt;hello<br>
   * wor<br>
   * ld&lt;/foo&gt;</b><br>
   * <br>
   * Seja interpretado como:<br>
   * <b>Element foo<br>
   * <ul>
   * Text node: ""<br>
   * Text node: "Hello "<br>
   * Text node: "wor"<br>
   * Text node: "ld"<br>
   * </ul>
   * </b> <br>
   * e sim como:<br>
   *
   * <b>Element foo<br>
   * <ul>
   * Text node: "Hello world"
   * </ul>
   * </b>
   *
   * <br>
   * <br>
   * ATEN��O: No caso de m�ltiplos acessos ao mesmo XML, considere criar um �nico documento DOM e passar diretamente nos m�todos ao inv�s de for�ar o sistema a realizar o parser m�ltiplas vezes, atrapalhando a performance do sistema.
   *
   * @param input
   *
   * @param bytes Conte�do no formato XML em bytes no encoding UTF-8.
   * @return Objeto DOM
   * @throws RFWException
   */
  public static Document parseXMLToDOMDocumentNormalized(InputStream input) throws RFWException {
    final Document doc;
    try {
      doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input, "UTF-8");
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_ERR_200441", e);
    }
    doc.getDocumentElement().normalize();
    return doc;
  }

  /**
   * Este m�todo cria uma �rvore DOM nova a partir "do zero". Que pode ser usada para come�ar a escrita de um novo XML.
   *
   * @return
   * @throws RFWExecption
   */
  public static Document createNewDocument() throws RFWException {
    try {
      DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder icBuilder = icFactory.newDocumentBuilder();
      Document doc = icBuilder.newDocument();
      return doc;
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_ERR_200445", e);
    }
  }

  /**
   * Este m�todo cria uma �rvore DOM nova (um novo documento) a partir de algum n� de outra �rvore DOM.
   *
   * @param externalNode N� da �rvore externa que ser� a tag raiz do novo documento.
   * @return
   * @throws RFWExecption
   */
  public static Document createNewDocument(Node externalNode) throws RFWException {
    final Document newDoc = createNewDocument();
    final Node newNode = newDoc.importNode(externalNode, true);
    newDoc.appendChild(newNode);
    return newDoc;
  }

  /**
   * Cria um novo elemento raiz no XML.
   *
   * @param doc �rvore XML para criar o documento.
   * @param elementName Nome do Elemento (Tag) a ser criado.
   * @return Elemento/Tag criado
   * @throws RFWException
   */
  public static Element createElementTag(Document doc, String elementName) throws RFWException {
    final Element elem = doc.createElement(elementName);
    doc.appendChild(elem);
    return elem;
  }

  /**
   * Cria um novo elemento raiz no XML com uma defini��o de name space (cria o atributo "xmlns").
   *
   * @param doc �rvore XML para criar o documento.
   * @param elementName Nome do Elemento (Tag) a ser criado.
   * @param nameSpace NameSpace a ser atribuido neste elento (atributo 'xmlns')
   * @return Elemento/Tag criado
   * @throws RFWException
   */
  public static Element createElementTag(Document doc, String elementName, String nameSpace) throws RFWException {
    final Element elem = doc.createElement(elementName);
    doc.appendChild(elem);
    addAttribute(elem, "xmlns", nameSpace);
    return elem;
  }

  /**
   * Cria um novo elemento filho no elemento passado.
   *
   * @param doc �rvore XML para criar o documento.
   * @param parentTag Elemento da tag pai, onde o novo elemento ser� adicionado.
   * @param elementName Nome do Elemento (Tag) a ser criado.
   * @return Elemento/Tag criado
   * @throws RFWException
   */
  public static Element createElementTag(Document doc, Element parentTag, String elementName) throws RFWException {
    final Element elem = doc.createElement(elementName);
    parentTag.appendChild(elem);
    return elem;
  }

  /**
   * Cria um elemento j� com um conte�do de texto dentro de suas Tags.
   *
   * @param doc Documento DOM
   * @param parentTag
   * @param elementName
   * @param content
   * @return retorna o elemento filho criado
   * @throws RFWException
   */
  public static Element createElementTagWithTextContent(Document doc, Element parentTag, String elementName, String content) throws RFWException {
    final Element elem = createElementTag(doc, parentTag, elementName);
    if (content != null) elem.appendChild(doc.createTextNode(content));
    return elem;
  }

  /**
   * Define um atributo em uma tag j� existente.
   *
   * @param tag TAG a receber o atributo
   * @param attribute Nome do Atributo
   * @param value Valor do atributo
   * @throws RFWException
   */
  public static void addAttribute(Element tag, String attribute, String value) throws RFWException {
    tag.setAttribute(attribute, value);
  }

  /**
   * Este m�todo recebe um documento DOM e retorna seu conte�do escrito diretamente em uma String.
   *
   * @param doc �rvore DOM
   * @return String com todo o seu conte�do escrito.
   * @throws RFWException
   */
  public static String writeDOMToString(Document doc) throws RFWException {
    return writeDOMToString(doc, false);
  }

  /**
   * Este m�todo recebe um documento DOM e retorna seu conte�do escrito diretamente em uma String.
   *
   * @param doc �rvore DOM
   * @param omitXMLDeclaration Se true, o resultado do XML n�o ter� a dera��o inicial: "<?xml version="1.0" encoding="UTF-8"?>"
   * @return String com todo o seu conte�do escrito.
   * @throws RFWException
   */
  public static String writeDOMToString(Document doc, boolean omitXMLDeclaration) throws RFWException {
    try {
      final Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "no");
      if (omitXMLDeclaration) transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      final DOMSource source = new DOMSource(doc);
      final StringWriter writer = new StringWriter();
      transformer.transform(source, new StreamResult(writer));
      return writer.getBuffer().toString().replaceAll("\n|\r", "");
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_ERR_200446", e);
    }
  }

  /**
   * Este m�todo recebe um n� da �rvore DOM e retorna seu conte�do escrito diretamente em uma String.
   *
   * @param node n� da �rvore DOM
   * @return String com todo o seu conte�do escrito.
   * @throws RFWException
   */
  public static String writeNodeToString(Node node) throws RFWException {
    return writeNodeToString(node, false);
  }

  /**
   * Este m�todo recebe um documento DOM e retorna seu conte�do escrito diretamente em uma String.
   *
   * @param node �rvore DOM
   * @param omitXMLDeclaration Se true, o resultado do XML n�o ter� a dera��o inicial: "<?xml version="1.0" encoding="UTF-8"?>"
   * @return String com todo o seu conte�do escrito.
   * @throws RFWException
   */
  public static String writeNodeToString(Node node, boolean omitXMLDeclaration) throws RFWException {
    try {
      final Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "no");
      if (omitXMLDeclaration) transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      final DOMSource source = new DOMSource(node);
      final StringWriter writer = new StringWriter();
      transformer.transform(source, new StreamResult(writer));
      return writer.getBuffer().toString().replaceAll("\n|\r", "");
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_ERR_200446", e);
    }
  }

  /**
   * Retorna o NodeList da �rvore DOM baseado no caminho fornecido. O caminho deve estar no padr�o XPath.<br>
   * <ul>
   * <li>"/nfeProc/NFe/infNFe/ide/NFref" - Come�e com a barra para indicar desde o objeto raiz;</li>
   * <li>"NFe/infNFe/ide/NFref" - comece sem a barra para indicar a partir do objeto (node) atual.</li>
   * <li>"/CFe/infCFe/ide/nCFe"</li>
   * <li>"/CFe/infCFe[1]/ide[3]/nCFe"</li>
   * <li>"/howto/topic[@name='PowerBuilder']/url[2]/text()"</li>
   * <li>"/howto/topic/url/text/@nItem"</li>
   * </ul>
   * <br>
   *
   * @param doc
   * @param xPath
   * @return NodeList de acordo com o caminho passado.
   * @throws RFWException
   */
  public static NodeList getNodeList(Document doc, String xPath) throws RFWException {
    try {
      return (NodeList) XPathFactory.newInstance().newXPath().evaluate(xPath, doc.getDocumentElement(), XPathConstants.NODESET);
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_ERR_200442", new String[] { xPath }, e);
    }
  }

  /**
   * Retorna o NodeList relativo � um n� da �rvore DOM baseado no caminho fornecido. O caminho deve estar no padr�o XPath.<br>
   * <ul>
   * <li>"/nfeProc/NFe/infNFe/ide/NFref" - Come�e com a barra para indicar desde o objeto raiz;</li>
   * <li>"NFe/infNFe/ide/NFref" - comece sem a barra para indicar a partir do objeto (node) atual.</li>
   * <li>"/CFe/infCFe/ide/nCFe"</li>
   * <li>"/CFe/infCFe[1]/ide[3]/nCFe"</li>
   * <li>"/howto/topic[@name='PowerBuilder']/url[2]/text()"</li>
   * <li>"/howto/topic/url/text/@nItem"</li>
   * </ul>
   * <br>
   * <B>ATEN��O:</B> Node que o caminho relativo n�o come�a com "/", pois isso indica raiz, comece o XPath a partir do n� passado em Node!
   *
   * @param node
   * @param xPath
   * @return NodeList de acordo com o caminho passado.
   * @throws RFWException
   */
  public static NodeList getNodeList(Node node, String xPath) throws RFWException {
    try {
      return (NodeList) XPathFactory.newInstance().newXPath().evaluate(xPath, node, XPathConstants.NODESET);
    } catch (Exception e) {
      throw new RFWCriticalException("RFW_ERR_200442", new String[] { xPath }, e);
    }
  }

  /**
   * Recupera o primeiro n� do caminho.
   *
   * <ul>
   * <li>"/nfeProc/NFe/infNFe/ide/NFref" - Come�e com a barra para indicar desde o objeto raiz;</li>
   * <li>"NFe/infNFe/ide/NFref" - comece sem a barra para indicar a partir do objeto (node) atual.</li>
   * <li>"/CFe/infCFe/ide/nCFe"</li>
   * <li>"/CFe/infCFe[1]/ide[3]/nCFe"</li>
   * <li>"/howto/topic[@name='PowerBuilder']/url[2]/text()"</li>
   * <li>"/howto/topic/url/text/@nItem"</li>
   * </ul>
   *
   * @param node N� de refer�ncia
   * @param xPath Caminho XPath para procurar
   * @return
   * @throws RFWException
   */
  public static Node getFirstNode(Node node, String xPath) throws RFWException {
    NodeList nodeList = getNodeList(node, xPath);
    if (nodeList != null && nodeList.getLength() > 0) return nodeList.item(0);
    return null;
  }

  /**
   * Recupera o primeiro n� do caminho.
   *
   * <ul>
   * <li>"/nfeProc/NFe/infNFe/ide/NFref" - Come�e com a barra para indicar desde o objeto raiz;</li>
   * <li>"NFe/infNFe/ide/NFref" - comece sem a barra para indicar a partir do objeto (node) atual.</li>
   * <li>"/CFe/infCFe/ide/nCFe"</li>
   * <li>"/CFe/infCFe[1]/ide[3]/nCFe"</li>
   * <li>"/howto/topic[@name='PowerBuilder']/url[2]/text()"</li>
   * <li>"/howto/topic/url/text/@nItem"</li>
   * </ul>
   *
   * @param doc �rvore DOM para pesquisa.
   * @param xPath Caminho XPath para procurar
   * @return
   * @throws RFWException
   */
  public static Node getFirstNode(Document doc, String xPath) throws RFWException {
    NodeList nodeList = getNodeList(doc, xPath);
    if (nodeList != null && nodeList.getLength() > 0) return nodeList.item(0);
    return null;
  }

  /**
   * Recupera o conte�do de texto dentro do conte�do de uma TAG do XML baseado no seu xPath.<br>
   * Exemplos de XPath:<br>
   * <ul>
   * <li>"/nfeProc/NFe/infNFe/ide/NFref" - Come�e com a barra para indicar desde o objeto raiz;</li>
   * <li>"NFe/infNFe/ide/NFref" - comece sem a barra para indicar a partir do objeto (node) atual.</li>
   * <li>"/CFe/infCFe/ide/nCFe"</li>
   * <li>"/CFe/infCFe[1]/ide[3]/nCFe"</li>
   * <li>"/howto/topic[@name='PowerBuilder']/url[2]/text()"</li>
   * <li>"/howto/topic/url/text/@nItem"</li>
   * </ul>
   * <br>
   *
   * @param doc
   * @param xPath
   * @return
   * @throws RFWException
   */
  public static String getTextContent(Document doc, String xPath) throws RFWException {
    String value = null;
    final NodeList nodeList = getNodeList(doc, xPath);
    if (nodeList != null && nodeList.getLength() >= 1) {
      value = nodeList.item(0).getTextContent();
    }
    return value;
  }

  /**
   * Recupera o conte�do de texto dentro do conte�do de uma TAG do XML baseado no seu xPath.<br>
   *
   * <ul>
   * <li>"/nfeProc/NFe/infNFe/ide/NFref" - Come�e com a barra para indicar desde o objeto raiz;</li>
   * <li>"NFe/infNFe/ide/NFref" - comece sem a barra para indicar a partir do objeto (node) atual.</li>
   * <li>"/CFe/infCFe/ide/nCFe"</li>
   * <li>"/CFe/infCFe[1]/ide[3]/nCFe"</li>
   * <li>"/howto/topic[@name='PowerBuilder']/url[2]/text()"</li>
   * <li>"/howto/topic/url/text/@nItem"</li>
   * </ul>
   * <br>
   * <B>ATEN��O:</B> Node que o caminho relativo n�o come�a com "/", pois isso indica raiz, comece o XPath a partir do n� passado em Node!
   *
   * @param node
   * @param xPath
   * @return
   * @throws RFWException
   */
  public static String getTextContent(Node node, String xPath) throws RFWException {
    String value = null;
    final NodeList nodeList = getNodeList(node, xPath);
    if (nodeList != null && nodeList.getLength() >= 1) {
      value = nodeList.item(0).getTextContent();
    }
    return value;
  }

  /**
   * L� um XML pronto e importa a tag rais e toda sua estrutura como uma tag filha dentro do documento passado.
   *
   * @param doc Documento DOM j� criado e existente.
   * @param tag Tag pai onde o XML ser� incluso como estrutura filha.
   * @param xml XML a ser importando no documento.
   * @throws RFWException
   */
  public static void createElementTagFromXML(Document doc, Element tag, String xml) throws RFWException {
    final Document newDoc = parseXMLToDOMDocumentNormalized(xml);
    final Node rootElement = newDoc.getFirstChild();
    final Node newNode = doc.importNode(rootElement, true);
    tag.appendChild(newNode);
  }

  /**
   * Importa a tag raiz de outra �rvode DOM e toda sua estrutura como uma tag filha dentro do documento passado.
   *
   * @param doc Documento DOM j� criado e existente.
   * @param tag Tag pai onde o XML ser� incluso como estrutura filha.
   * @param childDoc �rvore DOM a ser importada como tag filha.
   * @throws RFWException
   */
  public static void createElementTagFromDOMRoot(Document doc, Element tag, Document childDoc) throws RFWException {
    final Node rootElement = childDoc.getFirstChild();
    final Node newNode = doc.importNode(rootElement, true);
    tag.appendChild(newNode);
  }

  /**
   * Importa um elemento de outra �rvore DOM e toda sua estrutura como uma tag filha dentro do documento passado.
   *
   * @param doc Documento DOM j� criado e existente.
   * @param tag Tag pai onde o XML ser� incluso como estrutura filha.
   * @param newElement Elemento da outra �rvore que deve ser importado como tag filha.
   * @throws RFWException
   */
  public static void createElementTagFromExternalNode(Document doc, Node tag, Node newElement) throws RFWException {
    final Node newNode = doc.importNode(newElement, true);
    tag.appendChild(newNode);
  }

  /**
   * Este m�todo faz a valida��o de um XML contra seu Schema (XSD).
   *
   * @param xml Conte�do do XML que ser� validado.
   * @param schemapath Caminho para o XSD de acordo com o caminho passado no atributo basePath (ou somente o nome do arquivo, caso ele j� esteja na pasta do basePath)
   * @param basepath Caminho base para os XSD de valida��o. �til quando o XML faz refer�ncia para outros XSD ao inv�s de ter todo o schema dentro do mesmo arquivo.
   * @throws RFWException
   */
  public static void validateXMLAgainstSchema2(String xml, String schemapath, String basepath) throws RFWException {
    try {
      InputStream in = RUXML.class.getResourceAsStream(basepath + schemapath); // L� durante o deploy "unexploded" do ear no eclipse (produ��o)
      if (in == null) {
        in = RUXML.class.getClassLoader().getResourceAsStream(basepath + schemapath); // L� durante o deploy "exploded" do ear no eclipse (desenvolvimento)
      }

      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      factory.setResourceResolver(new SchemaResourceResolver(basepath));
      Schema schema = factory.newSchema(new StreamSource(in));
      Validator validator = schema.newValidator();

      validator.validate(new StreamSource(new StringReader(xml)));
    } catch (SAXException e) {
      throw new RFWCriticalException("RFW_ERR_200486", e);
    } catch (IOException e) {
      throw new RFWCriticalException("RFW_ERR_200487", e);
    }
  }
}

/**
 * Description: Esta classe carrega os XSDs conforme o validador necessita. Foi necess�rio implementar essa classe para buscar os XSD importados pelos outros XSDs. J� que o validador n�o encontra eles sozinhos dentro do JAR.<br>
 *
 * @author Rodrigo Leit�o
 * @since 4.0.0 (27/01/2011)
 */
class SchemaResourceResolver implements LSResourceResolver {

  /**
   * Define o caminho base para encontrar os XSD dependentes.
   */
  private String basepath = null;

  public SchemaResourceResolver(String basepath) {
    super();
    if (basepath == null) {
      basepath = "";
    } else {
      this.basepath = basepath;
    }
  }

  @Override
  public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemid, String baseURI) {
    InputStream in = getClass().getResourceAsStream(this.basepath + systemid); // Como l� no ambiente de produ��o (Deploy em .EAR)
    if (in == null) {
      in = getClass().getClassLoader().getResourceAsStream(this.basepath + systemid); // Como L� no ambiente de desenvolvimento (Deploy em diret�rio)
    }
    return new LSInputImpl(publicId, systemid, in);
  }

  protected class LSInputImpl implements LSInput {
    private String publicId;
    private String systemId;

    private BufferedInputStream inputStream;

    public LSInputImpl(String publicId, String sysId, InputStream input) {
      this.publicId = publicId;
      this.systemId = sysId;
      this.inputStream = new BufferedInputStream(input);
    }

    @Override
    public String getPublicId() {
      return publicId;
    }

    @Override
    public void setPublicId(String publicId) {
      this.publicId = publicId;
    }

    @Override
    public String getBaseURI() {
      return null;
    }

    @Override
    public InputStream getByteStream() {
      return null;
    }

    @Override
    public boolean getCertifiedText() {
      return false;
    }

    @Override
    public Reader getCharacterStream() {
      return null;
    }

    @Override
    public String getEncoding() {
      return null;
    }

    @Override
    public String getStringData() {
      synchronized (inputStream) {
        try {
          byte[] input = new byte[inputStream.available()];
          inputStream.read(input);
          String contents = new String(input);
          return contents.replaceAll("﻿", "");
        } catch (IOException e) {
          RFWLogger.logException(e);
          return null;
        } finally {
          try {
            inputStream.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }

    @Override
    public void setBaseURI(String baseURI) {
    }

    @Override
    public void setByteStream(InputStream byteStream) {
    }

    @Override
    public void setCertifiedText(boolean certifiedText) {
    }

    @Override
    public void setCharacterStream(Reader characterStream) {
    }

    @Override
    public void setEncoding(String encoding) {
    }

    @Override
    public void setStringData(String stringData) {
    }

    @Override
    public String getSystemId() {
      return systemId;
    }

    @Override
    public void setSystemId(String systemId) {
      this.systemId = systemId;
    }

    public BufferedInputStream getInputStream() {
      return inputStream;
    }

    public void setInputStream(BufferedInputStream inputStream) {
      this.inputStream = inputStream;
    }
  }

}
