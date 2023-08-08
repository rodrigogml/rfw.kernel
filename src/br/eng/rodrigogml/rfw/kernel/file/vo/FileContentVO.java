package br.eng.rodrigogml.rfw.kernel.file.vo;

import java.io.Serializable;

import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaByteArrayField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaRelationshipField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaRelationshipField.RelationshipTypes;
import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: Classe usada para guardar o conte�do do arquivo separado das outras informa��es.<br>
 *
 * @author Rodrigo Leit�o
 * @since 3.2.0 (DEZ / 2009)
 */
public class FileContentVO extends RFWVO implements Serializable {

  private static final long serialVersionUID = 3302186106595703387L;

  /**
   * Defini��es do Arquivo.
   */
  @RFWMetaRelationshipField(caption = "Arquivo", required = true, relationship = RelationshipTypes.PARENT_ASSOCIATION, column = "idk_file")
  private FileVO fileVO = null;

  /**
   * Conte�do do arquivo.
   */
  @RFWMetaByteArrayField(caption = "Conte�do", required = true)
  private byte[] content = null;

  /**
   * Recupera o conte�do do arquivo.
   *
   * @return the conte�do do arquivo
   */
  public byte[] getContent() {
    return content;
  }

  /**
   * Define o conte�do do arquivo.
   *
   * @param content the new conte�do do arquivo
   */
  public void setContent(byte[] content) {
    this.content = content;
  }

  /**
   * Recupera o defini��es do Arquivo.
   *
   * @return the defini��es do Arquivo
   */
  public FileVO getFileVO() {
    return fileVO;
  }

  /**
   * Define o defini��es do Arquivo.
   *
   * @param fileVO the new defini��es do Arquivo
   */
  public void setFileVO(FileVO fileVO) {
    this.fileVO = fileVO;
  }

}
